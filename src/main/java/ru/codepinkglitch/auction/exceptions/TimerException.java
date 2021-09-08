package ru.codepinkglitch.auction.exceptions;

public class TimerException extends AbstractException {
    public TimerException(String message){
        super(message);
    }

    public TimerException(){
        super("Something went wrong with timer.");
    }
}
