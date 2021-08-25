package ru.codepinkglitch.auction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.codepinkglitch.auction.entities.ArtistEntity;

public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
    ArtistEntity findArtistEntityByUsername(String username);
    Boolean existsArtistEntityByUsername(String username);
}
