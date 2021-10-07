package ru.codepinkglitch.auction.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private BillingDetailsEntity billingDetails;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private MyUserDetails userDetails;

    private String email;


}
