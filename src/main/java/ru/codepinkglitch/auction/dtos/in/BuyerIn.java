package ru.codepinkglitch.auction.dtos.in;

import lombok.Data;

import java.util.List;

@Data
public class BuyerIn extends AbstractUserIn{

    private List<BidIn> bids;

}
