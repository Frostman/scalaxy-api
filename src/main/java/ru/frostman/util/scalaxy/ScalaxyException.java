package ru.frostman.util.scalaxy;

/**
 * @author slukjanov aka Frostman
 */
public class ScalaxyException extends Exception {

    public ScalaxyException() {
        super();
    }

    public ScalaxyException(String message) {
        super(message);
    }

    public ScalaxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScalaxyException(Throwable cause) {
        super(cause);
    }
}
