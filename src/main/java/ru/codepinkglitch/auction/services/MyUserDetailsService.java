package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.entities.AbstractUser;
import ru.codepinkglitch.auction.repositories.ArtistRepository;
import ru.codepinkglitch.auction.repositories.BuyerRepository;
import ru.codepinkglitch.auction.repositories.UserDetailsRepository;


@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final ArtistRepository artistRepository;
    private final BuyerRepository buyerRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        if(userDetailsRepository.existsMyUserDetailsByUsername(username)){
            throw new RuntimeException("No such user.");
        } else {
            return userDetailsRepository.findMyUserDetailsByUsername(username);
        }
    }
}
