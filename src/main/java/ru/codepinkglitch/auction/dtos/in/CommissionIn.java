package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;
import ru.codepinkglitch.auction.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommissionIn {

    private Long id;
    private Status status;
    private LocalDateTime publishDate;
    private LocalDateTime closingDate;
    private boolean previewPicturePresent;
    private boolean finishedPicturePresent;
    private List<String> tags;
    private ArtistIn author;
    private List<BidIn> bids;
}
