package ru.codepinkglitch.auction.dtos.out;

import lombok.Data;
import ru.codepinkglitch.auction.dtos.in.BillingDetailsIn;

@Data
public abstract class AbstractUserOut {
    private Long id;
    private BillingDetailsIn billingDetails;
    private String username;
    private String email;
}
