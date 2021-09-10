package ru.codepinkglitch.auction.exceptions;


// TODO: 9/9/2021 make 1 Service exception with EnumType
public class CommissionDontExistException extends AbstractException{
    public CommissionDontExistException(String message){
        super(message);
    }
}
