package ru.codepinkglitch.auction.enums;

public enum ExceptionEnum {
    BID_EXCEPTION("You can not outbid with lower amount."),
    COMMISSION_DONT_EXIST_EXCEPTION("No such commission."),
    EMPTY_COMMISSION_EXCEPTION("Can not save empty commisssion."),
    USER_DONT_EXIST_EXCEPTION("No such user."),
    USER_ALREADY_EXISTS_EXCEPTION("User with such username already exists."),
    SCHEDULED_TASK_EXCEPTION("Something went wrong with scheduled task."),
    ACCESS_EXCEPTION("You are not allowed to do that."),
    PICTURE_EXCEPTION("No such picture."),
    COMMISSION_NOT_OVER_EXCEPTION("Commission not over yet, you can't see finished picture."),
    CONVERSION_EXCEPTION("Something went wrong with conversion.");


    private String message;

    ExceptionEnum(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
