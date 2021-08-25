package ru.codepinkglitch.auction.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.codepinkglitch.auction.dtos.in.ArtistIn;
import ru.codepinkglitch.auction.dtos.in.BuyerIn;
import ru.codepinkglitch.auction.services.ArtistService;
import ru.codepinkglitch.auction.services.BuyerService;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {

    private final BuyerService buyerService;
    private final ArtistService artistService;

    @PostMapping("/buyer")
    public ResponseEntity<BuyerIn> newBuyer(@RequestBody BuyerIn buyer){
        return new ResponseEntity<>(buyerService.save(buyer), HttpStatus.OK);
    }

    @PostMapping("/artist")
    public ResponseEntity<ArtistIn> newArtist(@RequestBody ArtistIn artist){
        return new ResponseEntity<>(artistService.save(artist), HttpStatus.OK);
    }
}
