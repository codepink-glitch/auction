package ru.codepinkglitch.auction.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.CommissionWrapper;
import ru.codepinkglitch.auction.dtos.out.ArtistOut;
import ru.codepinkglitch.auction.dtos.out.BuyerOut;
import ru.codepinkglitch.auction.dtos.out.CommissionOut;
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.enums.Role;
import ru.codepinkglitch.auction.repositories.ArtistRepository;
import ru.codepinkglitch.auction.repositories.BuyerRepository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TestService {

    @Autowired
    BuyerService buyerService;

    @Autowired
    ArtistService artistService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    BuyerRepository buyerRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    Converter converter;

    BillingDetailsEntity billingDetailsEntity;

    @PostConstruct
    public void setup() {
        billingDetailsEntity = new BillingDetailsEntity();
        billingDetailsEntity.setName("Vasily Vasiliev");
        billingDetailsEntity.setStreet("Pupkina");
        billingDetailsEntity.setCity("Moscow");
        billingDetailsEntity.setState("Moscow");
        billingDetailsEntity.setZip("111111");
        billingDetailsEntity.setCcNumber("1111 1111 1111 1111 1111");
        billingDetailsEntity.setCcExpiration("10/22");
        billingDetailsEntity.setCcCVV("111");
    }

    public BuyerOut initForBuyer(String email, String username, String password) {
        BuyerEntity buyerEntity = new BuyerEntity();
        buyerEntity.setBillingDetails(billingDetailsEntity);
        buyerEntity.setBids(new ArrayList<>());
        MyAuthority myAuthority = new MyAuthority();
        myAuthority.setAuthority(Role.BUYER.name());
        buyerEntity.setUserDetails(new MyUserDetails(Collections.singletonList(myAuthority), password, username));
        buyerEntity.setEmail(email);
        return buyerService.save(converter.buyerToDto(buyerEntity));
    }

    public ArtistOut initForArtist(String email, String username, String password) {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setBillingDetails(billingDetailsEntity);
        artistEntity.setCommissions(new ArrayList<>());
        artistEntity.setDescription("default");
        MyAuthority myAuthority = new MyAuthority();
        myAuthority.setAuthority(Role.ARTIST.name());
        artistEntity.setUserDetails(new MyUserDetails(Collections.singletonList(myAuthority), password, username));
        artistEntity.setEmail(email);
        return artistService.save(converter.artistToDto(artistEntity));
    }

    public CommissionOut initForCommission(String artistUsername, List<String> tags, BigDecimal minimalBid) {
        CommissionWrapper commissionWrapper = new CommissionWrapper();
        commissionWrapper.setDaysPeriod(0);
        commissionWrapper.setHoursPeriod(1);
        commissionWrapper.setMinutesPeriod(0);
        commissionWrapper.setTags(tags);
        commissionWrapper.setMinimalBid(minimalBid);
        return commissionService.create(artistUsername, commissionWrapper);
    }

}

