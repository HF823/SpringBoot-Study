package com.example.demo.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
        SELECT t FROM Task t
        WHERE t.projectId = :projectId
          AND (:status IS NULL OR t.status = :status)
          AND (:keyword IS NULL OR t.title LIKE %:keyword%)
        ORDER BY t.id DESC
    """)
    Page<Task> search(@Param("projectId") Long projectId,
                      @Param("status") TaskStatus status,
                      @Param("keyword") String keyword,
                      Pageable pageable);

    long countByProjectId(Long projectId);

    void deleteByProjectId(Long projectId);
}