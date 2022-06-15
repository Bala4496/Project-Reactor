package ua.bala.project_reactor.exception;

public class ServiceException extends RuntimeException {
    String message;
    public ServiceException(String message) {
        super(message);
        this.message = message;
    }

    public ServiceException(Throwable ex) {
        super(ex);
        this.message = ex.getMessage();
    }
}
