package com.sorosoro.project.repository;

import com.sorosoro.project.domain.Project;
import com.sorosoro.project.domain.ProjectStatus;
import com.sorosoro.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByUserAndStatus(User user, ProjectStatus status);
}
