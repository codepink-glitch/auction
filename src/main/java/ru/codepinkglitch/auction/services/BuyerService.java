package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.BidIn;
import ru.codepinkglitch.auction.dtos.in.BuyerIn;
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.exceptions.UserAlreadyExistsException;
import ru.codepinkglitch.auction.exceptions.UserDontExistException;
import ru.codepinkglitch.auction.repositories.BuyerRepository;
import ru.codepinkglitch.auction.repositories.UserDetailsRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final Converter converter;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final static String NO_USER_MESSAGE = "No such user.";

    @PostConstruct
    private void postConstruct(){
        BuyerEntity buyer = new BuyerEntity();
        buyer.setBids(new ArrayList<>());
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
        buyer.setEmail("default");
        List<MyAuthority> list = Collections.emptyList();
        buyer.setUserDetails(new MyUserDetails(Collections.singletonList(new MyAuthority(Role.BUYER.name())), "default", "default", 1L));
        buyer.setId(1L);
        buyerRepository.save(buyer);
    }

    public BuyerIn save(BuyerIn buyerIn){
        if(userDetailsRepository.existsMyUserDetailsByUsername(buyerIn.getUsername())){
            throw new UserAlreadyExistsException("User with such username already exists.");
        } else {
            buyerIn.setPassword(bCryptPasswordEncoder.encode(buyerIn.getPassword()));
            return converter.buyerToDto(buyerRepository.save(converter.buyerFromDto(buyerIn)));
        }
    }

    public BuyerIn update(String name, BuyerIn buyerIn) {
        BuyerEntity buyerEntity = buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        BuyerEntity fromEntity = converter.buyerFromDto(buyerIn);
        buyerEntity.update(fromEntity);
        return converter.buyerToDto(buyerRepository.save(buyerEntity));
    }

    public void delete(String name) {
        BuyerEntity buyerEntity = buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(buyerEntity == null){
            throw new UserDontExistException(NO_USER_MESSAGE);
        }
        buyerRepository.delete(buyerEntity);
    }

    public BuyerIn find(String name) {
        BuyerEntity buyerEntity = buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(buyerEntity == null){
            throw new UserDontExistException(NO_USER_MESSAGE);
        }
        return converter.buyerToDto(buyerEntity);
    }

    public List<BidIn> getBids(String name) {
        BuyerEntity buyerEntity = buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(buyerEntity == null){
            throw new UserDontExistException(NO_USER_MESSAGE);
        }
        return buyerEntity.getBids().stream()
                .map(converter::bidToDto)
                .collect(Collectors.toList());
    }

    public List<BidIn> getWonBids(String name) {
        BuyerEntity buyerEntity = buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(buyerEntity == null){
            throw new UserDontExistException(NO_USER_MESSAGE);
        }
        return buyerEntity.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.WON))
                .map(converter::bidToDto)
                .collect(Collectors.toList());
    }
}
