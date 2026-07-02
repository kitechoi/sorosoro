package com.sorosoro.project.repository;

import com.sorosoro.project.domain.Project;
import com.sorosoro.project.domain.ProjectSpecification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectSpecificationRepository extends JpaRepository<ProjectSpecification, Long> {

    Optional<ProjectSpecification> findByProject(Project project);
}
