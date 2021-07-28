package com.webApp.security;

import com.webApp.cache.LoggedOutJwtTokenCache;
import com.webApp.event.OnUserLogoutSuccessEvent;
import com.webApp.exception_handling.InvalidTokenRequestException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class JwtTokenValidatorTest {

    private static final String jwtSecret = "testSecret";
    private static final long jwtExpiryInMs = 2500;

    @Mock
    private LoggedOutJwtTokenCache cache;

    private JwtTokenProvider tokenProvider;

    private JwtTokenValidator tokenValidator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.tokenProvider = new JwtTokenProvider(jwtSecret,jwtExpiryInMs);
        this.tokenValidator = new JwtTokenValidator(jwtSecret, cache);
    }

    @Test
    public void testValidateTokenThrowsExceptionWhenTokenIsDamaged() {
        String token = tokenProvider.generateTokenFromUserId(100L);
        OnUserLogoutSuccessEvent logoutEvent = stubLogoutEvent("U1", token);
        when(cache.getLogoutEventForToken(token)).thenReturn(logoutEvent);

        thrown.expect(InvalidTokenRequestException.class);
        thrown.expectMessage("Incorrect signature");
        tokenValidator.validateToken(token + "-Damage");
    }

    @Test
    public void testValidateTokenThrowsExceptionWhenTokenIsExprired() throws InterruptedException {
        String token = tokenProvider.generateTokenFromUserId(123L);
        TimeUnit.MILLISECONDS.sleep(jwtExpiryInMs);
        OnUserLogoutSuccessEvent logoutEvent = stubLogoutEvent("U1", token);
        when(cache.getLogoutEventForToken(token)).thenReturn(logoutEvent);
    }

    @Test
    public void testValidateTokenThrowsExceptionWhenItIsPresentInTokenCache() {
        String token = tokenProvider.generateTokenFromUserId(124L);
        OnUserLogoutSuccessEvent logoutEvent = stubLogoutEvent("U2", token);
        when(cache.getLogoutEventForToken(token)).thenReturn(logoutEvent);

        thrown.expect(InvalidTokenRequestException.class);
        thrown.expectMessage("Token corresponds to an already logged out user [U2]");
        tokenValidator.validateToken(token);
    }

    @Test
    public void testValidateTokenWorksWhenItIsNotPresentInTokenCache() {
        String token = tokenProvider.generateTokenFromUserId(100L);
        tokenValidator.validateToken(token);
        verify(cache,times(1)).getLogoutEventForToken(token);
    }

    private OnUserLogoutSuccessEvent stubLogoutEvent(String email, String token) {
        return new OnUserLogoutSuccessEvent(email,token,null);
    }
}
