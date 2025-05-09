package com.flexit.user_management.service.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexit.user_management.components.JwtUtil;
import com.flexit.user_management.dao.RoleDao;
import com.flexit.user_management.dao.UserDao;
import com.flexit.user_management.dto.AuthDtoResponse;
import com.flexit.user_management.dto.RefreshTokenDto;
import com.flexit.user_management.dto.RoleDto;
import com.flexit.user_management.dto.UserDto;
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
        RoleDto role;
        RefreshToken token = null;
        String refreshToken = "";
        Long accessTokenValidity = iniConfiguration.getLong(IniConstant.ACCESS_TOKEN_VALIDITY);
        long refreshTokenValidity = iniConfiguration.getLong(IniConstant.REFRESH_TOKEN_VALIDITY);
        String accessToken = jwtUtil.generateToken(userDto.getUsername(), accessTokenValidity);
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);
        if (userDto.getRole() != null) {
            role = setUserRole(userDto.getId());
        } else {
            role = setUserRole(102L);
        }
        userDto.setRole(role);
        User user = objectMapper.convertValue(userDto, User.class);
        if (user.getUsername() != null) {
            token = setRefreshToken(user.getUsername(), refreshTokenValidity);
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
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public AuthDtoResponse validateUserByCredentials(String username, String password) {
        String accessToken = "";
        User userDetails = null;
        Long accessTokenValidity = iniConfiguration.getLong(IniConstant.ACCESS_TOKEN_VALIDITY);
        Long refreshTokenValidity = iniConfiguration.getLong(IniConstant.REFRESH_TOKEN_VALIDITY);
        try {
            if (username != null) {
                userDetails = getUserByUsername(username);
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
        accessToken = jwtUtil.generateToken(username, accessTokenValidity);
        RefreshToken token = setRefreshToken(username, refreshTokenValidity);
        refreshTokenService.updateInsertRefreshToken(userDetails.getId(), token.getRefreshToken(), token.getExpiryDate());
        return new AuthDtoResponse(accessToken, token.getRefreshToken(), accessTokenValidity, refreshTokenValidity);
    }

    @Override
    public AuthDtoResponse validateUserByToken(String refreshToken) {
        RefreshTokenDto token = null;
        Optional<User> user;
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
        if (!jwtUtil.validateToken(token.getRefreshToken(), username)) {
            throw new FlexitCustomException(HttpStatus.BAD_REQUEST, "Token is not valid", "Refresh token not valid");
        }
        if (jwtUtil.isTokenExpired(token.getExpiryDate())) {
            log.error("Refresh token expired");
            throw new FlexitCustomException(HttpStatus.NOT_ACCEPTABLE, "Refresh token expired", "Refresh token expired for the user");
        }
        String accessToken = jwtUtil.generateToken(username, accessTokenValidity);
        return new AuthDtoResponse(accessToken, refreshToken, accessTokenValidity, refreshTokenValidity);
    }

    private RoleDto setUserRole(Long roleID) {
        RoleDto role = new RoleDto();
        Optional<Role> roleObj = roleDao.findRoleById(roleID);
        role.setId(roleID);
        roleObj.ifPresent(value -> role.setName(value.getName()));
        return role;
    }

    private RefreshToken setRefreshToken(String username, Long refreshTokenValidity) {
        String refreshToken = jwtUtil.generateToken(username, refreshTokenValidity);
        RefreshToken token = new RefreshToken();
        token.setRefreshToken(refreshToken);
        token.setExpiryDate(Instant.now().toEpochMilli() + refreshTokenValidity);
        return token;
    }

}