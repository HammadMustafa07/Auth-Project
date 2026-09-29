package com.example.secureauth.exception;

public class UserProvisioningException extends RuntimeException{
    public UserProvisioningException(String message, Throwable cause) {
        super(message, cause);
    }
}

//Why do we need this?
//Suppose PostgreSQL fails while creating the user.
//We don't want our UserService to expose a low-level database exception throughout the application.

//PostgreSQL exception
//        ↓
//UserProvisioningException
//        ↓
//application-level meaning

//this is our first custom application exception