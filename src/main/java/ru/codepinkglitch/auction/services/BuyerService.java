package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.BidIn;
import ru.codepinkglitch.auction.dtos.in.BuyerIn;
import ru.codepinkglitch.auction.dtos.out.BuyerOut;
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.enums.BidStatus;
import ru.codepinkglitch.auction.enums.ExceptionEnum;
import ru.codepinkglitch.auction.enums.Role;
import ru.codepinkglitch.auction.exceptions.ServiceException;
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
    private static final String DEFAULT = "default";
    private static BuyerEntity defaultEntity;

    @PostConstruct
    private void postConstruct(){
        BuyerEntity buyer = new BuyerEntity();
        buyer.setBids(new ArrayList<>());
        BillingDetailsEntity billingDetails = new BillingDetailsEntity();
        billingDetails.setCcCVV(DEFAULT);
        billingDetails.setCcExpiration(DEFAULT);
        billingDetails.setCity(DEFAULT);
        billingDetails.setCcNumber(DEFAULT);
        billingDetails.setName(DEFAULT);
        billingDetails.setState(DEFAULT);
        billingDetails.setStreet(DEFAULT);
        billingDetails.setZip(DEFAULT);
        buyer.setBillingDetails(billingDetails);
        buyer.setEmail(DEFAULT);
        buyer.setUserDetails(new MyUserDetails(Collections.singletonList(new MyAuthority(Role.BUYER.name())), DEFAULT, DEFAULT, 1L));
        buyer.setId(1L);
        defaultEntity = buyerRepository.save(buyer);
    }

    public static String getDefaultBuyerUsername(){
        return defaultEntity.getUserDetails().getUsername();
    }

    public BuyerOut save(BuyerIn buyerIn){
        if(userDetailsRepository.existsMyUserDetailsByUsername(buyerIn.getUsername()).equals(true)){
            throw new ServiceException(ExceptionEnum.USER_ALREADY_EXISTS_EXCEPTION);
        } else {
            buyerIn.setPassword(bCryptPasswordEncoder.encode(buyerIn.getPassword()));
            return converter.buyerToOut(buyerRepository.save(converter.buyerFromDto(buyerIn)));
        }
    }

    public BuyerOut update(String name, BuyerIn buyerIn) {
        BuyerEntity buyerEntity = buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        BuyerEntity fromEntity = converter.buyerFromDto(buyerIn);
        converter.updateUser(buyerEntity, fromEntity, bCryptPasswordEncoder);
        return converter.buyerToOut(buyerRepository.save(buyerEntity));
    }

    public void delete(String name) {
        BuyerEntity buyerEntity = buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(buyerEntity == null){
            throw new ServiceException(ExceptionEnum.USER_DONT_EXIST_EXCEPTION);
        }
        buyerRepository.delete(buyerEntity);
    }

    public BuyerOut find(String name) {
        BuyerEntity buyerEntity = buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(buyerEntity == null){
            throw new ServiceException(ExceptionEnum.USER_DONT_EXIST_EXCEPTION);
        }
        return converter.buyerToOut(buyerEntity);
    }

    public List<BidIn> getBids(String name) {
        BuyerEntity buyerEntity = buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(buyerEntity == null){
            throw new ServiceException(ExceptionEnum.USER_DONT_EXIST_EXCEPTION);
        }
        return buyerEntity.getBids().stream()
                .map(converter::bidToDto)
                .collect(Collectors.toList());
    }

    public List<BidIn> getWonBids(String name) {
        BuyerEntity buyerEntity = buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(buyerEntity == null){
            throw new ServiceException(ExceptionEnum.USER_DONT_EXIST_EXCEPTION);
        }
        return buyerEntity.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.WON))
                .map(converter::bidToDto)
                .collect(Collectors.toList());
    }
}
