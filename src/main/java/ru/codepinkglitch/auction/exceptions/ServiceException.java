package ru.codepinkglitch.auction.exceptions;


import ru.codepinkglitch.auction.enums.ExceptionEnum;

public class ServiceException extends AbstractException{

    private final ExceptionEnum exceptionEnum;

    public ServiceException(ExceptionEnum exceptionEnum){
        super(exceptionEnum.getMessage());
        this.exceptionEnum = exceptionEnum;
    }

    public ServiceException(){
        super(ExceptionEnum.SCHEDULED_TASK_EXCEPTION.getMessage());
        this.exceptionEnum = ExceptionEnum.SCHEDULED_TASK_EXCEPTION;
    }

    public ExceptionEnum getExceptionEnum(){
        return exceptionEnum;
    }

}
