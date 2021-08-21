package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;
import ru.codepinkglitch.auction.entities.BidStatus;

import java.math.BigDecimal;

@Data
public class BidIn {

    private Long id;
    private BigDecimal amount;
    private BidStatus bidStatus;
    private CommissionIn commission;
    private BuyerIn buyer;

}
