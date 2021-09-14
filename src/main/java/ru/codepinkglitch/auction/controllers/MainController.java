package ru.codepinkglitch.auction.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.codepinkglitch.auction.dtos.in.CommissionWrapper;
import ru.codepinkglitch.auction.dtos.out.CommissionOut;
import ru.codepinkglitch.auction.services.CommissionService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main/")
public class MainController {

    private final CommissionService commissionService;

    @PostMapping
    public ResponseEntity<CommissionOut> createNew(@RequestBody CommissionWrapper commissionWrapper){
        return new ResponseEntity<>(commissionService.create(SecurityContextHolder.getContext().getAuthentication().getName(),
                commissionWrapper), HttpStatus.OK);
    }

    @GetMapping("/find")
    public ResponseEntity<List<CommissionOut>> findByTag(@RequestParam String tag){
        return new ResponseEntity<>(commissionService.findByTag(tag), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CommissionOut>> findAll(){
        return new ResponseEntity<>(commissionService.findAll(), HttpStatus.OK);
    }


    @GetMapping()
    public ResponseEntity<CommissionOut> bid(@RequestParam BigDecimal bid, @RequestParam Long commissionId){
        return new ResponseEntity<>(commissionService.setBid(bid, commissionId, SecurityContextHolder.getContext().getAuthentication().getName()), HttpStatus.OK);
    }

    @PostMapping("/image")
    public ResponseEntity<String> setImage(@RequestParam Long commissionId, @RequestParam("file") MultipartFile multipartFile){
        commissionService.attachImage(commissionId, multipartFile, SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>("Image attached.", HttpStatus.OK);
    }

    @GetMapping(value = "/preview", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getPreview(@RequestParam Long commissionId){
        return commissionService.getPreview(commissionId);
    }

    @PostMapping("/finishedImage")
    public ResponseEntity<String> setFinishedImage(@RequestParam Long commissionId, @RequestParam("file") MultipartFile multipartFile){
        commissionService.attachFinishedImage(commissionId, multipartFile, SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>("Image attached.", HttpStatus.OK);
    }

    @GetMapping(value = "/finished", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getFinishedImage(@RequestParam Long commissionId){
        return commissionService.getFinishedImage(commissionId, SecurityContextHolder.getContext().getAuthentication().getName());
    }


}
