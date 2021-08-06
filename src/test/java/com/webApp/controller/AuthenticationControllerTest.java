package com.webApp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    private static final String PASSWORD_RESET = "http://localhost:8080/api/auth/password";

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
                                        "    \"password\": \"" + password + "\",\n" +
                                        "    \"deviceInfo\": {\n" +
                                        "        \"deviceId\": \"" + info.getDeviceId() + "\",\n" +
                                        "        \"deviceType\": \"" + info.getDeviceType() + "\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() + "\"\n" +
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
                                        "    \"password\": \"" + password + "\",\n" +
                                        "    \"deviceInfo\": {\n" +
                                        "        \"deviceId\": \"" + info.getDeviceId() + "\",\n" +
                                        "        \"deviceType\": \"" + info.getDeviceType() + "\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() + "\"\n" +
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
        String email = "alexwoodyside@gmai.com";
        String username = "alex";
        String password = "secret12345";
        String registerAsAsmin = "false";

        mockMvc
                .perform(
                        post(AUTH_URL + "/" + "register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "   \"email\": \"" + email + "\",\n" +
                                        "   \"username\": \"" + username + "\",\n" +
                                        "   \"password\": \"" + password + "\",\n" +
                                        "   \"registerAsAdmin\": " + registerAsAsmin + "\n" +
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
                                        "   \"username\": \"" + username + "\",\n" +
                                        "   \"password\": \"" + password + "\",\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Specify whether the user has to be registered as an admin or not"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));
    }

    private  void registerUserWithNoPassword() throws Exception {
        String email = "whatever@gmai.com";
        String username = "alex";
        String registerAsAsmin = "false";

        mockMvc
                .perform(
                        post(AUTH_URL + "/" + "register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "   \"email\": \"" + email + "\",\n" +
                                        "   \"username\": \"" + username + "\",\n" +
                                        "   \"registerAsAdmin\": " + registerAsAsmin + "\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Registration password cannot be null"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));
    }

    private  void registerUserWithAlreadyUsedMail() throws Exception {
        String email = "alexwoodyside@gmai.com";
        String username = "alex";
        String password = "secret12345";
        String registerAsAsmin = "false";

        mockMvc
                .perform(
                        post(AUTH_URL + "/" + "register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "   \"email\": \"" + email + "\",\n" +
                                        "   \"username\": \"" + username + "\",\n" +
                                        "   \"password\": \"" + password + "\",\n" +
                                        "   \"registerAsAdmin\": " + registerAsAsmin + "\n" +
                                        "}")
                )
                .andExpect(status().isConflict());

    }

    private void sendResetLinkToMail() throws Exception {
        String email = "alexwoodyside@gmail.com";
        String password = "notsupersecretpasswordatall";
        DeviceInfo info = new DeviceInfo();
        info.setDeviceId("123456");
        info.setDeviceType(DeviceType.DEVICE_TYPE_ANDROID);
        info.setNotificationToken("78910");
        mockMvc
                .perform(
                        post(PASSWORD_RESET + "/" + "resetlink")
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
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Password reset link sent successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"));

    }
    public void refreshToken() throws Exception {
        String refreshToken = getRefreshTokenFromJWT();
        mockMvc
                .perform(
                        post(AUTH_URL + "/" + "refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"refreshToken\": \"" + refreshToken + "\"\n" +
                                        "}\n")
                )
                .andExpect(status().isOk());
    }

    private  void refreshTokenWithNoRefreshToken() throws Exception {
        mockMvc
                .perform(
                        post(AUTH_URL + "/" + "refresh")

                )
                .andExpect(status().isBadRequest());
    }

    private  void refreshInvalidRefreshToken() throws Exception {

        mockMvc
                .perform(
                        post(AUTH_URL + "/" + "refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"refreshToken\": \"" + "814f3208-ff61-4a50-83z2-3b230f45qqf73" + "\"\n" +
                                        "}\n")
                )
                .andExpect(status().isExpectationFailed());
    }

    private  void sendResetLinkWithEmptyBody() throws Exception {
        mockMvc
                .perform(
                        post(PASSWORD_RESET + "/" + "resetlink")

                )
                .andExpect(status().isBadRequest());
    }


    private void registerUserWithNoUserData() throws Exception {
        mockMvc
                .perform(
                        post(AUTH_URL + "/" + "register")

                )
                .andExpect(status().isBadRequest());
    }


    private String getRefreshTokenFromJWT() throws Exception {
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

        Gson gson = new Gson();

        JsonObject jsonObjects = gson.fromJson(response, JsonObject.class);

        return jsonObjects.get("refreshToken").getAsString();
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

    @Test
    public void whenRegisterUserWithAlreadyUserMail_thenReturnConflict() throws Exception {
        registerUser();
        registerUserWithAlreadyUsedMail();
    }

    @Test
    public void whenResetLinkNeeded_thenSendItToEmail() throws Exception {
        sendResetLinkToMail();
    }

    @Test
    public void whenResetLinkNeededButWithEmptyBody_then400() throws Exception {
        sendResetLinkWithEmptyBody();
    }

    @Test
    public void whenRefreshToken_thenReturnRefreshedOne() throws Exception {
        refreshToken();
    }

    @Test
    public void whenGetRefreshTokenFromJwtToken_thenReturnRefreshToken() throws Exception {
        getRefreshTokenFromJWT();
    }

    @Test
    public void whenRefreshTokenWithNoToken_thenReturn400() throws Exception {
        refreshTokenWithNoRefreshToken();
    }

    @Test
    public void whenRefreshTokenIsInvalid_thenReturn400() throws Exception {
        refreshInvalidRefreshToken();
    }
}

