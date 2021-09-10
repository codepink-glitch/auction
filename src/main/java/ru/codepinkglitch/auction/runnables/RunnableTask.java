package ru.codepinkglitch.auction.runnables;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.transaction.annotation.Transactional;
import ru.codepinkglitch.auction.entities.BidStatus;
import ru.codepinkglitch.auction.entities.CommissionEntity;
import ru.codepinkglitch.auction.entities.Status;
import ru.codepinkglitch.auction.exceptions.TimerException;
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
        CommissionEntity commissionEntity = commissionRepository.findById(id).orElseThrow(TimerException::new);
        commissionEntity.setStatus(Status.CLOSED);
        commissionEntity.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.HIGHEST))
                .findFirst()
                .orElseThrow(TimerException::new)
                .setBidStatus(BidStatus.WON);
        commissionRepository.save(commissionEntity);
    }


}
