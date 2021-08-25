package ru.codepinkglitch.auction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.codepinkglitch.auction.entities.BuyerEntity;

public interface BuyerRepository extends JpaRepository<BuyerEntity, Long> {
    BuyerEntity findBuyerEntityByUsername(String username);
    Boolean existsBuyerEntityByUsername(String username);
}
