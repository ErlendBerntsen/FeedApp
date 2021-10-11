package no.hvl.dat250.jpa.basicexample.dao;

import no.hvl.dat250.jpa.basicexample.entities.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository

public interface UserDAO extends JpaRepository<UserClass, Long> {
    Optional<UserClass> findByUsernameAndPassword(String username, String password);
}
