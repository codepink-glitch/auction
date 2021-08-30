package ru.codepinkglitch.auction.dtos.out;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BidOut {
    private Long id;
    private BigDecimal amount;
    private String buyerUsername;
}
