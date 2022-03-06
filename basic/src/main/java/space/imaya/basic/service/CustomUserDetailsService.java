package space.imaya.basic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import space.imaya.basic.domain.User;
import space.imaya.basic.model.CustomUserDetails;
import space.imaya.basic.repository.UserRepository;

import java.util.function.Supplier;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        Supplier<UsernameNotFoundException> s =
                () -> new UsernameNotFoundException("Username" + username + "is invalid!");

        User u = userRepository.findUserByUsername(username).orElseThrow(s);

        return new CustomUserDetails(u);
    }
}
