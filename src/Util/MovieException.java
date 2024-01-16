package Util;

/**
 * @author ThePirateBayV2
 * Application wide exception to contain an exception in The Move Collections system
 */


public class MovieException extends Exception {

    // Blank Exception handling
    public MovieException() {}

    // Exception with a custom message
    public MovieException(String msg) { super(msg); }

    // Exception with a custom message and cause(from the superclass Exception)
    public MovieException(String msg, Exception cause) { super(msg, cause); }

    // Exception that is just a throwable object.
    public MovieException(Throwable cause) { super(cause); }

    // ?? Fancy
    public MovieException(String msg, Throwable cause, boolean enableSuppresion, boolean writableStackTrace) {
        super(msg, cause, enableSuppresion, writableStackTrace);
    }

}
