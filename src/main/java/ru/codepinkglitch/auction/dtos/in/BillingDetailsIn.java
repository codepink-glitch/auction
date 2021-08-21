package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;

@Data
public class BillingDetailsIn {

    private Long id;

    private String name;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String ccNumber;
    private String ccExpiration;
    private String ccCVV;
}
