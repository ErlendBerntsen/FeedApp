package no.hvl.dat250.jpa.basicexample.domain_primitives;

import com.fasterxml.jackson.annotation.JsonValue;

import static org.apache.commons.lang3.Validate.*;

public class Username {

    private final String username;

    public Username(final String username){
        notNull(username);
        notBlank(username);
        isTrue(username.length() >= 3 && username.length() <= 32);
        this.username = username;
    }

    @JsonValue
    public String toJson(){
        return this.username;
    }

    public String getUsername() {
        return username;
    }
}
