package ui.main;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

public class Producers {


    @Produces
    @Named("url")
    public String getUrl() {
        return "jjj";
    }

    @Produces
    @Named("configDB")
    public String getDB() {
        return "jjj";
    }

}
