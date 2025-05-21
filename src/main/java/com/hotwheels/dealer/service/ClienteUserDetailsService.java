package com.hotwheels.dealer.service;

import com.hotwheels.dealer.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ClienteUserDetailsService implements UserDetailsService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return clienteRepository.findByUsernameAndActivoTrue(username)
                .orElseGet(() -> clienteRepository.findByEmailAndActivoTrue(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username)));
    }
}