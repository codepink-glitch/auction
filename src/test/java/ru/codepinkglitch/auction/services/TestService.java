package ru.codepinkglitch.auction.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.ArtistIn;
import ru.codepinkglitch.auction.dtos.in.BuyerIn;
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.enums.Role;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;

@Service
// TODO: 9/9/2021 improve to 80% all line coverage
public class TestService {

    @Autowired
    BuyerService buyerService;

    @Autowired
    ArtistService artistService;

    @Autowired
    Converter converter;

    BillingDetailsEntity billingDetailsEntity;

    @PostConstruct
    public void setup(){
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

    public BuyerIn initForBuyer(String email, String username, String password){
            BuyerEntity buyerEntity = new BuyerEntity();
            buyerEntity.setBillingDetails(billingDetailsEntity);
            buyerEntity.setBids(new ArrayList<>());
            MyAuthority myAuthority = new MyAuthority();
            myAuthority.setAuthority(Role.BUYER.name());
            buyerEntity.setUserDetails(new MyUserDetails(Collections.singletonList(myAuthority), password, username));
            buyerEntity.setEmail(email);
            return buyerService.save(converter.buyerToDto(buyerEntity));
        }

    public ArtistIn initForArtist(String email, String username, String password){
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

}

