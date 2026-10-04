package com.sorosoro.fabric.repository;

import com.sorosoro.fabric.domain.Fabric;
import com.sorosoro.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FabricRepository
        extends JpaRepository<Fabric, Long>,
                org.springframework.data.jpa.repository.JpaSpecificationExecutor<Fabric> {

    List<Fabric> findByUserOrderByCreatedAtDesc(User user);

    List<Fabric> findByUserAndStoreName(User user, String storeName);
}
