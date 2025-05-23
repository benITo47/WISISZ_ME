package me.wisisz.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

public class AppException {
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class UserAlreadyExistsException extends Exception {
        public UserAlreadyExistsException(String msg) {
            super(msg);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class LoginFailedException extends Exception {
        public LoginFailedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidTokenException extends Exception {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BadRequestException extends Exception {
        public BadRequestException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class UserNotInTeamException extends Exception {
        public UserNotInTeamException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFoundException extends Exception {
        public NotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class UnexpectedException extends Exception {
        public UnexpectedException(String message) {
            super(message);
        }
    }
}
