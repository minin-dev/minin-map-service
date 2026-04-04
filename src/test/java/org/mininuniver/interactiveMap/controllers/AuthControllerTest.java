/*
 * This file is part of mininuniver-interactive-map-service.
 *
 * Copyright (C) 2026 Eiztrips
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.mininuniver.interactiveMap.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mininuniver.interactiveMap.auth.controller.AuthController;
import org.mininuniver.interactiveMap.auth.dto.AuthResponse;
import org.mininuniver.interactiveMap.auth.dto.LoginRequest;
import org.mininuniver.interactiveMap.auth.dto.LoginResponse;
import org.mininuniver.interactiveMap.auth.dto.RefreshTokenRequest;
import org.mininuniver.interactiveMap.auth.security.JwtUtil;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private UserDetails userDetails;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        userDetails = new User("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        loginRequest = new LoginRequest("admin", "password");
    }

    @Test
    void createAuthenticationToken_ok() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn("refresh-token");

        ResponseEntity<AuthResponse> response = authController.createAuthenticationToken(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("access-token");
        assertThat(response.getBody().getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void createAuthenticationToken_badCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authController.createAuthenticationToken(loginRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Неверное имя пользователя или пароль");
    }

    @Test
    void refreshToken_ok() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("valid-refresh-token");

        when(jwtUtil.isRefreshToken("valid-refresh-token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid-refresh-token")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.validateToken("valid-refresh-token", userDetails)).thenReturn(true);
        when(jwtUtil.generateToken(userDetails)).thenReturn("new-access-token");

        ResponseEntity<LoginResponse> response = authController.refreshToken(refreshRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("new-access-token");
    }

    @Test
    void refreshToken_notRefreshToken() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("access-token");

        when(jwtUtil.isRefreshToken("access-token")).thenReturn(false);

        assertThatThrownBy(() -> authController.refreshToken(refreshRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not a refresh token");
    }

    @Test
    void refreshToken_invalidToken() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalid-refresh-token");

        when(jwtUtil.isRefreshToken("invalid-refresh-token")).thenReturn(true);
        when(jwtUtil.extractUsername("invalid-refresh-token")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.validateToken("invalid-refresh-token", userDetails)).thenReturn(false);

        assertThatThrownBy(() -> authController.refreshToken(refreshRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    void logout_ok() {
        String authHeader = "Bearer valid-token";

        ResponseEntity<Map<String, String>> response = authController.logout(authHeader);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Logout successful");
        verify(jwtUtil).blacklistToken("valid-token");
    }

    @Test
    void logout_noBearer() {
        String authHeader = "invalid-header";

        ResponseEntity<Map<String, String>> response = authController.logout(authHeader);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("message", "Logout successful");
        verify(jwtUtil, never()).blacklistToken(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void checkToken_ok() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(authentication).getAuthorities();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<Map<String, Object>> response = authController.checkToken();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("valid", true);
        assertThat(response.getBody()).containsEntry("username", "admin");

        SecurityContextHolder.clearContext();
    }
}
