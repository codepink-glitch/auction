package ru.codepinkglitch.auction.exceptions;

public class UserDontExistException extends AbstractException {
    public UserDontExistException(String message){
        super(message);
    }
}
