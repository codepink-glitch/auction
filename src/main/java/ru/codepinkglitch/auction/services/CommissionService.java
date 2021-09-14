package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.CommissionIn;
import ru.codepinkglitch.auction.dtos.in.CommissionWrapper;
import ru.codepinkglitch.auction.dtos.out.CommissionOut;
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.enums.BidStatus;
import ru.codepinkglitch.auction.enums.ExceptionEnum;
import ru.codepinkglitch.auction.enums.Status;
import ru.codepinkglitch.auction.exceptions.*;
import ru.codepinkglitch.auction.repositories.*;
import ru.codepinkglitch.auction.runnables.RunnableTask;

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

    public CommissionIn findById(Long id) {
        Optional<CommissionEntity> optional = commissionRepository.findById(id);
        if (optional.isPresent()) {
            return converter.commissionToDto(optional.get());
        } else {
            throw new ServiceException(ExceptionEnum.COMMISSION_DONT_EXIST_EXCEPTION);
        }
    }

    public CommissionIn save(CommissionIn commissionIn) {
        if (commissionIn == null) {
            throw new ServiceException(ExceptionEnum.EMPTY_COMMISSION_EXCEPTION);
        } else {
            CommissionEntity commission = converter.commissionFromDto(commissionIn);
            return converter.commissionToDto(commissionRepository.save(commission));
        }
    }


    public List<CommissionOut> findByTag(String tag) {
        List<CommissionEntity> list = commissionRepository.findAll();
        List<CommissionOut> filteredList = list.stream().filter(x -> x.getTags().contains(tag)).map(converter::commissionToOut).collect(Collectors.toList());
        if(!filteredList.isEmpty()) {
            return filteredList;
        } else {
            throw new ServiceException(ExceptionEnum.COMMISSION_DONT_EXIST_EXCEPTION);
        }
    }

    public void remove(Long id) {
        if(commissionRepository.existsById(id)){
            commissionRepository.deleteById(id);
        } else {
            throw new ServiceException(ExceptionEnum.COMMISSION_DONT_EXIST_EXCEPTION);
        }
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
        CommissionOut commissionOut = converter.commissionToOut(commissionRepository.save(commission));
        taskScheduler.schedule(new RunnableTask(commissionOut.getId(), commissionRepository),
                Date.from(commission.getClosingDate().atZone(ZoneId.systemDefault()).toInstant()));
        return commissionOut;
    }

    public List<CommissionOut> findAll() {
        return commissionRepository.findAll()
                .stream()
                .map(converter::commissionToOut)
                .collect(Collectors.toList());
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
        return converter.commissionToOut(commissionRepository.save(commissionEntity));
    }

}
