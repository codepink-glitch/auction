package ru.codepinkglitch.auction.dtos.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommissionWrapper {
    private List<String> tags;
    private BigDecimal minimalBid;
    private Integer daysPeriod;
    private Integer hoursPeriod;
    private Integer minutesPeriod;
}
