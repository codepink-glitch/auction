package ru.codepinkglitch.auction.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class BidEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private BidStatus bidStatus;

    @OneToOne
    private CommissionEntity commission;

    @ManyToOne
    private BuyerEntity buyer;


}
