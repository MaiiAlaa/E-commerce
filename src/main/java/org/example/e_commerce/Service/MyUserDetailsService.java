package org.example.e_commerce.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Implement your logic to load the user by username
        // Example:
        // return new org.springframework.security.core.userdetails.User(username, password, authorities);
        return null; // Replace with actual UserDetails object
    }
}
