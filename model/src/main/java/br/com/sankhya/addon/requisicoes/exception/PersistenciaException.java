package br.com.sankhya.addon.requisicoes.exception;

public class PersistenciaException extends Exception {

    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenciaException(String message) {
        super(message);
    }

    public PersistenciaException(Throwable cause) {
        super(cause);
    }
}
