package com.flexit.user_management.service.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexit.user_management.components.JwtUtil;
import com.flexit.user_management.dao.RoleDao;
import com.flexit.user_management.dao.UserDao;
import com.flexit.user_management.dto.*;
import com.flexit.user_management.exception.FlexitCustomException;
import com.flexit.user_management.model.Address;
import com.flexit.user_management.model.RefreshToken;
import com.flexit.user_management.model.Role;
import com.flexit.user_management.model.User;
import com.flexit.user_management.service.RefreshTokenService;
import com.flexit.user_management.service.UserService;
import com.flexit.user_management.util.constants.IniConstant;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final ObjectMapper objectMapper;
    private final UserDao userDao;
    private final Configuration iniConfiguration;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RoleDao roleDao;


    @Override
    @Transactional
    public AuthDtoResponse save(UserDto userDto) {
        RoleDto roleDto;
        RefreshToken token = null;
        String refreshToken = "";
        Long accessTokenValidity = iniConfiguration.getLong(IniConstant.ACCESS_TOKEN_VALIDITY);
        long refreshTokenValidity = iniConfiguration.getLong(IniConstant.REFRESH_TOKEN_VALIDITY);
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);
        if (userDto.getRole() != null) {
            roleDto = setUserRole(userDto.getRole().getId());
        } else {
            roleDto = setUserRole(102L);
        }
        String role = String.valueOf(roleDto.getName());
        String accessToken = jwtUtil.generateToken(userDto.getUsername(), role, accessTokenValidity);
        userDto.setRole(roleDto);
        User user = objectMapper.convertValue(userDto, User.class);
        if (user.getUsername() != null) {
            token = setRefreshToken(user.getUsername(), role, refreshTokenValidity);
            token.setUser(user);
            refreshToken = token.getRefreshToken();
        }
        List<Address> addresses = userDto.getAddresses().stream().map(a -> objectMapper.convertValue(a, Address.class)).toList();
        addresses.forEach(s -> s.setUser(user));
        user.setAddresses(addresses);
        try {
            refreshTokenService.saveRefreshToken(token);
            userDao.save(user);
        } catch (FlexitCustomException e) {
            log.error(e.getError());
            throw new FlexitCustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to save user", "User not saved due to some error");
        }
        log.info("User saved successfully.");
        return new AuthDtoResponse(accessToken, refreshToken, accessTokenValidity, refreshTokenValidity);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userDao.findByUsername(username);
        return objectMapper.convertValue(user, UserDto.class);
    }

    @Override
    public AuthDtoResponse validateUserByCredentials(String username, String password) {
        String accessToken = "";
        User userDetails = null;
        Long accessTokenValidity = iniConfiguration.getLong(IniConstant.ACCESS_TOKEN_VALIDITY);
        Long refreshTokenValidity = iniConfiguration.getLong(IniConstant.REFRESH_TOKEN_VALIDITY);
        try {
            if (username != null) {
                UserDto userDto = getUserByUsername(username);
                userDetails = objectMapper.convertValue(userDto, User.class);
            }
        } catch (FlexitCustomException e) {
            log.error("Error occurred while fetching user");
            throw new FlexitCustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while fetching user", "Error occurred while fetching user form username");
        }
        if (userDetails == null) {
            throw new FlexitCustomException(HttpStatus.BAD_REQUEST, "User not found", "User not found");
        }
        if (!userDetails.getUsername().equals(username)
            || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new FlexitCustomException(HttpStatus.BAD_REQUEST, "Username password does not match", "Username and password do not match");
        }
        String role = String.valueOf(userDetails.getRole().getName());
        accessToken = jwtUtil.generateToken(username, role, accessTokenValidity);
        RefreshToken token = setRefreshToken(username, role, refreshTokenValidity);
        refreshTokenService.updateInsertRefreshToken(userDetails.getId(), token.getRefreshToken(), token.getExpiryDate());
        return new AuthDtoResponse(accessToken, token.getRefreshToken(), accessTokenValidity, refreshTokenValidity);
    }

    @Override
    public AuthDtoResponse validateUserByToken(String refreshToken) {
        RefreshTokenDto token = null;
        Long accessTokenValidity = iniConfiguration.getLong(IniConstant.ACCESS_TOKEN_VALIDITY);
        Long refreshTokenValidity = iniConfiguration.getLong(IniConstant.REFRESH_TOKEN_VALIDITY);
        try {
            token = refreshTokenService.findByRefreshToken(refreshToken);
        } catch (FlexitCustomException e) {
            log.error("Exception occurred while fetching refresh token");
            throw new FlexitCustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception occurred while fetching refresh token", "Exception occurred while fetching refresh token");
        }
        if (token == null) {
            log.error("Token not found for the user");
            throw new FlexitCustomException(HttpStatus.NOT_ACCEPTABLE, "Token not found", "Token not found for the user");
        }
        String username = token.getUser().getUsername();
        UserDto userDto = getUserByUsername(username);
        User userDetails = objectMapper.convertValue(userDto, User.class);
        String role = String.valueOf(userDetails.getRole().getName());
        if (!jwtUtil.validateToken(token.getRefreshToken(), username)) {
            throw new FlexitCustomException(HttpStatus.BAD_REQUEST, "Token is not valid", "Refresh token not valid");
        }
        if (jwtUtil.isRefreshTokenExpired(token.getExpiryDate())) {
            log.error("Refresh token expired");
            throw new FlexitCustomException(HttpStatus.NOT_ACCEPTABLE, "Refresh token expired", "Refresh token expired for the user");
        }
        String accessToken = jwtUtil.generateToken(username, role, accessTokenValidity);
        return new AuthDtoResponse(accessToken, refreshToken, accessTokenValidity, refreshTokenValidity);
    }

    @Override
    public void logoutUser(Long userId) {
        if (userId != null) {
            refreshTokenService.deleteRefreshTokenByUserid(userId);
        } else {
            throw new FlexitCustomException(HttpStatus.BAD_REQUEST, "User Id cannot be null", "Please fill the user id");
        }
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateDto userDto, String username) {
        Optional<User> existingUserInfo = Optional.ofNullable(userDao.findByUsername(username));
        if (existingUserInfo.isPresent()) {
            existingUserInfo.get().setFirstName(userDto.getFirstName());
            existingUserInfo.get().setLastName(userDto.getLastName());
            existingUserInfo.get().setEmail(userDto.getEmail());
            existingUserInfo.get().setAge(userDto.getAge());
            existingUserInfo.get().setPhoneNumber(userDto.getPhoneNumber());
            try {
                userDao.save(existingUserInfo.get());
            } catch (FlexitCustomException e) {
                log.error("Exception occurred while updating User {}", e.getDescription());
                throw new FlexitCustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception occurred while updating user", "Exception occurred while updating user");
            }
        } else {
            throw new FlexitCustomException(HttpStatus.BAD_REQUEST, "User not found", "User not found for the username");
        }
    }

    private RoleDto setUserRole(Long roleID) {
        RoleDto role = new RoleDto();
        Optional<Role> roleObj = roleDao.findRoleById(roleID);
        role.setId(roleID);
        roleObj.ifPresent(roleValue -> role.setName(roleValue.getName()));
        return role;
    }

    private RefreshToken setRefreshToken(String username, String role, Long refreshTokenValidity) {
        String refreshToken = jwtUtil.generateToken(username, role, refreshTokenValidity);
        RefreshToken token = new RefreshToken();
        token.setRefreshToken(refreshToken);
        token.setExpiryDate(Instant.now().toEpochMilli() + refreshTokenValidity);
        return token;
    }

}