package com.webApp.controller;

import com.webApp.model.DeviceType;
import com.webApp.payload.DeviceInfo;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/delete_titles.sql","/delete_refresh_token.sql","/delete_user_device.sql","/delete_user_role.sql",
        "/insert_role.sql", "/insert_user.sql", "/insert_user_role.sql"})
@ActiveProfiles(profiles = "test")
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String LOGIN_URL = "http://localhost:8080/api/auth/login";
    private static final String USER_URL = "http://localhost:8080/api/user";

    private String getJWTToken() throws Exception {
        String email = "alexwoodyside@gmail.com";
        String password = "secret123";
        DeviceInfo info = new DeviceInfo();
        info.setDeviceId("123456");
        info.setDeviceType(DeviceType.DEVICE_TYPE_ANDROID);
        info.setNotificationToken("78910");

        String response = mockMvc
                .perform(
                        post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"email\": \"" + email + "\",\n" +
                                        "    \"password\": \"" + password + "\",\n" +
                                        "    \"deviceInfo\": {\n" +
                                        "        \"deviceId\": \"" + info.getDeviceId() + "\",\n" +
                                        "        \"deviceType\": \"" + info.getDeviceType() + "\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() + "\"\n" +
                                        "    }\n" +
                                        "}")

                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken")
                        .isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tokenType").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.expiryDuration").value(900000))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONObject jsonObject = new JSONObject(response);
        return "Bearer " + jsonObject.get("accessToken");
    }

    @WithMockUser(username = "bobby", roles = "USER")
    private void getUserProfile() throws Exception {
        mockMvc
                .perform(
                        get(USER_URL + "/" + "me")
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isOk());
    }

    private void getUserProfileWithIncorrectJWTToken() throws Exception{
        mockMvc
                .perform(
                        get(USER_URL + "/" + "me")
                            .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI4MjM0NDgyLCJleHAiOjE2MjgyMzUzODIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.LT6FpAfaLLbyBUk6uL9jb1Xa6Ae5mzlAwgD5DAmKUA2nPBBZxp41_j_OmnCNY74_yNiET7qwo-VWQzREsRJTpw")
                )
                .andExpect(status().isUnauthorized());
    }

    private void getUserProfileWithNoJWTToken() throws Exception {
        mockMvc
                .perform(
                        get(USER_URL + "/" + "me")
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void whenGetUserProfile_the200() throws Exception {
        getUserProfile();
    }

    @Test
    public void whenGetUserProfileWithInvalidJWTToken_thenNotAuthenticated() throws Exception {
        getUserProfileWithIncorrectJWTToken();
    }

    @Test
    public void whenGetUserProfileWithNoJWTToken_thenNotAuthenticated() throws Exception {
        getUserProfileWithNoJWTToken();
    }
}
