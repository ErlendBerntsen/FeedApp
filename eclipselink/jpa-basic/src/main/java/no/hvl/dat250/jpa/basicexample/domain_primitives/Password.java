package no.hvl.dat250.jpa.basicexample.domain_primitives;

import com.fasterxml.jackson.annotation.JsonValue;

import static org.apache.commons.lang3.Validate.*;

public class Password {

    private final String password;

    public Password(final String password){
        notNull(password);
        notBlank(password);
        isTrue(password.length() >= 8 && password.length() <= 128);
        this.password = password;
    }

    @JsonValue
    public String toJson(){
        return this.password;
    }

    public String getPassword() {
        return password;
    }
}
