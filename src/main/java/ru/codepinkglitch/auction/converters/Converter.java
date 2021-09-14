package ru.codepinkglitch.auction.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.codepinkglitch.auction.dtos.in.*;
import ru.codepinkglitch.auction.dtos.out.ArtistOut;
import ru.codepinkglitch.auction.dtos.out.BidOut;
import ru.codepinkglitch.auction.dtos.out.CommissionOut;
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.enums.BidStatus;
import ru.codepinkglitch.auction.enums.Role;
import ru.codepinkglitch.auction.repositories.BidRepository;
import ru.codepinkglitch.auction.repositories.BuyerRepository;
import ru.codepinkglitch.auction.repositories.CommissionRepository;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class Converter {

    private final BidRepository bidRepository;
    private final CommissionRepository commissionRepository;
    private final BuyerRepository buyerRepository;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public BuyerEntity buyerFromDto(BuyerIn buyer){
        BuyerEntity buyerEntity = new BuyerEntity();
        buyerEntity.setBids(bidRepository.findByIdIn(buyer.getBidsIds()));
        buyerEntity.setId(buyer.getId());
        buyerEntity.setUserDetails(new MyUserDetails(Collections.singletonList(new MyAuthority(Role.BUYER.name())),
                buyer.getPassword(), buyer.getUsername(), buyer.getUserDetailsId()));
        buyerEntity.setEmail(buyer.getEmail());
        buyerEntity.setBillingDetails(detailsFromDto(buyer.getBillingDetails()));
        return buyerEntity;
    }

    public BuyerIn buyerToDto(BuyerEntity buyer){
        BuyerIn buyerIn = new BuyerIn();
        buyerIn.setBidsIds(buyer.getBids()
                .stream()
                .map(BidEntity::getId)
                .collect(Collectors.toList())
        );
        buyerIn.setId(buyer.getId());
        buyerIn.setUsername(buyer.getUserDetails().getUsername());
        buyerIn.setPassword(buyer.getUserDetails().getPassword());
        buyerIn.setUserDetailsId(buyer.getUserDetails().getId());
        buyerIn.setEmail(buyer.getEmail());
        buyerIn.setBillingDetails(detailsToDto(buyer.getBillingDetails()));
        return buyerIn;
    }


    public BillingDetailsIn detailsToDto(BillingDetailsEntity billingDetailsEntity){
        return objectMapper.convertValue(billingDetailsEntity, BillingDetailsIn.class);
    }


    public BillingDetailsEntity detailsFromDto(BillingDetailsIn billingDetailsIn){
        return objectMapper.convertValue(billingDetailsIn, BillingDetailsEntity.class);
    }

    public ArtistIn artistToDto(ArtistEntity artistEntity){
        ArtistIn artistIn = new ArtistIn();
        artistIn.setId(artistEntity.getId());
        artistIn.setEmail(artistEntity.getEmail());
        artistIn.setBillingDetails(detailsToDto(artistEntity.getBillingDetails()));
        artistIn.setUsername(artistEntity.getUserDetails().getUsername());
        artistIn.setPassword(artistEntity.getUserDetails().getPassword());
        artistIn.setUserDetailsId(artistEntity.getUserDetails().getId());
        artistIn.setCommissionsIds(artistEntity.getCommissions()
                .stream()
                .map(CommissionEntity::getId)
                .collect(Collectors.toList())
        );
        artistIn.setDescription(artistEntity.getDescription());
        return artistIn;
    }

    public ArtistEntity artistFromDto(ArtistIn artistIn){
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setId(artistIn.getId());
        artistEntity.setEmail(artistIn.getEmail());
        artistEntity.setBillingDetails(detailsFromDto(artistIn.getBillingDetails()));
        artistEntity.setUserDetails(new MyUserDetails(Collections.singletonList(new MyAuthority(Role.ARTIST.name())), artistIn.getPassword(), artistIn.getUsername(), artistIn.getUserDetailsId()));
        artistEntity.setCommissions(artistIn.getCommissionsIds()
                .stream()
                .map(commissionRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList())
        );
        artistEntity.setDescription(artistIn.getDescription());
        return artistEntity;
    }

    public BidIn bidToDto(BidEntity bidEntity){
        BidIn bidIn = new BidIn();
        bidIn.setId(bidEntity.getId());
        bidIn.setAmount(bidEntity.getAmount());
        bidIn.setBidStatus(bidEntity.getBidStatus());
        bidIn.setCommissionId(bidEntity.getCommission().getId());
        bidIn.setBuyerId(bidEntity.getBuyer().getId());
        return bidIn;
    }

    public BidEntity bidFromDto(BidIn bidIn){
        BidEntity bidEntity = new BidEntity();
        bidEntity.setId(bidIn.getId());
        bidEntity.setAmount(bidIn.getAmount());
        bidEntity.setBidStatus(bidIn.getBidStatus());
        bidEntity.setCommission(commissionRepository.findById(bidIn.getId()).orElse(null));
        bidEntity.setBuyer(buyerRepository.findById(bidIn.getBuyerId()).orElse(null));
        return bidEntity;
    }

    public CommissionIn commissionToDto(CommissionEntity commissionEntity){
        CommissionIn commissionIn = new CommissionIn();
        commissionIn.setId(commissionEntity.getId());
        commissionIn.setStatus(commissionEntity.getStatus());
        commissionIn.setPublishDate(commissionEntity.getPublishDate());
        commissionIn.setClosingDate(commissionEntity.getClosingDate());
        commissionIn.setUri(commissionEntity.getPreviewPicture());
        commissionIn.setTags(commissionEntity.getTags());
        commissionIn.setAuthor(artistToDto(commissionEntity.getAuthor()));
        commissionIn.setBids(commissionEntity.getBids().stream()
                .map(this::bidToDto)
                .collect(Collectors.toList()));
        return commissionIn;
    }

    public CommissionEntity commissionFromDto(CommissionIn commissionIn){
        CommissionEntity commissionEntity = new CommissionEntity();
        commissionEntity.setId(commissionIn.getId());
        commissionEntity.setStatus(commissionIn.getStatus());
        commissionEntity.setPublishDate(commissionIn.getPublishDate());
        commissionEntity.setClosingDate(commissionIn.getClosingDate());
        commissionEntity.setPreviewPicture(commissionIn.getUri());
        commissionEntity.setTags(commissionIn.getTags());
        commissionEntity.setAuthor(artistFromDto(commissionIn.getAuthor()));
        commissionEntity.setBids(commissionIn.getBids().stream()
                .map(this::bidFromDto)
                .collect(Collectors.toList()));
        return commissionEntity;
    }

    public CommissionOut commissionToOut(CommissionEntity commissionEntity){
        CommissionOut commissionOut = new CommissionOut();
        commissionOut.setId(commissionEntity.getId());
        commissionOut.setStatus(commissionEntity.getStatus());
        commissionOut.setPublishDate(commissionEntity.getPublishDate().format(formatter));
        commissionOut.setClosingDate(commissionEntity.getClosingDate().format(formatter));
        commissionOut.setUri(commissionEntity.getPreviewPicture());
        commissionOut.setTags(commissionEntity.getTags());
        commissionOut.setAuthor(artistToOut(commissionEntity.getAuthor()));
        // TODO: 9/9/2021 remove split business and mapping
        commissionOut.setBid(bidToOut(commissionEntity.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.HIGHEST) || x.getBidStatus().equals(BidStatus.WON))
                .findFirst()
                .orElseThrow(RuntimeException::new)));
        return commissionOut;
    }

    public ArtistOut artistToOut(ArtistEntity artistEntity){
        ArtistOut artistOut = new ArtistOut();
        artistOut.setId(artistEntity.getId());
        artistOut.setUsername(artistEntity.getUserDetails().getUsername());
        artistOut.setEmail(artistEntity.getEmail());
        artistOut.setDescription(artistEntity.getDescription());
        return artistOut;
    }

    public BidOut bidToOut(BidEntity bidEntity){
        BidOut bidOut = new BidOut();
        bidOut.setId(bidEntity.getId());
        bidOut.setAmount(bidEntity.getAmount());
        bidOut.setBuyerUsername(bidEntity.getBuyer().getUserDetails().getUsername());
        return bidOut;
    }

    public void updateUser(AbstractUser toUpdate, AbstractUser updateFrom, PasswordEncoder passwordEncoder){
        if(updateFrom.getBillingDetails() != null){
            toUpdate.setBillingDetails(updateFrom.getBillingDetails());
        }
        if(updateFrom.getEmail() != null){
            toUpdate.setEmail(updateFrom.getEmail());
        }
        if(updateFrom.getUserDetails().getUsername() != null){
            toUpdate.getUserDetails().setUsername(updateFrom.getUserDetails().getUsername());
        }
        if(updateFrom.getUserDetails().getPassword() != null){
            toUpdate.getUserDetails().setPassword(passwordEncoder.encode(updateFrom.getUserDetails().getPassword()));
        }
    }

    public void updateArtistEntity(ArtistEntity toUpdate, ArtistEntity updateFrom, PasswordEncoder passwordEncoder){
        updateUser(toUpdate, updateFrom, passwordEncoder);
        if(updateFrom.getDescription() != null){
            toUpdate.setDescription(updateFrom.getDescription());
        }
    }
}
