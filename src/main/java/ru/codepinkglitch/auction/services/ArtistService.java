package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.ArtistIn;
import ru.codepinkglitch.auction.repositories.ArtistRepository;
import ru.codepinkglitch.auction.repositories.UserDetailsRepository;


@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final Converter converter;
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public ArtistIn save(ArtistIn artist) {
        if(userDetailsRepository.existsMyUserDetailsByUsername(artist.getUsername())){
            throw new RuntimeException("User with such username already exists.");
        } else {
            artist.setPassword(bCryptPasswordEncoder.encode(artist.getPassword()));
            return converter.artistToDto(artistRepository.save(converter.artistFromDto(artist)));
        }
    }
}