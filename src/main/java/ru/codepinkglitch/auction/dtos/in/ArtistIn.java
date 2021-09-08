package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;

import java.util.List;

@Data
public class ArtistIn extends AbstractUserIn {

    private List<Long> commissionsIds;
    private String description;
}
