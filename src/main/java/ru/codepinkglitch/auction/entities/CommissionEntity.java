package ru.codepinkglitch.auction.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.codepinkglitch.auction.enums.Status;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class CommissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Status status;
    private LocalDateTime publishDate;
    private LocalDateTime closingDate;

    @Lob
    private String previewPicture;

    @Lob
    private String finishedPicture;

    @ElementCollection
    private Set<String> tags = new HashSet<>();

    @OneToOne
    private ArtistEntity author;

    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @Cascade(CascadeType.ALL)
    private List<BidEntity> bids;

}
