package ru.codepinkglitch.auction.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.codepinkglitch.auction.dtos.in.BuyerIn;
import ru.codepinkglitch.auction.services.BuyerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/buyer")
public class BuyerController {

    private final BuyerService buyerService;

    @PutMapping
    public ResponseEntity<BuyerIn> update(@RequestBody BuyerIn buyerIn){
        return new ResponseEntity<>(buyerService.update(SecurityContextHolder.getContext().getAuthentication().getName(), buyerIn), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> delete(){
        buyerService.delete(SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>("Account deleted.", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<BuyerIn> getBuyer(){
        return new ResponseEntity<>(buyerService.find(SecurityContextHolder.getContext().getAuthentication().getName()), HttpStatus.OK);
    }
}
