package ru.codepinkglitch.auction.exceptions;

public class CommissionDontExistException extends AbstractException{
    public CommissionDontExistException(String message){
        super(message);
    }
}
