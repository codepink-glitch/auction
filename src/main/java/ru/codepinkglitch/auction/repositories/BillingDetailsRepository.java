package ru.codepinkglitch.auction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.codepinkglitch.auction.entities.BillingDetailsEntity;

public interface BillingDetailsRepository extends JpaRepository<BillingDetailsEntity, Long> {
}
