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
public class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String LOGIN_URL = "http://localhost:8080/api/auth/login";
    private static final String AUTH_URL = "http://localhost:8080/api/auth";

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

    private void checkNotUsedMail(String data) throws Exception {
        String email = "whatever@gmail.com";
        mockMvc
                .perform(
                        get(AUTH_URL + "/" + "checkEmailInUse?")
                        .param("email", email)
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(data));
    }

    private void checkUsedMail(String data) throws Exception {
        String email = "alexwoodyside@gmail.com";
        mockMvc
                .perform(
                        get(AUTH_URL + "/" + "checkEmailInUse?")
                        .param("email", email)
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(data));
    }

    private void checkNotUsedUsername(String data) throws Exception {
        String username = "whateverName";
        mockMvc
                .perform(
                        get(AUTH_URL + "/" + "checkUsernameInUse?")
                        .param("username", username)
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(data));
    }

    private void checkUsedUsername(String data) throws Exception {
        String username = "bobby";
        mockMvc
                .perform(
                        get(AUTH_URL + "/" + "checkUsernameInUse?")
                        .param("username", username)
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(data));
    }

    private void registerUser() throws Exception {
        String email = "whatever@gmai.com";
        String username = "alex";
        String password = "secret12345";
        String registerAsAsmin = "false";

        mockMvc
                .perform(
                        post(AUTH_URL + "/" + "register")
                            .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "   \"email\": \"" + email + "\",\n" +
                                        "   \"username\": \""+ username + "\",\n" +
                                        "   \"password\": \""+ password + "\",\n" +
                                        "   \"registerAsAdmin\": " + registerAsAsmin+ "\n" +
                                        "}")
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("User registered successfully. Check your email for verification"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"));
    }

    private void registerUserWithNoRole() throws Exception {
        String email = "whatever@gmai.com";
        String username = "alex";
        String password = "secret12345";
        mockMvc
                .perform(
                        post(AUTH_URL + "/" + "register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "   \"email\": \"" + email + "\",\n" +
                                        "   \"username\": \""+ username + "\",\n" +
                                        "   \"password\": \""+ password + "\",\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Specify whether the user has to be registered as an admin or not"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));
    }

    public void registerUserWithNoPassword() throws Exception {
        String email = "whatever@gmai.com";
        String username = "alex";
        String registerAsAsmin = "false";

        mockMvc
                .perform(
                        post(AUTH_URL + "/" + "register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "   \"email\": \"" + email + "\",\n" +
                                        "   \"username\": \""+ username + "\",\n" +
                                        "   \"registerAsAdmin\": " + registerAsAsmin+ "\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Registration password cannot be null"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));
    }

    public void registerUserWithNoUserData() throws Exception {
        mockMvc
                .perform(
                        post(AUTH_URL + "/" + "register")

                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenGetToken_ReturnCurrentJWTToken() throws Exception {
        getJWTToken();
    }

    @Test
    public void whenIncorrectPassword_thenReturnUnauthorized() throws Exception {
        getJWTTokenWithIncorrectPassword();
    }

    @Test
    public void whenIncorrectUsernameAndPassword_thenReturnUnauthorized() throws Exception {
        getTokenWithIncorrectUsernameAndPassword();
    }

    @Test
    public void whenEmailIsNotInUse_returnFalse() throws Exception {
        checkNotUsedMail("false");
    }

    @Test
    public void whenEmailIsInUse_thenReturnTrue() throws Exception {
        checkUsedMail("true");
    }

    @Test
    public void whenUsernameIsNotInUse_thenReturnFalse() throws Exception {
        checkNotUsedUsername("false");
    }

    @Test
    public void whenUsernameIsInUser_thenReturnTrue() throws Exception {
        checkUsedUsername("true");
    }

    @Test
    public void whenRegisterUser_thenReturn200() throws Exception {
        registerUser();
    }

    @Test
    public void whenRegisterUserWithNoRole_thenReturn400() throws Exception {
        registerUserWithNoRole();
    }

    @Test
    public void whenRegisterUserWithNoPassword_thenReturn400() throws Exception {
        registerUserWithNoPassword();
    }

    @Test
    public void whenRegisterUserWithNoData_thenReturn400() throws Exception {
        registerUserWithNoUserData();
    }
 }
