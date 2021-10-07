package ru.codepinkglitch.auction.dtos.out;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArtistOut extends AbstractUserOut{
    private String description;
}
