package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;


@Data
public abstract class AbstractUserIn {

    private Long id;
    private BillingDetailsIn billingDetails;
    private Long userDetailsId;
    private String username;
    private String password;
    private String email;

}
