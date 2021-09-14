package ru.codepinkglitch.auction.runnables;

import org.springframework.transaction.annotation.Transactional;
import ru.codepinkglitch.auction.enums.BidStatus;
import ru.codepinkglitch.auction.entities.CommissionEntity;
import ru.codepinkglitch.auction.enums.Status;
import ru.codepinkglitch.auction.exceptions.ServiceException;
import ru.codepinkglitch.auction.repositories.CommissionRepository;


public class RunnableTask implements Runnable{


    private CommissionRepository commissionRepository;
    private Long id;

    public RunnableTask(Long id, CommissionRepository commissionRepository){
        this.commissionRepository = commissionRepository;
        this.id = id;
    }

    @Override
    @Transactional
    public void run() {
        CommissionEntity commissionEntity = commissionRepository.findById(id).orElseThrow(ServiceException::new);
        commissionEntity.setStatus(Status.CLOSED);
        commissionEntity.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.HIGHEST))
                .findFirst()
                .orElseThrow(ServiceException::new)
                .setBidStatus(BidStatus.WON);
        commissionRepository.save(commissionEntity);
    }


}
