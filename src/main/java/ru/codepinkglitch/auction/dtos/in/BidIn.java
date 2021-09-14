package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;
import ru.codepinkglitch.auction.enums.BidStatus;

import java.math.BigDecimal;

@Data
public class BidIn {

    private Long id;
    private BigDecimal amount;
    private BidStatus bidStatus;
    private Long commissionId;
    private Long buyerId;

}
