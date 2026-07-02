package com.sorosoro.project.repository;

import com.sorosoro.project.domain.ProjectReference;
import com.sorosoro.project.domain.ProjectSpecification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectReferenceRepository extends JpaRepository<ProjectReference, Long> {

    List<ProjectReference> findByProjectSpecificationOrderBySortOrderAsc(ProjectSpecification projectSpecification);
}
