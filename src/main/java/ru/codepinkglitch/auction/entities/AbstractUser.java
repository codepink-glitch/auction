package ru.codepinkglitch.auction.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private BillingDetailsEntity billingDetails;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private MyUserDetails userDetails;

    private String email;


}
