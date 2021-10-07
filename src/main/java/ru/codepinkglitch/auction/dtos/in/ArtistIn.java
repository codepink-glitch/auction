package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArtistIn extends AbstractUserIn {

    private List<Long> commissionsIds;
    private String description;
}
