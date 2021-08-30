package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.BuyerIn;
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.repositories.BuyerRepository;
import ru.codepinkglitch.auction.repositories.UserDetailsRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final Converter converter;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    private void postConstruct(){
        BuyerEntity buyer = new BuyerEntity();
        buyer.setBids(new ArrayList<>());
        buyer.setCommissions(new ArrayList<>());
        BillingDetailsEntity billingDetails = new BillingDetailsEntity();
        billingDetails.setCcCVV("default");
        billingDetails.setCcExpiration("default");
        billingDetails.setCity("default");
        billingDetails.setCcNumber("default");
        billingDetails.setName("default");
        billingDetails.setState("default");
        billingDetails.setStreet("default");
        billingDetails.setZip("default");
        buyer.setBillingDetails(billingDetails);
        buyer.setName("default");
        buyer.setEmail("default");
        List<MyAuthority> list = new ArrayList<>();
        list.add(new MyAuthority(Role.BUYER.name()));
        buyer.setSurname("default");
        MyUserDetails myUserDetails = new MyUserDetails(list, "default", "default");
        buyer.setUserDetails(myUserDetails);
        buyer.setId(1L);
        buyerRepository.save(buyer);
    }

    public BuyerIn save(BuyerIn buyerIn){
        if(userDetailsRepository.existsMyUserDetailsByUsername(buyerIn.getUsername())){
            throw new RuntimeException("User with such username already exists.");
        } else {
            buyerIn.setPassword(bCryptPasswordEncoder.encode(buyerIn.getPassword()));
            return converter.buyerToDto(buyerRepository.save(converter.buyerFromDto(buyerIn)));
        }
    }

}
