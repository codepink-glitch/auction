package ru.codepinkglitch.auction.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class BuyerEntity extends AbstractUser {

    @OneToMany
    private List<BidEntity> bids;



}
