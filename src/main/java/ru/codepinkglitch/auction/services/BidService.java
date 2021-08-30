package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.repositories.BidRepository;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final Converter converter;


}
