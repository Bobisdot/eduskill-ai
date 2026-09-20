package com.eduskill.eduskill_ai.repository;
import com.eduskill.eduskill_ai.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Пайдаланушыны email арқылы іздеу әдісі
    Optional<User> findByEmail(String email);

    // Осындай email базада бар-жоғын тексеру
    boolean existsByEmail(String email);
}