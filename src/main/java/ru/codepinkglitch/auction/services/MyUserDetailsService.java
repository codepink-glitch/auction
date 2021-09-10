package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.repositories.UserDetailsRepository;


@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserDetailsRepository userDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        boolean userExist = userDetailsRepository.existsMyUserDetailsByUsername(username);
        if(!userExist){
            throw new UsernameNotFoundException("No such user.");
        } else {
            return userDetailsRepository.findMyUserDetailsByUsername(username);
        }
    }
}
