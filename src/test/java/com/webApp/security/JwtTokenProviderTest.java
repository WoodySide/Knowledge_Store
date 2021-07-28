package com.webApp.security;

import com.webApp.model.CustomUserDetails;
import com.webApp.model.Role;
import com.webApp.model.RoleName;
import com.webApp.model.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JwtTokenProviderTest {

    private static final String jwtSecret = "testSecret";
    private static final long jwtExpiryInMs = 25000;

    private JwtTokenProvider tokenProvider;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.tokenProvider = new JwtTokenProvider(jwtSecret,jwtExpiryInMs);
    }

    @Test
    public void testGetUserIdFromJWT() {
        String token = tokenProvider.generateToken(stubCustomerUser());
        assertEquals(100, tokenProvider.getUserIdFromJWT(token).longValue());
    }

    @Test
    public void testGetTokenExpiryFromJWT() {
        String token = tokenProvider.generateTokenFromUserId(120L);
        assertNotNull(tokenProvider.getTokenExpiryFromJWT(token));
    }

    @Test
    public void testGetExpiryDuration() {
        assertEquals(jwtExpiryInMs, tokenProvider.getExpiryDuration());
    }

    @Test
    public void testGetAuthoritiesFromJWT() {
        String token = tokenProvider.generateToken(stubCustomerUser());
        assertNotNull(tokenProvider.getAuthoritiesFromJWT(token));
    }

    private CustomUserDetails stubCustomerUser() {
        User user = new User();
        user.setId((long) 100);
        user.setRoles(Collections.singleton(new Role(RoleName.ROLE_ADMIN)));
        return new CustomUserDetails(user);
    }
}
