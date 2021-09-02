package ru.codepinkglitch.auction.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.codepinkglitch.auction.dtos.in.*;
import ru.codepinkglitch.auction.dtos.out.ArtistOut;
import ru.codepinkglitch.auction.dtos.out.BidOut;
import ru.codepinkglitch.auction.dtos.out.CommissionOut;
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.repositories.BidRepository;
import ru.codepinkglitch.auction.repositories.BuyerRepository;
import ru.codepinkglitch.auction.repositories.CommissionRepository;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class Converter {

    private final BidRepository bidRepository;
    private final CommissionRepository commissionRepository;
    private final BuyerRepository buyerRepository;

    public BuyerEntity buyerFromDto(BuyerIn buyer){
        BuyerEntity buyerEntity = new BuyerEntity();
        buyerEntity.setBids(buyer.getBidsIds().stream()
                .map(bidRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList())
        );
        buyerEntity.setId(buyer.getId());
        buyerEntity.setCommissions(buyer.getCommissionsIds()
                .stream()
                .map(commissionRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
        buyerEntity.setUserDetails(new MyUserDetails(Arrays.asList(new MyAuthority(Role.BUYER.name())), buyer.getPassword(), buyer.getUsername()));
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
        buyerIn.setCommissionsIds(buyer.getCommissions()
                .stream()
                .map(CommissionEntity::getId)
                .collect(Collectors.toList())
        );
        buyerIn.setUsername(buyer.getUserDetails().getUsername());
        buyerIn.setPassword(buyer.getUserDetails().getPassword());
        buyerIn.setEmail(buyer.getEmail());
        buyerIn.setBillingDetails(detailsToDto(buyer.getBillingDetails()));
        return buyerIn;
    }

    public BillingDetailsIn detailsToDto(BillingDetailsEntity billingDetailsEntity){
        BillingDetailsIn billingDetailsIn = new BillingDetailsIn();
        billingDetailsIn.setId(billingDetailsEntity.getId());
        billingDetailsIn.setName(billingDetailsEntity.getName());
        billingDetailsIn.setStreet(billingDetailsEntity.getStreet());
        billingDetailsIn.setCity(billingDetailsEntity.getCity());
        billingDetailsIn.setState(billingDetailsEntity.getState());
        billingDetailsIn.setZip(billingDetailsEntity.getZip());
        billingDetailsIn.setCcNumber(billingDetailsEntity.getCcNumber());
        billingDetailsIn.setCcExpiration(billingDetailsEntity.getCcExpiration());
        billingDetailsIn.setCcCVV(billingDetailsEntity.getCcCVV());
        return billingDetailsIn;
    }

    public BillingDetailsEntity detailsFromDto(BillingDetailsIn billingDetailsIn){
        BillingDetailsEntity billingDetails = new BillingDetailsEntity();
        billingDetails.setId(billingDetailsIn.getId());
        billingDetails.setName(billingDetailsIn.getName());
        billingDetails.setStreet(billingDetailsIn.getStreet());
        billingDetails.setCity(billingDetailsIn.getCity());
        billingDetails.setState(billingDetailsIn.getState());
        billingDetails.setZip(billingDetailsIn.getZip());
        billingDetails.setCcNumber(billingDetailsIn.getCcNumber());
        billingDetails.setCcExpiration(billingDetailsIn.getCcExpiration());
        billingDetails.setCcCVV(billingDetailsIn.getCcCVV());
        return billingDetails;
    }

    public ArtistIn artistToDto(ArtistEntity artistEntity){
        ArtistIn artistIn = new ArtistIn();
        artistIn.setId(artistEntity.getId());
        artistIn.setEmail(artistEntity.getEmail());
        artistIn.setBillingDetails(detailsToDto(artistEntity.getBillingDetails()));
        artistIn.setUsername(artistEntity.getUserDetails().getUsername());
        artistIn.setPassword(artistEntity.getUserDetails().getPassword());
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
        artistEntity.setUserDetails(new MyUserDetails(Arrays.asList(new MyAuthority(Role.ARTIST.name())), artistIn.getPassword(), artistIn.getUsername()));
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
        commissionIn.setUri(commissionEntity.getUri());
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
        commissionEntity.setUri(commissionIn.getUri());
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
        commissionOut.setPublishDate(commissionEntity.getPublishDate().get(Calendar.DAY_OF_MONTH)
                + "." + (commissionEntity.getPublishDate().get(Calendar.MONTH) + 1)
                + "." + commissionEntity.getPublishDate().get(Calendar.YEAR)
                + " " + commissionEntity.getPublishDate().get(Calendar.HOUR_OF_DAY)
                + ":" + commissionEntity.getPublishDate().get(Calendar.MINUTE));
        commissionOut.setClosingDate(commissionEntity.getClosingDate().get(Calendar.DAY_OF_MONTH)
                + "." + (commissionEntity.getClosingDate().get(Calendar.MONTH) + 1)
                + "." + commissionEntity.getClosingDate().get(Calendar.YEAR)
                + " " + commissionEntity.getClosingDate().get(Calendar.HOUR_OF_DAY)
                + ":" + commissionEntity.getClosingDate().get(Calendar.MINUTE));
        commissionOut.setUri(commissionEntity.getUri());
        commissionOut.setTags(commissionEntity.getTags());
        commissionOut.setAuthor(artistToOut(commissionEntity.getAuthor()));
        commissionOut.setBid(bidToOut(commissionEntity.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.HIGHEST))
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
}
