package com.webApp.cache;

import com.webApp.event.OnUserLogoutSuccessEvent;
import com.webApp.security.JwtTokenProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class LoggedOutJwtTokenCacheTest {

    @Mock
    private JwtTokenProvider mockTokenProvider;

    private LoggedOutJwtTokenCache cache;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.cache = new LoggedOutJwtTokenCache(10, mockTokenProvider);
    }

    @Test
    public void testMarkLogoutEventInsertsOnlyOnce() {
        OnUserLogoutSuccessEvent event = stubLogoutEvent("U1", "T1");
        when(mockTokenProvider.getTokenExpiryFromJWT("T1")).thenReturn(Date.from(Instant.now().plusSeconds(100)));

        cache.markLogoutEventForToken(event);
        cache.markLogoutEventForToken(event);
        cache.markLogoutEventForToken(event);
        verify(mockTokenProvider, times(1)).getTokenExpiryFromJWT("T1");
    }

    @Test
    public void getLogoutEventToken() {
        OnUserLogoutSuccessEvent event = stubLogoutEvent("U2", "T2");
        when(mockTokenProvider.getTokenExpiryFromJWT("T2")).thenReturn(Date.from(Instant.now().plusSeconds(10)));

        cache.markLogoutEventForToken(event);
        assertNull(cache.getLogoutEventForToken("T1"));
        assertNotNull(cache.getLogoutEventForToken("T2"));
    }

    private OnUserLogoutSuccessEvent stubLogoutEvent(String email, String token) {
        return new OnUserLogoutSuccessEvent(email, token, null);
    }
}
