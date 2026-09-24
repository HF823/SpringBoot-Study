package com.example.demo.task;

import com.example.demo.common.BusinessException;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.PageResult;
import com.example.demo.project.Project;
import com.example.demo.project.ProjectRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    private Long currentUserId(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND, "用户不存在"));
    }

    /** 校验：当前用户是否拥有这个项目 */
    private Project ownProject(Long projectId, String username) {
        Long ownerId = currentUserId(username);
        return projectRepository.findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND, "项目不存在"));
    }

    /** 校验：当前用户是否拥有这个任务（= 任务所属项目是不是你的） */
    private Task ownTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND, "任务不存在"));
        ownProject(task.getProjectId(), username);   // ← 关键：顺着链条校验
        return task;
    }

    /** 任务列表：分页 + 状态过滤 + 关键词搜索 */
    public PageResult<TaskResponse> listTasks(String username, Long projectId,
                                              TaskStatus status, String keyword,
                                              int page, int size) {
        ownProject(projectId, username);
        if (size > 100) size = 100;
        PageRequest pageable = PageRequest.of(page, size);
        return PageResult.of(
                taskRepository.search(projectId, status, keyword, pageable),
                TaskResponse::from);
    }

    /** 查单个（带归属校验） */
    public TaskResponse getTask(Long taskId, String username) {
        return TaskResponse.from(ownTask(taskId, username));
    }

    /** 新建任务 */
    @Transactional
    public TaskResponse create(String username, Long projectId,
                               CreateTaskRequest request) {
        Project project = ownProject(projectId, username);   // ← 先校验
        Task task = new Task();
        task.setProjectId(project.getId());
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(TaskStatus.TODO);
        task.setPriority(request.priority() == null ? 3 : request.priority());
        task.setDueDate(request.dueDate());
        return TaskResponse.from(taskRepository.save(task));
    }

    /** 更新任务 */
    @Transactional
    public TaskResponse update(Long taskId, String username,
                               UpdateTaskRequest request) {
        Task task = ownTask(taskId, username);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority() == null ? task.getPriority()
                : request.priority());
        task.setDueDate(request.dueDate());
        return TaskResponse.from(taskRepository.save(task));
    }

    /** 切换状态 */
    @Transactional
    public TaskResponse toggleStatus(Long taskId, String username) {
        Task task = ownTask(taskId, username);
        task.setStatus(switch (task.getStatus()) {
            case TODO -> TaskStatus.DOING;
            case DOING -> TaskStatus.DONE;
            case DONE -> TaskStatus.TODO;
        });
        return TaskResponse.from(taskRepository.save(task));
    }

    /** 删除任务 */
    @Transactional
    public void delete(Long taskId, String username) {
        Task task = ownTask(taskId, username);
        taskRepository.delete(task);
    }
}