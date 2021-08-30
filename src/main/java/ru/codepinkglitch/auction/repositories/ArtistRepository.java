package ru.codepinkglitch.auction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.codepinkglitch.auction.entities.ArtistEntity;
import ru.codepinkglitch.auction.entities.MyUserDetails;

public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
    ArtistEntity findArtistEntityByUserDetails(MyUserDetails userDetails);
}
