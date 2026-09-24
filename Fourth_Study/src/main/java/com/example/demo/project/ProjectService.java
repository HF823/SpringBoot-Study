package com.example.demo.project;

import com.example.demo.common.BusinessException;
import com.example.demo.common.ErrorCode;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    private Long currentUserId(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND, "用户不存在"));
    }

    /** 返回 DTO 分页 */
    public Page<ProjectResponse> listMyProjects(String username, int page, int size) {
        if (size > 100) size = 100;
        Long ownerId = currentUserId(username);
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "id"));
        return projectRepository.findByOwnerId(ownerId, pageable)
                .map(ProjectResponse::from);      // ← 关键：实体转 DTO
    }

    /** 返回 DTO */
    public ProjectResponse getMyProject(Long id, String username) {
        Long ownerId = currentUserId(username);
        return projectRepository.findByIdAndOwnerId(id, ownerId)
                .map(ProjectResponse::from)       // ← 关键
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND, "项目不存在"));
    }

    @Transactional
    public ProjectResponse create(String username, CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setOwnerId(currentUserId(username));
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse update(Long id, String username,
                                  CreateProjectRequest request) {
        Long ownerId = currentUserId(username);
        Project project = projectRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND, "项目不存在"));
        project.setName(request.name());
        project.setDescription(request.description());
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public void delete(Long id, String username) {
        Long ownerId = currentUserId(username);
        if (!projectRepository.existsByIdAndOwnerId(id, ownerId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "项目不存在");
        }
        projectRepository.deleteById(id);
    }
}