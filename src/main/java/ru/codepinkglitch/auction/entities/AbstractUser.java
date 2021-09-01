package ru.codepinkglitch.auction.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

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
    @Cascade(CascadeType.ALL)
    private BillingDetailsEntity billingDetails;

    @OneToMany
    private List<CommissionEntity> commissions;

    @OneToOne
    @Cascade(CascadeType.ALL)
    private MyUserDetails userDetails;

    private String name;
    private String surname;
    private String email;

    public void update(AbstractUser abstractUser){
        if(billingDetails != null) {
            this.billingDetails = abstractUser.getBillingDetails();
        }
        if(userDetails != null) {
            this.userDetails = abstractUser.getUserDetails();
        }
        if(name != null) {
            this.name = abstractUser.getName();
        }
        if(surname != null) {
            this.surname = abstractUser.getSurname();
        }
        if(email != null) {
            this.email = abstractUser.getEmail();
        }
    }
}
