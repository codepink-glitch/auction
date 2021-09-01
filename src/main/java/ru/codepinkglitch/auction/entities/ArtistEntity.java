package ru.codepinkglitch.auction.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ArtistEntity extends AbstractUser{

    private String description;

    public void update(ArtistEntity artistEntity){
        super.update(artistEntity);
        if(description != null){
            this.description = artistEntity.getDescription();
        }
    }
}
