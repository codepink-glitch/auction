package ru.codepinkglitch.auction.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class BuyerEntity extends AbstractUser {
    {
        setRole(Role.BUYER);
    }
    @OneToMany
    private List<BidEntity> bids;

}
