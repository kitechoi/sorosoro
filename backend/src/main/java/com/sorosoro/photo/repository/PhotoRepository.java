package com.sorosoro.photo.repository;

import com.sorosoro.photo.domain.Photo;
import com.sorosoro.photo.domain.PhotoOwnerType;
import com.sorosoro.photo.domain.PhotoStatus;
import com.sorosoro.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByOwnerTypeAndOwnerIdOrderBySortOrderAsc(PhotoOwnerType ownerType, Long ownerId);

    List<Photo> findByUserAndStatus(User user, PhotoStatus status);
}
