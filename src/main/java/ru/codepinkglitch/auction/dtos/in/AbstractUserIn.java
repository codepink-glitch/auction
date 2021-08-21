package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;

import java.util.List;

@Data
public abstract class AbstractUserIn {

    private Long id;
    private BillingDetailsIn billingDetails;
    private List<CommissionIn> commissions;
    private String username;
    private String name;
    private String surname;
    private String email;
}
