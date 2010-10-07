package ru.frostman.util.scalaxy;


/**
 * @author slukjanov aka Frostman
 */
public enum ScalaxyStatus {
    SC_200("OK"),
    SC_201("Object created"),
    SC_204("Object deleted"),
    SC_401("Permission denied"),
    SC_403("Forbidden"),
    SC_404("Object not found"),
    SC_406("Incorrect JSON format"),
    SC_422("Incorrect request data"),
    SC_500("Internal API error");

    private String description;

    private ScalaxyStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ScalaxyStatus getByHttpStatus(int status) {
        return valueOf("SC_" + status);
    }
}
