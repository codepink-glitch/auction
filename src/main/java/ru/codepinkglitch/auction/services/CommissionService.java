package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.CommissionIn;
import ru.codepinkglitch.auction.dtos.in.CommissionWrapper;
import ru.codepinkglitch.auction.dtos.out.CommissionOut;
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.repositories.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final ArtistRepository artistRepository;
    private final CommissionRepository commissionRepository;
    private final BuyerRepository buyerRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final Converter converter;

    public CommissionIn findById(Long id) {
        Optional<CommissionEntity> optional = commissionRepository.findById(id);
        if (optional.isPresent()) {
            return converter.commissionToDto(optional.get());
        } else {
            throw new RuntimeException("No such user.");
        }
    }

    public CommissionIn save(CommissionIn commissionIn) {
        if (commissionIn == null) {
            throw new RuntimeException("Can not save this commission.");
        } else {
            CommissionEntity commission = converter.commissionFromDto(commissionIn);
            return converter.commissionToDto(commissionRepository.save(commission));
        }
    }

    public CommissionIn update(CommissionIn commissionIn) {
        Optional<CommissionEntity> optional = commissionRepository.findById(commissionIn.getId());
        if (optional.isPresent()) {
            CommissionIn commission = converter.commissionToDto(optional.get());
            commission.update(commissionIn);
            return save(commission);
        } else {
            throw new RuntimeException("Can not update this commission.");
        }
    }

    public List<CommissionOut> findByTag(String tag) {
        List<CommissionEntity> list = commissionRepository.findAll();
        List<CommissionOut> filteredList = list.stream().filter(x -> x.getTags().contains(tag)).map(converter::commissionToOut).collect(Collectors.toList());
        if(!filteredList.isEmpty()) {
            return filteredList;
        } else {
            throw new RuntimeException("No such commissions.");
        }
    }

    public void remove(Long id) {
        if(commissionRepository.existsById(id)){
            commissionRepository.deleteById(id);
        } else {
            throw new RuntimeException("No such commission.");
        }
    }
    public CommissionOut create(String name, CommissionWrapper commissionWrapper) {
        CommissionEntity commission = new CommissionEntity();
        commission.setStatus(Status.OPEN);
        commission.setPublishDate(Calendar.getInstance());
        commission.setUri(commissionWrapper.getUri());
        commission.setTags(commissionWrapper.getTags());
        commission.setAuthor(
                artistRepository.findArtistEntityByUserDetails(
                        userDetailsRepository.findMyUserDetailsByUsername(name)
                ));
        BidEntity bidEntity = new BidEntity();
        bidEntity.setBidStatus(BidStatus.HIGHEST);
        bidEntity.setAmount(commissionWrapper.getMinimalBid());
        bidEntity.setBuyer(buyerRepository.getById(1L));
        bidEntity.setCommission(commission);
        commission.setBids(Arrays.asList(bidEntity));
        return converter.commissionToOut(commissionRepository.save(commission));
    }

    public List<CommissionOut> findAll() {
        return commissionRepository.findAll()
                .stream()
                .map(converter::commissionToOut)
                .collect(Collectors.toList());
    }

    public CommissionOut setBid(BigDecimal bid, Long commissionId, String name) {
        Optional<CommissionEntity> optional = commissionRepository.findById(commissionId);
        if(!optional.isPresent()) {
            throw new RuntimeException("No such commission.");
        }
        CommissionEntity commissionEntity = optional.get();
        if(commissionEntity.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.HIGHEST)).findFirst().orElseThrow(RuntimeException::new).getAmount().compareTo(bid) > 0){
            throw new RuntimeException("You can not outbid with lower amount.");
        }
        commissionEntity.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.HIGHEST))
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .setBidStatus(BidStatus.OUTBID);
        BidEntity bidEntity = new BidEntity();
        bidEntity.setBidStatus(BidStatus.HIGHEST);
        bidEntity.setAmount(bid);
        bidEntity.setCommission(commissionEntity);
        bidEntity.setBuyer(
                buyerRepository.findBuyerEntityByUserDetails
                        (userDetailsRepository.findMyUserDetailsByUsername(name)
                        ));
        commissionEntity.getBids().add(bidEntity);
        return converter.commissionToOut(commissionRepository.save(commissionEntity));
    }

}
