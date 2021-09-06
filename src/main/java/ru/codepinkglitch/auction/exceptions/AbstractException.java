package ru.codepinkglitch.auction.exceptions;

public abstract class AbstractException extends RuntimeException {
    public AbstractException(String message){
        super(message);
    }
}
