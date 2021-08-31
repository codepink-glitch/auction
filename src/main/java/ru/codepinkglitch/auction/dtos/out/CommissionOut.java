package ru.codepinkglitch.auction.dtos.out;

import lombok.Data;
import ru.codepinkglitch.auction.entities.Status;

import java.util.List;

@Data
public class CommissionOut {

    private Long id;
    private Status status;
    private String publishDate;
    private String closingDate;
    private String uri;
    private List<String> tags;
    private ArtistOut author;
    private BidOut bid;
}
