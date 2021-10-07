package ru.codepinkglitch.auction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.codepinkglitch.auction.entities.CommissionEntity;

import java.util.Set;


public interface CommissionRepository extends JpaRepository<CommissionEntity, Long> {
    Set<CommissionEntity> findByTagsIgnoreCase(String tag);
}
