package ru.codepinkglitch.auction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.codepinkglitch.auction.entities.BidEntity;

import java.util.List;

public interface BidRepository extends JpaRepository<BidEntity, Long> {
    List<BidEntity> findByIdIn(List<Long> id);
}
