package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.ArtistIn;
import ru.codepinkglitch.auction.dtos.in.CommissionIn;
import ru.codepinkglitch.auction.dtos.out.CommissionOut;
import ru.codepinkglitch.auction.entities.ArtistEntity;
import ru.codepinkglitch.auction.enums.Status;
import ru.codepinkglitch.auction.enums.ExceptionEnum;
import ru.codepinkglitch.auction.exceptions.ServiceException;
import ru.codepinkglitch.auction.repositories.ArtistRepository;
import ru.codepinkglitch.auction.repositories.UserDetailsRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final Converter converter;
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public ArtistIn save(ArtistIn artist) {
        if(userDetailsRepository.existsMyUserDetailsByUsername(artist.getUsername()).equals(true)){
            throw new ServiceException(ExceptionEnum.USER_ALREADY_EXISTS_EXCEPTION);
        } else {
            artist.setPassword(bCryptPasswordEncoder.encode(artist.getPassword()));
            return converter.artistToDto(artistRepository.save(converter.artistFromDto(artist)));
        }
    }

    public ArtistIn update(String name, ArtistIn artistIn) {
        ArtistEntity artistEntity = artistRepository.findArtistEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        ArtistEntity fromEntity = converter.artistFromDto(artistIn);
        converter.updateArtistEntity(artistEntity, fromEntity, bCryptPasswordEncoder);
        return converter.artistToDto(artistRepository.save(artistEntity));
    }

    public void delete(String name) {
        ArtistEntity artistEntity = artistRepository.findArtistEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(artistEntity == null){
            throw new ServiceException(ExceptionEnum.USER_DONT_EXIST_EXCEPTION);
        }
        artistRepository.delete(artistEntity);
    }

    public ArtistIn find(String name){
        ArtistEntity artistEntity = artistRepository.findArtistEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(artistEntity == null){
            throw new ServiceException(ExceptionEnum.USER_DONT_EXIST_EXCEPTION);
        }
        return converter.artistToDto(artistEntity);
    }

    public List<CommissionIn> getCommissions(String name) {
        ArtistEntity artistEntity = artistRepository.findArtistEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(artistEntity == null){
            throw new ServiceException(ExceptionEnum.USER_DONT_EXIST_EXCEPTION);
        }
        return artistEntity.getCommissions().stream()
                .map(converter::commissionToDto)
                .collect(Collectors.toList());
    }

    public List<CommissionOut> getSoldCommissions(String name) {
        ArtistEntity artistEntity = artistRepository.findArtistEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        if(artistEntity == null){
            throw new ServiceException(ExceptionEnum.USER_DONT_EXIST_EXCEPTION);
        }
        return artistEntity.getCommissions().stream()
                .filter(x -> x.getStatus().equals(Status.CLOSED))
                .map(converter::commissionToOut)
                .filter(x -> !x.getBid().getBuyerUsername().equals("default"))
                .collect(Collectors.toList());
    }
}
