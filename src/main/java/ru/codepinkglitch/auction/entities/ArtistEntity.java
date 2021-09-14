package ru.codepinkglitch.auction.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class ArtistEntity extends AbstractUser{

    private String description;

    @OneToMany
    private List<CommissionEntity> commissions;

}
