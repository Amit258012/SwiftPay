package com.swiftpay.user_service.security;

import com.swiftpay.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                             .map(user -> new org.springframework.security.core.userdetails.User(
                                     user.getEmail(),
                                     user.getPassword(),
                                     new ArrayList<>()
                             ))
                             .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
