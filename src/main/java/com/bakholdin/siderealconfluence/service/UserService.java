package com.bakholdin.siderealconfluence.service;

import com.bakholdin.siderealconfluence.dto.RegisterDto;
import com.bakholdin.siderealconfluence.dto.UpdatePasswordDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.entity.User;
import com.bakholdin.siderealconfluence.mapper.UserMapper;
import com.bakholdin.siderealconfluence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDto registerUser(RegisterDto registerDto) {
        userRepository.findByUsername(registerDto.getUsername())
                .ifPresent(u -> {
                    throw new RuntimeException("Username is already taken!");
                });

        User user = User.builder()
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .build();
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    public void updatePassword(UserDto userDto, UpdatePasswordDto updatePasswordDto) {
        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        if (!passwordEncoder.matches(updatePasswordDto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
        userRepository.save(user);
    }
}
