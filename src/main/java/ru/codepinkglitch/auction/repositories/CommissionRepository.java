package ru.codepinkglitch.auction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.codepinkglitch.auction.entities.CommissionEntity;


public interface CommissionRepository extends JpaRepository<CommissionEntity, Long> {

}
