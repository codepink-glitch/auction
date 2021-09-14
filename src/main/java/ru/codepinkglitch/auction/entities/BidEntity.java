package ru.codepinkglitch.auction.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import ru.codepinkglitch.auction.enums.BidStatus;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
public class BidEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private BidStatus bidStatus;

    @OneToOne
    @Cascade(CascadeType.ALL)
    private CommissionEntity commission;

    @ManyToOne
    @Cascade(CascadeType.ALL)
    private BuyerEntity buyer;


}
