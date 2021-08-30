package ru.codepinkglitch.auction.dtos.out;

import lombok.Data;

@Data
public class ArtistOut {
    private Long id;
    private String username;
    private String email;
    private String description;
}
