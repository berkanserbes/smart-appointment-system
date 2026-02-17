package com.smartappointment.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartappointment.dto.auth.requests.LoginRequest;
import com.smartappointment.dto.auth.requests.RegisterRequest;
import com.smartappointment.dto.auth.responses.LoginResponse;
import com.smartappointment.dto.auth.responses.RegisterResponse;
import com.smartappointment.dto.user.responses.UserResponse;
import com.smartappointment.exception.BadRequestException;
import com.smartappointment.mapper.AuthMapper;
import com.smartappointment.model.entity.User;
import com.smartappointment.repository.UserRepository;
import com.smartappointment.security.JwtUtil;
import com.smartappointment.service.interfaces.IAuthService;

@Service
public class AuthService implements IAuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final AuthenticationManager authenticationManager;
        private final AuthMapper authMapper;

        public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil, AuthenticationManager authenticationManager, AuthMapper authMapper) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtUtil = jwtUtil;
                this.authenticationManager = authenticationManager;
                this.authMapper = authMapper;
        }

        @Override
        @Transactional
        public RegisterResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.email())) {
                        throw new BadRequestException("Email already registered: " + request.email());
                }

                User user = authMapper.toEntity(request, passwordEncoder.encode(request.password()));

                userRepository.save(user);

                return authMapper.toRegisterResponse(user);
        }

        @Override
        public LoginResponse login(LoginRequest request) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new BadRequestException("User not found"));

                String token = jwtUtil.generateToken(userDetails);

                return authMapper.toLoginResponse(token, jwtUtil.getExpiration(), user);
        }

        @Override
        public UserResponse getCurrentUser(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new BadRequestException("User not found with email: " + email));

                return authMapper.toUserResponse(user);
        }
}
