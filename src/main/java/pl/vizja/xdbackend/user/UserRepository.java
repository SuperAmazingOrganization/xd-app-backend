package pl.vizja.xdbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByUsernameOrPhoneOrEmail(String username, String phone, String email);
    User findByUsernameOrPhoneOrEmail(String username, String phone, String email);
    boolean existsByPhone(String phone);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User findUserById(Long id);

}
