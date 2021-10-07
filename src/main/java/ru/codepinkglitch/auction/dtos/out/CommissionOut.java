package ru.codepinkglitch.auction.dtos.out;

import lombok.Data;
import ru.codepinkglitch.auction.enums.Status;

import java.util.Set;

@Data
public class CommissionOut {

    private Long id;
    private Status status;
    private String publishDate;
    private String closingDate;
    private boolean previewPicturePresent;
    private boolean finishedPicturePresent;
    private Set<String> tags;
    private InsideCommissionArtist author;
    private BidOut bid;
}
