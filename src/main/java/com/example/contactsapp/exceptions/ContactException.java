package com.example.contactsapp.exceptions;

public class ContactException extends Exception {
    ErrorCode errorCode;

    public ContactException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public String showMessage(){
        return errorCode + " " + super.getMessage();
    }
}
