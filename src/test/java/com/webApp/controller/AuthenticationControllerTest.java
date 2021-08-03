package com.webApp.controller;

import com.webApp.model.DeviceType;
import com.webApp.payload.DeviceInfo;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/delete_titles.sql","/delete_refresh_token.sql","/delete_user_device.sql","/delete_user_role.sql",
        "/insert_role.sql", "/insert_user.sql", "/insert_user_role.sql"})
public class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String LOGIN_URL = "http://localhost:8080/api/auth/login";

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
                                        "    \"password\": \"" + password+ "\",\n" +
                                        "    \"deviceInfo\": {\n" +
                                        "        \"deviceId\": \""+ info.getDeviceId() + "\",\n" +
                                        "        \"deviceType\": \"" + info.getDeviceType() +"\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() +"\"\n" +
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

    private void getJWTTokenWithIncorrectPassword() throws Exception {
        String email = "alexwoodyside@gmail.com";
        String password = "notsupersecretpasswordatall";
        DeviceInfo info = new DeviceInfo();
        info.setDeviceId("123456");
        info.setDeviceType(DeviceType.DEVICE_TYPE_ANDROID);
        info.setNotificationToken("78910");
        mockMvc
                .perform(
                        post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"email\": \"" + email + "\",\n" +
                                        "    \"password\": \"" + password+ "\",\n" +
                                        "    \"deviceInfo\": {\n" +
                                        "        \"deviceId\": \""+ info.getDeviceId() + "\",\n" +
                                        "        \"deviceType\": \"" + info.getDeviceType() +"\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() +"\"\n" +
                                        "    }\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());


    }

    private void getTokenWithIncorrectUsernameAndPassword() throws Exception {
        String email = "whatever@gmail.com";
        String password = "notsupersecretpasswordatall";
        DeviceInfo info = new DeviceInfo();
        info.setDeviceId("123456");
        info.setDeviceType(DeviceType.DEVICE_TYPE_ANDROID);
        info.setNotificationToken("78910");
        mockMvc
                .perform(
                        post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"email\": \"" + email + "\",\n" +
                                        "    \"password\": \"" + password+ "\",\n" +
                                        "    \"deviceInfo\": {\n" +
                                        "        \"deviceId\": \""+ info.getDeviceId() + "\",\n" +
                                        "        \"deviceType\": \"" + info.getDeviceType() +"\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() +"\"\n" +
                                        "    }\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void whenGetToken_ReturnCurrentJWTToken() throws Exception {
        getJWTToken();
    }

    @Test
    public void whenIncorrectPassword_thenReturnUnauthorized() throws Exception {
        getJWTTokenWithIncorrectPassword();
    }

    public void whenIncorrectUsernameAndPassword_thenReturnUnauthorized() throws Exception {
        getTokenWithIncorrectUsernameAndPassword();
    }
}
