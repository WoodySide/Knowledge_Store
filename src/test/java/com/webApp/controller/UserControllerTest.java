package com.webApp.controller;

import com.webApp.model.DeviceType;
import com.webApp.payload.DeviceInfo;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
@Sql({"/test_sql_scripts/delete_titles.sql",
        "/test_sql_scripts/delete_refresh_token.sql",
        "/test_sql_scripts/delete_user_device.sql",
        "/test_sql_scripts/delete_user_role.sql",
        "/test_sql_scripts/insert_role.sql",
        "/test_sql_scripts/insert_user.sql",
        "/test_sql_scripts/insert_user_role.sql"})
@ActiveProfiles(profiles = "test")
@FixMethodOrder(MethodSorters.JVM)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String LOGIN_URL = "http://localhost:8080/api/auth/login";
    private static final String USER_URL = "http://localhost:8080/api/user";

    private String getJWTTokenForUser() throws Exception {

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

    private String getJWTTokenForAdmin() throws Exception {
        String email = "whatever@mail.ru";
        String password = "secret10";
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
                                .header(HttpHeaders.AUTHORIZATION, getJWTTokenForUser())
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

    @WithMockUser(username = "steve", roles = "ADMIN")
    private void getAdminProfile() throws Exception {
        mockMvc
                .perform(
                        get(USER_URL + "/" + "admins")
                            .header(HttpHeaders.AUTHORIZATION, getJWTTokenForAdmin())
                )
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "steve", roles = "ADMIN")
    private void getAllUsers() throws Exception {
        mockMvc
                .perform(
                        get(USER_URL + "/" + "users")
                            .header(HttpHeaders.AUTHORIZATION, getJWTTokenForAdmin())
                )
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "bobby", roles = "USER")
    private void getAllUsersWithUserAccess() throws Exception {
        mockMvc
                .perform(
                        get(USER_URL + "/" + "users")
                                .header(HttpHeaders.AUTHORIZATION, getJWTTokenForUser())
                )
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "steve", roles = "ADMIN")
    private void getAllUsersWithInvalidJWTToken() throws Exception {
        mockMvc
                .perform(
                        get(USER_URL + "/" + "users")
                                .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI3ODkzMDI0LCJleHAiOjE2Mjc4OTM5MjQsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.AAf_MA1pNBUFzr6Wj0ERuYcu6AmYNttEst_t_eQgrKiRW9ZsD0c4moZQIce7sghAgEOs8TeBk4SAVvZ4aieeAQ")
                )
                .andExpect(status().isUnauthorized());
    }
    @WithMockUser(username = "steve", roles = "ADMIN")
    private void getAllUsersWithoutJWTToken() throws Exception {
        mockMvc
                .perform(
                        get(USER_URL + "/" + "users")
                )
                .andExpect(status().isUnauthorized());
    }


    private void getAdminProfileWithIncorrectToken() throws Exception {
        mockMvc
                .perform(
                        get(USER_URL + "/" + "admins")
                            .header(HttpHeaders.AUTHORIZATION, "$2a$10$.FJPOp79RdeSvs8MdOARqeaNLrbC1FaNTZsqMO9ZsIyaWlEj/CJgq")
                )
                .andExpect(status().isUnauthorized());
    }

    private void getAdminProfileWithNoJWTToken() throws Exception {
        mockMvc
                .perform(
                        get(USER_URL + "/" + "admins")
                )
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "bobby", roles = "USER")
    private void updateUserPassword() throws Exception {
        String oldPassword = "secret123";
        String newPassword = "secret12345";
        mockMvc
                .perform(
                        post(USER_URL + "/" + "password" + "/" + "update")
                            .header(HttpHeaders.AUTHORIZATION, getJWTTokenForUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "   \"oldPassword\": \"" + oldPassword + "\",\n" +
                                        "   \"newPassword\": \"" + newPassword + "\"\n" +
                                        "}")
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Password changed successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"));
    }

    @WithMockUser(username = "bobby", roles = "USER")
    private void updateUserPasswordWithNoOldOne() throws Exception {
        String newPassword = "secret12345";

        mockMvc
                .perform(
                        post(USER_URL + "/" + "password" + "/" + "update")
                            .header(HttpHeaders.AUTHORIZATION, getJWTTokenForUser())
                                 .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\n" +
                                        "   \"newPassword\": \"" + newPassword + "\"\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Old password must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));
    }

    private void updateUserPasswordWithNoNewOne() throws Exception {
        String oldPassword = "secret123";

        mockMvc
                .perform(
                        post(USER_URL + "/" + "password" + "/" + "update")
                            .header(HttpHeaders.AUTHORIZATION, getJWTTokenForUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\n" +
                                        "   \"oldPassword\": \"" + oldPassword + "\"\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("New password must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));
    }

    private void logoutUser() throws Exception {

        DeviceInfo info = new DeviceInfo();
        info.setDeviceId("123456");
        info.setDeviceType(DeviceType.DEVICE_TYPE_ANDROID);
        info.setNotificationToken("78910");

        mockMvc
                .perform(
                        post(USER_URL + "/" + "logout")
                              .header(HttpHeaders.AUTHORIZATION, getJWTTokenForUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\n" +
                                        "    \"deviceInfo\": {\n" +
                                        "        \"deviceId\": \"" + info.getDeviceId() + "\",\n" +
                                        "        \"deviceType\": \"" + info.getDeviceType() + "\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() + "\"\n" +
                                        "    }\n" +
                                        "}")
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Log out successful"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"));
    }

    private void logoutWithEmptyBody() throws Exception {

        mockMvc
                .perform(
                        post(USER_URL + "/" + "logout")
                                .header(HttpHeaders.AUTHORIZATION, getJWTTokenForUser())
                )
                .andExpect(status().isBadRequest());
    }

    private void logoutWithIncorrectDeviceInfoData() throws Exception {

        DeviceInfo info = new DeviceInfo();
        info.setDeviceId("000000");
        info.setDeviceType(DeviceType.DEVICE_TYPE_ANDROID);
        info.setNotificationToken("0000");

        mockMvc
                .perform(
                        post(USER_URL + "/" + "logout")
                                .header(HttpHeaders.AUTHORIZATION, getJWTTokenForUser())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\n" +
                                        "    \"deviceInfo\": {\n" +
                                        "        \"deviceId\": \"" + info.getDeviceId() + "\",\n" +
                                        "        \"deviceType\": \"" + info.getDeviceType() + "\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() + "\"\n" +
                                        "    }\n" +
                                        "}")
                )
                .andExpect(status().isExpectationFailed());
    }

    private void logoutWithoutDeviceInfoData() throws Exception {

        DeviceInfo info = new DeviceInfo();
        info.setDeviceId("000000");
        info.setDeviceType(DeviceType.DEVICE_TYPE_ANDROID);
        info.setNotificationToken("0000");

        mockMvc
                .perform(
                        post(USER_URL + "/" + "logout")
                                .header(HttpHeaders.AUTHORIZATION, getJWTTokenForUser())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\n" +
                                        "        \"deviceId\": \"" + info.getDeviceId() + "\",\n" +
                                        "        \"deviceType\": \"" + info.getDeviceType() + "\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() + "\"\n" +
                                        "    }\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Device info cannot be null"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));
    }

    private void logoutWithBlankDeviceId() throws Exception {

        DeviceInfo info = new DeviceInfo();
        info.setDeviceType(DeviceType.DEVICE_TYPE_ANDROID);
        info.setNotificationToken("0000");

        mockMvc
                .perform(
                        post(USER_URL + "/" + "logout")
                                .header(HttpHeaders.AUTHORIZATION, getJWTTokenForUser())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\n" +
                                        "    \"deviceInfo\": {\n" +
                                        "        \"deviceType\": \"" + info.getDeviceType() + "\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() + "\"\n" +
                                        "    }\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Device id cannot be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));
    }

    private void logoutWithoutDeviceType() throws Exception {

        DeviceInfo info = new DeviceInfo();
        info.setDeviceId("000000");
        info.setNotificationToken("0000");

        mockMvc
                .perform(
                        post(USER_URL + "/" + "logout")
                                .header(HttpHeaders.AUTHORIZATION, getJWTTokenForUser())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\n" +
                                        "    \"deviceInfo\": {\n" +
                                        "        \"deviceId\": \"" + info.getDeviceId() + "\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() + "\"\n" +
                                        "    }\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Device type cannot be null"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));;
    }

    private void logoutWithNoJWTToken() throws Exception {

        DeviceInfo info = new DeviceInfo();
        info.setDeviceId("123456");
        info.setDeviceType(DeviceType.DEVICE_TYPE_ANDROID);
        info.setNotificationToken("78910");

        mockMvc
                .perform(
                        post(USER_URL + "/" + "logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\n" +
                                        "    \"deviceInfo\": {\n" +
                                        "        \"deviceId\": \"" + info.getDeviceId() + "\",\n" +
                                        "        \"deviceType\": \"" + info.getDeviceType() + "\",\n" +
                                        "        \"notificationToken\": \"" + info.getNotificationToken() + "\"\n" +
                                        "    }\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());
    }

    @Before
    public void beforeTest() throws Exception {
        getJWTTokenForUser();
    }


    @Test
    public void whenGetUserProfile_then200() throws Exception {
        getUserProfile();
    }

    @Test
    public void whenGetAllUsers_thenReturn200() throws Exception {
        getAllUsers();
    }

    @Test
    public void whenGetAllUsersWithUserAccess_thenReturn403() throws Exception {
        getAllUsersWithUserAccess();
    }

    @Test
    public void whenGetAllUserWithInvalidJWTToken_thenReturnUnauthorized() throws Exception {
        getAllUsersWithInvalidJWTToken();
    }

    @Test
    public void whenGetAllUserWithoutJWTToken_thenReturnUnauthorized() throws Exception {
        getAllUsersWithoutJWTToken();
    }

    @Test
    public void whenGetUserProfileWithIncorrectJWTToken_thenNotAuthenticated() throws Exception {
        getUserProfileWithIncorrectJWTToken();
    }

    @Test
    public void whenGetUserProfileWithNoJWTToken_thenNotAuthenticated() throws Exception {
        getUserProfileWithNoJWTToken();
    }

    @Test
    public void whenGetAdminProfile_then200() throws Exception {
        getAdminProfile();
    }

    @Test
    public void whenGetAdminProfileWithIncorrectJWTToken_thenNotAuthenticated() throws Exception {
        getAdminProfileWithIncorrectToken();
    }

    @Test
    public void whenAdminUserProfileWithNoJWTToken_thenNotAuthenticated() throws Exception {
        getAdminProfileWithNoJWTToken();
    }

    @Test
    public void whenUpdateUserPassword_thenReturnUpdatedOne() throws Exception{
        updateUserPassword();
    }

    @Test
    public void whenUpdateUserPasswordWithoutOldOne_thenReturn400() throws Exception {
        updateUserPasswordWithNoOldOne();
    }

    @Test
    public void whenUpdateUserPasswordWithoutNewOne_thenReturn400() throws Exception {
        updateUserPasswordWithNoNewOne();
    }

    @Test
    public void whenLogoutUser_thenReturn200() throws Exception {
        getJWTTokenForUser();
        logoutUser();
    }

    @Test
    public void whenLogoutUserWithEmptyBody_then400() throws Exception {
        logoutWithEmptyBody();
    }

    @Test
    public void whenLogoutUserWithIncorrectDeviceInfoData_thenReturnExpectationFailed() throws Exception {
        logoutWithIncorrectDeviceInfoData();
    }

    @Test
    public void whenLogoutUserWithNoDeviceInfoData_thenReturn400() throws Exception {
        logoutWithoutDeviceInfoData();
    }

    @Test
    public void whenLogoutUserWithBlankDeviceId_thenReturn400() throws Exception {
        logoutWithBlankDeviceId();
    }

    @Test
    public void whenLogoutUserWithoutDeviceType_thenReturn400() throws Exception {
        logoutWithoutDeviceType();
    }

    @Test
    public void whenLogoutUserWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        logoutWithNoJWTToken();
    }
 }
