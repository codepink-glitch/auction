package ru.codepinkglitch.auction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.codepinkglitch.auction.entities.CommissionEntity;

import java.util.List;

public interface CommissionRepository extends JpaRepository<CommissionEntity, Long> {
    List<CommissionEntity> findCommissionEntityByTagsContains(String tag);

}
