package com.sorosoro.fabric.repository;

import com.sorosoro.fabric.domain.Fabric;
import com.sorosoro.fabric.domain.ProjectFabric;
import com.sorosoro.project.domain.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectFabricRepository extends JpaRepository<ProjectFabric, Long> {

    List<ProjectFabric> findByProject(Project project);

    List<ProjectFabric> findByFabric(Fabric fabric);

    Optional<ProjectFabric> findByProjectAndFabric(Project project, Fabric fabric);
}
