package ru.codepinkglitch.auction.exceptions;

public abstract class AbstractException extends RuntimeException {
    protected AbstractException(String message){
        super(message);
    }
}
