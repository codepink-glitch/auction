package ru.codepinkglitch.auction.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.codepinkglitch.auction.dtos.in.CommissionIn;
import ru.codepinkglitch.auction.entities.Status;
import ru.codepinkglitch.auction.services.MyUserDetailsService;

import java.util.Calendar;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main/")
public class MainController {

    private final MyUserDetailsService myUserDetailsService;

    @PostMapping
    public ResponseEntity<CommissionIn> createNew(@RequestBody List<String> tags,
                                                  @RequestBody String uri){
        CommissionIn commission = new CommissionIn();
        commission.setStatus(Status.OPEN);
        commission.setPublishDate(Calendar.getInstance());
        commission.setUri(uri);
        commission.setTags(tags);
        //commission.setAuthor()
        //commission.setBid(null);
        return new ResponseEntity<>(commission, HttpStatus.OK);
    }


    /*
    @GetMapping("/{username}")
    public ResponseEntity getUser(@PathVariable String username){
        return new ResponseEntity(myUserDetailsService.loadUserByUsername(username), HttpStatus.OK);
    }
     */
}
