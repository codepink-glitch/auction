package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;

import java.util.List;

@Data
public abstract class AbstractUserIn {

    private Long id;
    private BillingDetailsIn billingDetails;
    private List<Long> commissionsIds;
    private Long userDetailsId;
    private String username;
    private String password;
    private String email;

}
