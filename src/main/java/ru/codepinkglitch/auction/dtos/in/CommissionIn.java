package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;
import ru.codepinkglitch.auction.entities.Status;

@Data
public class CommissionIn {

    private Long id;
    private Status status;
    private ArtistIn author;
    private BidIn bid;
}
