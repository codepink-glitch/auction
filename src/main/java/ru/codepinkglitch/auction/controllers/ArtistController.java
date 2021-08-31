package ru.codepinkglitch.auction.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.codepinkglitch.auction.dtos.in.ArtistIn;
import ru.codepinkglitch.auction.services.ArtistService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artist")
public class ArtistController {

    private final ArtistService artistService;

    @PutMapping
    public ResponseEntity<ArtistIn> update(@RequestBody ArtistIn artistIn){
        return new ResponseEntity<>(artistService.update(SecurityContextHolder.getContext().getAuthentication().getName(), artistIn), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> delete(){
        artistService.delete(SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>("Account deleted.", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ArtistIn> getArtist(){
        return new ResponseEntity<>(artistService.find(SecurityContextHolder.getContext().getAuthentication().getName()), HttpStatus.OK);
    }
}
