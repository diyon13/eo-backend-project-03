package com.example.prompt.repository;

import com.example.prompt.domain.UserEntity;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserid(String userid);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByUserid(String userid);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByProviderAndProviderId(String provider, String providerId);


}
