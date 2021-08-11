package com.webApp.controller;

import com.jayway.jsonpath.JsonPath;
import com.webApp.model.DeviceType;
import com.webApp.payload.DeviceInfo;
import com.webApp.repository.TitleRepository;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/delete_category.sql","/delete_titles.sql","/delete_refresh_token.sql","/delete_user_device.sql","/delete_user_role.sql",
        "/insert_role.sql", "/insert_user.sql", "/insert_user_role.sql"})
@ActiveProfiles(profiles = "test")
public class TitleControllerTest {

    private static final String TITLE_URL = "http://localhost:8080/api/user/titles";
    private static final String LOGIN_URL = "http://localhost:8080/api/auth/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TitleRepository titleRepository;

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

    private void getTitle(Integer id, String name) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private void getTitleAfterUpdate(Integer id, String name) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" + id)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name));
    }

    private void getTitleWithNotExistentTitleId(Integer id) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" + id)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isNotFound());
    }

    private void getTitleWithSymbolId(String id) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" + id)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isBadRequest());
    }

    private void updateTitle(Integer id, String name) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" + id)
                                        .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                            .contentType(MediaType.APPLICATION_JSON)
                                                 .content("{\n" +
                                               "    \"name\": \"" + name + "\"\n" +
                                               "}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name));
    }

    private void updateTitleWithEmptyBody(Integer id) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" + id)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Title name should not be empty"));

    }

    private void updateTitleBySymbolId(String id) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" + id)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isBadRequest());
    }

    private void updateTitleByNonExistentId(Integer id, String name) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" + id)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" + "\"name\": \"" + name + "\"\n" +
                                        "}")

                )
                .andExpect(status().isNotFound());
    }

    private void getAllTitles() throws Exception {
        mockMvc.
                perform(
                        get(TITLE_URL + "/")
                            .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isOk());
    }

    private void getAllTitleUnauthorized() throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/")
                )
                .andExpect(status().isUnauthorized());
    }

    private void getAllTitlesWithInvalidJWTToken() throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/")
                            .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI3ODkzMDI0LCJleHAiOjE2Mjc4OTM5MjQsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.AAf_MA1pNBUFzr6Wj0ERuYcu6AmYNttEst_t_eQgrKiRW9ZsD0c4moZQIce7sghAgEOs8TeBk4SAVvZ4aieeAQ")
                )
                .andExpect(status().isUnauthorized());
    }


    private ResultActions createTitle(String title) throws Exception {
        return mockMvc
                .perform(
                        post(TITLE_URL + "/")
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\n" +
                                             "    \"name\": \"" + title + "\"\n" +
                                                "}")
                )
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories").isNotEmpty());
    }

    private void createTitleWithEmptyBody() throws Exception {
        mockMvc
                .perform(
                        post(TITLE_URL + "/")
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Title name should not be empty"));
    }

    private ResultActions createTitleWithWithoutJWTToken(String title) throws Exception {
        return mockMvc
                .perform(
                        post(TITLE_URL + "/")
                                .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\n" +
                                        "    \"name\": \"" + title + "\"\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());
    }

    private ResultActions createTitleWithWithInvalidJWTToken(String title) throws Exception {

        return mockMvc
                .perform(
                        post(TITLE_URL + "/")
                                .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI4NDk1MzY0LCJleHAiOjE2Mjg0OTYyNjQsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.ERheYQHTkPK9yGRMpsWaQ0oHdyxtfVVvHlDTUk7kK8JunZxmd9mxmu4XV-EmdBLrLwlq5SIFQ02ES2LC16ITlw")
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\n" +
                                        "    \"name\": \"" + title + "\"\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());

    }

    private void deleteTitleById(Integer id) throws Exception {
        mockMvc
                .perform(
                        delete(TITLE_URL + "/" + id)
                            .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isOk());
    }

    private void deleteTitleWithInvalidJWTToken(Integer id) throws Exception {
        mockMvc
                .perform(
                        delete(TITLE_URL + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "whateverwhateveryotarenotsurewhatitis")
                )
                .andExpect(status().isUnauthorized());
    }

    private void deleteTitleWithNullJWTToken(Integer id) throws Exception {
        mockMvc
                .perform(
                        delete(TITLE_URL + "/" + id)
                )
                .andExpect(status().isUnauthorized());
    }



    private void deleteTitleByNonExistentId(Integer id) throws Exception {
        mockMvc
                .perform(
                        delete(TITLE_URL + "/" + id)
                            .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isNotFound());
    }

    private void deleteTitleBySymbolId(String id) throws Exception {
        mockMvc
                .perform(
                        delete(TITLE_URL + "/" + id)
                            .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isBadRequest());
    }

    @Before
    public void beforeTest() {
       titleRepository.deleteAll();
    }

    @Test
    public void whenGetToken_ReturnCurrentJWTToken() throws Exception {
        getJWTToken();
    }

    @Test
    public void whenCreateTitle_ReturnCreatedOne() throws Exception {
        createTitle("Title1");
    }

    @Test
    public void whenCreateTitleWithoutJWTToken_thenReturnUnAuthorized() throws Exception {
        createTitleWithWithoutJWTToken("Title1");
    }

    @Test
    public void whenCreateTitleWithInvalidJWTToken_thenReturnUnAuthorized() throws Exception {
        createTitleWithWithInvalidJWTToken("Title1");
    }


    @Test
    public void whenGetTitleById_thenReturnThisTitle() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer id = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getTitle(id,"Title1");
    }

    @Test
    public void whenUpdateTitle_thenReturnUpdatedOne() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer id = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getTitle(id, "Title1");
        updateTitle(id, "Title2");
        getTitleAfterUpdate(id, "Title2");
    }

    @Test
    public void whenGetAllTitles_thenReturnTitles() throws Exception {
        createTitle("Title1");
        createTitle("Title2");
        getAllTitles();
    }

    @Test
    public void whenGetAllTitlesUnauthorised_thenNoAccess() throws Exception {
        createTitle("Title1");
        createTitle("Title2");
        getAllTitleUnauthorized();
    }

    @Test
    public void whenGetAllTitlesWithIncorrectJWTToken_thenNoAccess() throws Exception {
        createTitle("Title1");
        createTitle("Title2");
        getAllTitlesWithInvalidJWTToken();
    }

    @Test
    public void whenDeleteTitleById_thenReturnNoTitle() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer id = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        deleteTitleById(id);
    }

    @Test
    public void whenGetTitleByNonExistentTitleId_thenReturn404() throws Exception {
        getTitleWithNotExistentTitleId(130);
    }

    @Test
    public void whenGetTitleBySymbolId_thenReturnNoTitle() throws Exception {
        getTitleWithSymbolId("abc");
    }

    @Test
    public void whenDeleteTitleByNonExistentId_thenReturn404() throws Exception {
        deleteTitleByNonExistentId(130);
    }

    @Test
    public void whenDeleteTitleBySymbolId_thenReturn400() throws Exception {
        deleteTitleBySymbolId("abc");
    }

    @Test
    public void whenUpdateTitleBySymbolId_thenReturn400() throws Exception {
        updateTitleBySymbolId("abc");
    }

    @Test
    public void whenUpdateTitleByNonExistentId_thenReturn400() throws Exception {
        updateTitleByNonExistentId(130, "Title130");
    }

    @Test
    public void whenCreateTitleWithEmptyBody_thenReturn400() throws Exception {
        createTitleWithEmptyBody();
    }

    @Test
    public void whenUpdateTitleWithEmptyBody_thenReturn400() throws Exception {
        updateTitleWithEmptyBody(130);
    }

    @Test
    public void whenDeleteTitleWithInvalidJWTToken_thenReturnIsNotUnauthorized() throws Exception {
        deleteTitleWithInvalidJWTToken(130);
    }

    @Test
    public void whenDeleteTitleWithNullJWTToken_thenReturnIsNotUnauthorized() throws Exception {
        deleteTitleWithNullJWTToken(130);
    }
}


