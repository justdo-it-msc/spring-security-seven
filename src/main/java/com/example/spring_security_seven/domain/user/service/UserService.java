package com.example.spring_security_seven.domain.user.service;

import com.example.spring_security_seven.domain.user.dto.UserRequestDto;
import com.example.spring_security_seven.domain.user.entity.UserEntity;
import com.example.spring_security_seven.domain.user.entity.UserRole;
import com.example.spring_security_seven.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /// 회원가입 메소드
    public void join(UserRequestDto dto) {

        String username = dto.getUsername();
        String password = dto.getPassword();

        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setPassword(passwordEncoder.encode(password));
        entity.setRole(UserRole.USER);

        userRepository.save(entity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity entity = userRepository.findByUsername(username).orElseThrow();

        return User.builder()
            .username(entity.getUsername())
            .password(entity.getPassword())
            .roles(entity.getRole().name())
            .build();
    }
}
