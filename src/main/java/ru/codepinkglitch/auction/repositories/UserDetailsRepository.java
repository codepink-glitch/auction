package ru.codepinkglitch.auction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.codepinkglitch.auction.entities.MyUserDetails;

public interface UserDetailsRepository extends JpaRepository<MyUserDetails, Long> {
    Boolean existsMyUserDetailsByUsername(String username);
    MyUserDetails findMyUserDetailsByUsername(String username);
}
