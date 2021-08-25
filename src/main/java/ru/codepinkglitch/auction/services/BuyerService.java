package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.BuyerIn;
import ru.codepinkglitch.auction.repositories.BuyerRepository;


@Service
@RequiredArgsConstructor
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final Converter converter;
    private final static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public BuyerIn save(BuyerIn buyerIn){
        if(buyerRepository.existsBuyerEntityByUsername(buyerIn.getUsername())){
            throw new RuntimeException("User with such username already exists.");
        } else {
            buyerIn.setPassword(bCryptPasswordEncoder.encode(buyerIn.getPassword()));
            return converter.buyerToDto(buyerRepository.save(converter.buyerFromDto(buyerIn)));
        }
    }

}
