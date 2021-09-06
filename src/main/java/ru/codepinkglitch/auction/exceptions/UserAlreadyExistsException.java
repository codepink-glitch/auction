package ru.codepinkglitch.auction.exceptions;

public class UserAlreadyExistsException extends AbstractException {
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
