package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;
import ru.codepinkglitch.auction.entities.Status;

import java.util.Calendar;
import java.util.List;

@Data
public class CommissionIn {

    private Long id;
    private Status status;
    private Calendar publishDate;
    private Calendar closingDate;
    private String uri;
    private List<String> tags;
    private ArtistIn author;
    private List<BidIn> bids;

    public void update(CommissionIn commissionIn){
        if(commissionIn.getId() != null)
            this.id = commissionIn.getId();
        if(commissionIn.getStatus() != null)
            this.status = commissionIn.getStatus();
        if(commissionIn.getPublishDate() != null)
            this.publishDate = commissionIn.getPublishDate();
        if(commissionIn.getUri() != null)
            this.uri = commissionIn.getUri();
        if(commissionIn.getTags() != null)
            this.tags = commissionIn.getTags();
        if(commissionIn.getAuthor() != null)
            this.author = commissionIn.getAuthor();
        if(commissionIn.getBids() != null)
            this.bids = commissionIn.getBids();
    }
}
