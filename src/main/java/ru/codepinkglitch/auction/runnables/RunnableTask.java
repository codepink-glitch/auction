package ru.codepinkglitch.auction.runnables;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.codepinkglitch.auction.enums.BidStatus;
import ru.codepinkglitch.auction.entities.CommissionEntity;
import ru.codepinkglitch.auction.enums.Status;
import ru.codepinkglitch.auction.exceptions.ServiceException;
import ru.codepinkglitch.auction.repositories.CommissionRepository;

@RequiredArgsConstructor
public class RunnableTask implements Runnable{

    private final CommissionRepository commissionRepository;
    private final Long commissionId;


    @Override
    @Transactional
    public void run() {
        CommissionEntity commissionEntity = commissionRepository.findById(commissionId).orElseThrow(ServiceException::new);
        commissionEntity.setStatus(Status.CLOSED);
        commissionEntity.getBids().stream()
                .filter(x -> x.getBidStatus().equals(BidStatus.HIGHEST))
                .findFirst()
                .orElseThrow(ServiceException::new)
                .setBidStatus(BidStatus.WON);
        commissionRepository.save(commissionEntity);
    }


}
