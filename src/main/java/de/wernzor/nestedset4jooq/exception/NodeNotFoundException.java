package de.wernzor.nestedset4jooq.exception;

public class NodeNotFoundException extends RuntimeException {

    public NodeNotFoundException() {
        super();
    }

    public NodeNotFoundException(String message) {
        super(message);
    }

    public NodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
