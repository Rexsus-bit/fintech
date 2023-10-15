package org.projectweather.exceptions.dataBaseQuriesExceptions;

public class InvalidDataException extends RuntimeException{
    public InvalidDataException(String message) {
        super(message);
    }
}
