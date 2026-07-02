package com.sorosoro.fabric.repository;

import com.sorosoro.fabric.domain.Fabric;
import com.sorosoro.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FabricRepository extends JpaRepository<Fabric, Long> {

    List<Fabric> findByUserOrderByCreatedAtDesc(User user);

    List<Fabric> findByUserAndStoreName(User user, String storeName);
}
