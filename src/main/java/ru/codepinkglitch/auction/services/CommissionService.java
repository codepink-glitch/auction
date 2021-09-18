package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.CommissionWrapper;
import ru.codepinkglitch.auction.dtos.out.CommissionOut;
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.enums.BidStatus;
import ru.codepinkglitch.auction.enums.ExceptionEnum;
import ru.codepinkglitch.auction.enums.Status;
import ru.codepinkglitch.auction.exceptions.*;
import ru.codepinkglitch.auction.repositories.*;
import ru.codepinkglitch.auction.runnables.RunnableTask;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final TaskScheduler taskScheduler;

    public List<CommissionOut> findByTag(String tag) {
        List<CommissionEntity> filtered = commissionRepository.findAll().stream()
                .filter(x -> x.getTags().contains(tag))
                .collect(Collectors.toList());
        if(filtered.isEmpty()){
            throw new ServiceException(ExceptionEnum.COMMISSION_DONT_EXIST_EXCEPTION);
        }
        List<CommissionOut> filteredDtos = filtered.stream().map(converter::commissionToOut).collect(Collectors.toList());
        return setBidForCommissionOutList(filteredDtos, filtered);
    }

    @Transactional
    public CommissionOut create(String name, CommissionWrapper commissionWrapper) {
        CommissionEntity commission = new CommissionEntity();
        commission.setStatus(Status.OPEN);
        commission.setPublishDate(LocalDateTime.now());
        commission.setClosingDate(LocalDateTime.now()
                .plusMinutes(commissionWrapper.getMinutesPeriod())
                .plusHours(commissionWrapper.getHoursPeriod())
                .plusDays(commissionWrapper.getDaysPeriod()));
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
        commission.setBids(Collections.singletonList(bidEntity));
        CommissionOut commissionOut = converter.commissionToOut(commissionRepository.save(commission));
        taskScheduler.schedule(new RunnableTask(commissionOut.getId(), commissionRepository),
                Date.from(commission.getClosingDate().atZone(ZoneId.systemDefault()).toInstant()));
        return setBidForCommissionOut(commissionOut, commission);
    }

    public List<CommissionOut> findAll() {
        List<CommissionEntity> allCommissions = commissionRepository.findAll();
        List<CommissionOut> commissionsToOut = allCommissions.stream()
                .map(converter::commissionToOut)
                .collect(Collectors.toList());
       return setBidForCommissionOutList(commissionsToOut, allCommissions);
    }

    @Transactional
    public CommissionOut setBid(BigDecimal bid, Long commissionId, String name) {
        Optional<CommissionEntity> optional = commissionRepository.findById(commissionId);
        if(!optional.isPresent()) {
            throw new ServiceException(ExceptionEnum.COMMISSION_DONT_EXIST_EXCEPTION);
        }
        CommissionEntity commissionEntity = optional.get();
        if(commissionEntity.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.HIGHEST))
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAmount().compareTo(bid) > 0) {
            throw new ServiceException(ExceptionEnum.BID_EXCEPTION);
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
        BuyerEntity buyerEntity = buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(name));
        bidEntity.setBuyer(buyerEntity);
        commissionEntity.getBids().add(bidEntity);
        buyerEntity.getBids().add(bidEntity);
        buyerRepository.save(buyerEntity);
        CommissionOut commissionOut = converter.commissionToOut(commissionRepository.save(commissionEntity));
        return setBidForCommissionOut(commissionOut, commissionEntity);
    }


    public void attachImage(Long commissionId, MultipartFile multipartFile, String name){
        Optional<CommissionEntity> optional = commissionRepository.findById(commissionId);
        if(!optional.isPresent()){
            throw new ServiceException(ExceptionEnum.COMMISSION_DONT_EXIST_EXCEPTION);
        }
        CommissionEntity commissionEntity = optional.get();
        if(!commissionEntity.getAuthor().getUserDetails().getUsername().equals(name)){
            throw new ServiceException(ExceptionEnum.ACCESS_EXCEPTION);
        }
        try {
            commissionEntity.setPreviewPicture(Base64.getEncoder().encodeToString(multipartFile.getBytes()));
            commissionRepository.save(commissionEntity);
        } catch(IOException e){
            throw new ServiceException(ExceptionEnum.PICTURE_EXCEPTION);
        }

    }

    public byte[] getPreview(Long commissionId) {
        Optional<CommissionEntity> optional = commissionRepository.findById(commissionId);
        if(!optional.isPresent()){
            throw new ServiceException(ExceptionEnum.COMMISSION_DONT_EXIST_EXCEPTION);
        }
        CommissionEntity commissionEntity = optional.get();
        if(commissionEntity.getPreviewPicture() == null){
            throw new ServiceException(ExceptionEnum.PICTURE_EXCEPTION);
        }
        return Base64.getDecoder().decode(commissionEntity.getPreviewPicture());
    }

    public void attachFinishedImage(Long commissionId, MultipartFile multipartFile, String name) {
        Optional<CommissionEntity> optional = commissionRepository.findById(commissionId);
        if(!optional.isPresent()){
            throw new ServiceException(ExceptionEnum.PICTURE_EXCEPTION);
        }
        CommissionEntity commissionEntity = optional.get();
        if(!commissionEntity.getAuthor().getUserDetails().getUsername().equals(name)){
            throw new ServiceException(ExceptionEnum.ACCESS_EXCEPTION);
        }
        try {
            commissionEntity.setFinishedPicture(Base64.getEncoder().encodeToString(multipartFile.getBytes()));
            commissionRepository.save(commissionEntity);
        } catch(IOException e) {
            throw new ServiceException(ExceptionEnum.PICTURE_EXCEPTION);
        }

    }

    public byte[] getFinishedImage(Long commissionId, String name) {
        Optional<CommissionEntity> optional = commissionRepository.findById(commissionId);
        if(!optional.isPresent()){
            throw new ServiceException(ExceptionEnum.COMMISSION_DONT_EXIST_EXCEPTION);
        }
        CommissionEntity commissionEntity = optional.get();
        if(commissionEntity.getStatus().equals(Status.OPEN)){
            throw new ServiceException(ExceptionEnum.COMMISSION_NOT_OVER_EXCEPTION);
        }
        BidEntity bidEntity = commissionEntity.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.WON))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        if(!bidEntity.getBuyer().getUserDetails().getUsername().equals(name)){
            throw new ServiceException(ExceptionEnum.ACCESS_EXCEPTION);
        }
        if(commissionEntity.getFinishedPicture() == null){
            throw new ServiceException(ExceptionEnum.PICTURE_EXCEPTION);
        }
        return Base64.getDecoder().decode(commissionEntity.getFinishedPicture());
    }

    private CommissionOut setBidForCommissionOut(CommissionOut commissionToSetBid, CommissionEntity commissionFromWhichGetBid){
        BidEntity bidEntity = commissionFromWhichGetBid.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.HIGHEST) || x.getBidStatus().equals(BidStatus.WON))
                .findFirst()
                .orElseThrow(() -> new ServiceException(ExceptionEnum.CONVERSION_EXCEPTION));
        commissionToSetBid.setBid(converter.bidToOut(bidEntity));
        return commissionToSetBid;
    }

    private List<CommissionOut> setBidForCommissionOutList(List<CommissionOut> listToSetBid, List<CommissionEntity> listFromWhichGetBids){
        if(listToSetBid.size() != listFromWhichGetBids.size()){
            throw new ServiceException(ExceptionEnum.CONVERSION_EXCEPTION);
        }
        for(int i = 0; i < listToSetBid.size(); i++){
            listToSetBid.set(i, setBidForCommissionOut(listToSetBid.get(i), listFromWhichGetBids.get(i)));
        }
        return listToSetBid;
    }
}
