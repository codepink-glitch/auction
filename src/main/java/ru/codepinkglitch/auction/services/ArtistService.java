package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.ArtistIn;
import ru.codepinkglitch.auction.entities.ArtistEntity;
import ru.codepinkglitch.auction.repositories.ArtistRepository;
import ru.codepinkglitch.auction.repositories.UserDetailsRepository;

import java.util.List;


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

    public ArtistIn update(String name, ArtistIn artistIn) {
        ArtistEntity artistEntity = artistRepository.findArtistEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        ArtistIn artistIn1 = converter.artistToDto(artistEntity);
        artistIn1.setDescription(artistIn.getDescription());
        artistIn1.setBillingDetails(artistIn.getBillingDetails());
        artistIn1.setUsername(artistIn.getUsername());
        artistIn1.setPassword(artistIn.getPassword());
        artistIn1.setName(artistIn.getName());
        artistIn1.setSurname(artistIn.getSurname());
        artistIn1.setEmail(artistIn.getEmail());
        return artistIn1;
    }

    public void delete(String name) {
        ArtistEntity artistEntity = artistRepository.findArtistEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(artistEntity == null){
            throw new RuntimeException("No such user.");
        }
        artistRepository.delete(artistEntity);
    }

    public ArtistIn find(String name){
        ArtistEntity artistEntity = artistRepository.findArtistEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(artistEntity == null){
            throw new RuntimeException("No such user.");
        }
        return converter.artistToDto(artistEntity);
    }
}
