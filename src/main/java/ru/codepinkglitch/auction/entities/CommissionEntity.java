package ru.codepinkglitch.auction.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Data
public class CommissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Status status;
    private Calendar publishDate;
    private Calendar closingDate;
    private String uri;

    @ElementCollection
    private List<String> tags = new ArrayList<>();

    @OneToOne
    private ArtistEntity author;

    @OneToMany
    @Cascade(CascadeType.ALL)
    private List<BidEntity> bids;
}
