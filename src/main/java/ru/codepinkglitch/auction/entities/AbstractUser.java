package ru.codepinkglitch.auction.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Data
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private BillingDetailsEntity billingDetails;

    @OneToMany
    private List<CommissionEntity> commissions;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private MyUserDetails userDetails;

    private String email;

    public void update(AbstractUser abstractUser){
        if(billingDetails != null) {
            this.billingDetails = abstractUser.getBillingDetails();
        }
        if(userDetails != null) {
            this.userDetails = abstractUser.getUserDetails();
        }
        if(email != null) {
            this.email = abstractUser.getEmail();
        }
    }
}
