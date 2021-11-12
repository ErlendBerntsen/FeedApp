package no.hvl.dat250.jpa.basicexample.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.hvl.dat250.jpa.basicexample.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class ApplicationUser implements UserDetails {
    private final UUID id;
    private final String username;
    private final UserType userType;

    @JsonIgnore
    private final Set<? extends GrantedAuthority> grantedAuthorities;
    @JsonIgnore
    private final String password;
    @JsonIgnore
    private final boolean isAccountNonExpired;
    @JsonIgnore
    private final boolean isAccountNonLocked;
    @JsonIgnore
    private final boolean isCredentialsNonExpired;
    @JsonIgnore
    private final boolean isEnabled;

    public ApplicationUser(UUID id,
                           String username,
                           String password,
                           UserType userType,
                           Set<? extends GrantedAuthority> grantedAuthorities,
                           boolean isAccountNonExpired,
                           boolean isAccountNonLocked,
                           boolean isCredentialsNonExpired,
                           boolean isEnabled) {
        this.id = id;
        this.grantedAuthorities = grantedAuthorities;
        this.password = password;
        this.username = username;
        this.userType = userType;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return isEnabled;
    }

    public UUID getId() {
        return id;
    }

    public UserType getUserType(){return userType;}

}
