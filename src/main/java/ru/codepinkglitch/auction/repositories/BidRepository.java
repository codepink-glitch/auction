package ru.codepinkglitch.auction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.codepinkglitch.auction.entities.BidEntity;

public interface BidRepository extends JpaRepository<BidEntity, Long> {
}
