package ru.codepinkglitch.auction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.codepinkglitch.auction.entities.BuyerEntity;
import ru.codepinkglitch.auction.entities.MyUserDetails;

public interface BuyerRepository extends JpaRepository<BuyerEntity, Long> {
    BuyerEntity findBuyerEntityByUserDetails(MyUserDetails myUserDetails);
}
