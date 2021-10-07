package ru.codepinkglitch.auction.entities;

import lombok.Data;

import javax.persistence.*;

@Embeddable
@Data
public class BillingDetailsEntity {

    private String name;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String ccNumber;
    private String ccExpiration;
    private String ccCVV;
}
