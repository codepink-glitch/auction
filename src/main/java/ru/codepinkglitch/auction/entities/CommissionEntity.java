package ru.codepinkglitch.auction.entities;

import javax.persistence.*;

@Entity
public class CommissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Status status;

    @OneToOne
    private ArtistEntity author;

    @OneToOne
    private BidEntity bid;
}
