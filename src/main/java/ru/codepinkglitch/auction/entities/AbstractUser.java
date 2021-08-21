package ru.codepinkglitch.auction.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private BillingDetailsEntity billingDetails;

    @OneToMany
    private List<CommissionEntity> commissions;

    private String username;
    private String name;
    private String surname;
    private String email;
}