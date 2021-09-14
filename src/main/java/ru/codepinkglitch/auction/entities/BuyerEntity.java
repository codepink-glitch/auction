package ru.codepinkglitch.auction.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.List;


@Data
@Entity
public class BuyerEntity extends AbstractUser {

    @OneToMany
    @Cascade(CascadeType.ALL)
    private List<BidEntity> bids;


}
