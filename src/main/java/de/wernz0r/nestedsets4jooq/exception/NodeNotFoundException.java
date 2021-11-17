package de.wernz0r.nestedsets4jooq.exception;

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
