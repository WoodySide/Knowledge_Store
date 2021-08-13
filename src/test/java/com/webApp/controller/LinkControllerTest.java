package com.webApp.controller;

import com.jayway.jsonpath.JsonPath;
import com.webApp.model.DeviceType;
import com.webApp.payload.DeviceInfo;
import com.webApp.repository.CategoryRepository;
import com.webApp.repository.LinkRepository;
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
@Sql({"/test_sql_scripts/delete_link.sql",
        "/test_sql_scripts/delete_category.sql",
        "/test_sql_scripts/delete_titles.sql",
        "/test_sql_scripts/delete_refresh_token.sql",
        "/test_sql_scripts/delete_user_device.sql",
        "/test_sql_scripts/delete_user_role.sql",
        "/test_sql_scripts/insert_role.sql",
        "/test_sql_scripts/insert_user.sql",
        "/test_sql_scripts/insert_user_role.sql"})
@ActiveProfiles(profiles = "test")
public class LinkControllerTest {

    private static final String TITLE_URL = "http://localhost8080/api/user/titles";

    private static final String LOGIN_URL = "http://localhost:8080/api/auth/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LinkRepository linkRepository;

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

    private ResultActions createCategory(Integer titleId, String category) throws Exception {
        return mockMvc
                .perform(
                        post(TITLE_URL + "/" + titleId + "/" + "categories")
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"name\": \"" + category + "\"\n" +
                                        "}")
                )
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(category));

    }

    private ResultActions createLink(Integer titleId, Integer categoryId, String link) throws Exception {
        return mockMvc
                .perform(
                        post(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links")
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"linkName\": \"" + link + "\"\n" +
                                        "}")
                )
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.linkName").value(link));

    }

    private ResultActions createLinkWithoutJWTToken(Integer titleId, Integer categoryId, String link) throws Exception {
        return mockMvc
                .perform(
                        post(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"linkName\": \"" + link + "\"\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());
    }

    private ResultActions createLinkWithInvalidJWTToken(Integer titleId, Integer categoryId, String link) throws Exception {
        return mockMvc
                .perform(
                        post(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links")
                                   .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI4NzYyMTA4LCJleHAiOjE2Mjg3NjMwMDgsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.AMyJJdowD9PNJTmDVqgOOvFzHsrBjxfduPS93JzxERjq--l-N3jA5A02L5JhJtFAHrGy0qfCfq2H7ACxmsQ0fw")
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\n" +
                                        "    \"linkName\": \"" + link + "\"\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());
    }

    private ResultActions createIncorrectLink(Integer titleId, Integer categoryId, String link) throws Exception {
        return mockMvc
                .perform(
                        post(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links")
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"linkName\": \"" + link + "\"\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest());
    }

    private void getLink(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.linkName").value(name))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private void getLinkWithoutJWTToken(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                )
                .andExpect(status().isUnauthorized());
    }

    private void getLinkWithInvalidJWTToken(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI4NzYyMTA4LCJleHAiOjE2Mjg3NjMwMDgsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.AMyJJdowD9PNJTmDVqgOOvFzHsrBjxfduPS93JzxERjq--l-N3jA5A02L5JhJtFAHrGy0qfCfq2H7ACxmsQ0fw")
                )
                .andExpect(status().isUnauthorized());
    }

    private void getLinkWithSymbolId(Integer titleId, Integer categoryId, String linkId, String name) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isBadRequest());
    }

    private void getLinkWithNonExistentId(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isNotFound());
    }

    private void getLinkAfterUpdate(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.linkName").value(name))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private void getAllLinks(Integer titleId, Integer categoryId) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links")
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isOk());
    }

    private void getAllLinksWithoutJWTToken(Integer titleId, Integer categoryId) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links")
                )
                .andExpect(status().isUnauthorized());
    }

    private void getAllLinksWithInvalidJWTToken(Integer titleId, Integer categoryId) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links")
                        .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI4NzYyMTA4LCJleHAiOjE2Mjg3NjMwMDgsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.AMyJJdowD9PNJTmDVqgOOvFzHsrBjxfduPS93JzxERjq--l-N3jA5A02L5JhJtFAHrGy0qfCfq2H7ACxmsQ0fw")
                )
                .andExpect(status().isUnauthorized());
    }

    private void updateLink(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception{
        mockMvc
                .perform(
                        put(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"linkName\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.linkName").value(name));
    }

    private void updateLinkWithEmptyBody(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isNotFound());
    }

    private void updateLinkWithoutJWTToken(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isUnauthorized());
    }

    private void updateLinkWithInvalidJWTToken(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI4NzYyMTA4LCJleHAiOjE2Mjg3NjMwMDgsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.AMyJJdowD9PNJTmDVqgOOvFzHsrBjxfduPS93JzxERjq--l-N3jA5A02L5JhJtFAHrGy0qfCfq2H7ACxmsQ0fw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isUnauthorized());
    }

    private void updateLinkWithSymbolId(Integer titleId, Integer categoryId, String linkId, String name) throws Exception{
        mockMvc
                .perform(
                        put(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"linkName\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest());
    }

    private void updateLinkWithNonExistentId(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception{
        mockMvc
                .perform(
                        put(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"linkName\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isNotFound());
    }

    private void deleteLink(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception{
        mockMvc
                .perform(
                        delete(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"linkName\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isOk());
    }

    private void deleteLinkWithInvalidJWTToken(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception{
        mockMvc
                .perform(
                        delete(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI4NzYyMTA4LCJleHAiOjE2Mjg3NjMwMDgsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.AMyJJdowD9PNJTmDVqgOOvFzHsrBjxfduPS93JzxERjq--l-N3jA5A02L5JhJtFAHrGy0qfCfq2H7ACxmsQ0fw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"linkName\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());
    }

    private void deleteLinkWithoutTToken(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception{
        mockMvc
                .perform(
                        delete(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"linkName\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());
    }

    private void deleteLinkByNonExistentId(Integer titleId, Integer categoryId, Integer linkId, String name) throws Exception{
        mockMvc
                .perform(
                        delete(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"linkName\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isNotFound());
    }

    private void deleteLinkBySymbolId(Integer titleId, Integer categoryId, String linkId, String name) throws Exception{
        mockMvc
                .perform(
                        delete(TITLE_URL + "/" +
                                titleId + "/" + "categories" + "/" + categoryId + "/" + "links" + "/" + linkId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"linkName\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest());
    }

    @Before
    public void setUp() {
        titleRepository.deleteAll();
        categoryRepository.deleteAll();
        linkRepository.deleteAll();
    }

    @Test
    public void whenCreateLink_thenReturnCreatedOne() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createLink(titleId,categoryId, "http://whatever.com");
    }

    @Test
    public void whenCreateLinkWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createLinkWithoutJWTToken(titleId,categoryId, "http://whatever.com");
    }

    @Test
    public void whenCreateLinkWithInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createLinkWithInvalidJWTToken(titleId,categoryId, "http://whatever.com");
    }

    @Test
    public void whenCreateIncorrectLink_thenReturn400() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createIncorrectLink(titleId,categoryId, "whatever");
    }

    @Test
    public void whenGetLink_thenReturnOne() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions linkActions = createLink(titleId,categoryId, "http://whatever.com");
        Integer linkId = JsonPath.read(linkActions.andReturn().getResponse().getContentAsString(), "$.id");
        getLink(titleId,categoryId,linkId, "http://whatever.com");
    }

    @Test
    public void whenGetLinkWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions linkActions = createLink(titleId,categoryId, "http://whatever.com");
        Integer linkId = JsonPath.read(linkActions.andReturn().getResponse().getContentAsString(), "$.id");
        getLinkWithoutJWTToken(titleId,categoryId,linkId, "http://whatever.com");
    }

    @Test
    public void whenGetLinkWithInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions linkActions = createLink(titleId,categoryId, "http://whatever.com");
        Integer linkId = JsonPath.read(linkActions.andReturn().getResponse().getContentAsString(), "$.id");
        getLinkWithInvalidJWTToken(titleId,categoryId,linkId, "http://whatever.com");
    }

    @Test
    public void whenGetLinkWithSymbolId_thenReturn400() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        getLinkWithSymbolId(titleId,categoryId,"abs", "http://whatever.com");
    }

    @Test
    public void whenGetLinkWithNonExistentId_thenReturn404() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        getLinkWithNonExistentId(titleId,categoryId,1000, "http://whatever.com");
    }

    @Test
    public void whenGetAllLinks_thenReturn200() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createLink(titleId,categoryId, "http://whatever.com");
        createLink(titleId,categoryId, "http://whatever2.ru");
        getAllLinks(titleId,categoryId);
    }

    @Test
    public void whenGetAllLinksWithInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createLink(titleId,categoryId, "http://whatever.com");
        createLink(titleId,categoryId, "http://whatever2.ru");
        getAllLinksWithInvalidJWTToken(titleId,categoryId);
    }

    @Test
    public void whenGetAllLinksWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createLink(titleId,categoryId, "http://whatever.com");
        createLink(titleId,categoryId, "http://whatever2.ru");
        getAllLinksWithoutJWTToken(titleId,categoryId);
    }

    @Test
    public void whenUpdateLink_thenReturnUpdatedOne() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions linkActions = createLink(titleId,categoryId, "http://whatever.com");
        Integer linkId = JsonPath.read(linkActions.andReturn().getResponse().getContentAsString(), "$.id");
        updateLink(titleId,categoryId,linkId, "http://whateverandwhoever.com");
        getLinkAfterUpdate(titleId,categoryId,linkId, "http://whateverandwhoever.com");
    }

    @Test
    public void whenUpdateLinkWithInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions linkActions = createLink(titleId,categoryId, "http://whatever.com");
        Integer linkId = JsonPath.read(linkActions.andReturn().getResponse().getContentAsString(), "$.id");
        updateLink(titleId,categoryId,linkId, "http://whateverandwhoever.com");
        updateLinkWithInvalidJWTToken(titleId,categoryId,linkId, "http://whateverandwhoever.com");
    }

    @Test
    public void whenUpdateLinkWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions linkActions = createLink(titleId,categoryId, "http://whatever.com");
        Integer linkId = JsonPath.read(linkActions.andReturn().getResponse().getContentAsString(), "$.id");
        updateLinkWithoutJWTToken(titleId,categoryId,linkId, "http://whateverandwhoever.com");
    }

    @Test
    public void whenUpdateLinkWithSymbolId_thenReturn400() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createLink(titleId,categoryId, "http://whatever.com");
        updateLinkWithSymbolId(titleId,categoryId,"abs", "http://whateverandwhoever.com");
    }

    @Test
    public void whenUpdateLinkByNonExistentId_thenReturn404() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createLink(titleId,categoryId, "http://whatever.com");
        updateLinkWithNonExistentId(titleId,categoryId,10000, "http://whateverandwhoever.com");
    }

    @Test
    public void whenUpdateLinkWithEmptyBody_thenReturn400() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createLink(titleId,categoryId, "http://whatever.com");
        updateLinkWithEmptyBody(titleId,categoryId,10000, "http://whateverandwhoever.com");
    }

    @Test
    public void whenDeleteLink_thenReturn200() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions linkActions = createLink(titleId,categoryId, "http://whatever.com");
        Integer linkId = JsonPath.read(linkActions.andReturn().getResponse().getContentAsString(), "$.id");
        deleteLink(titleId,categoryId,linkId, "http://whateverandwhoever.com");
    }

    @Test
    public void whenDeleteLinkWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions linkActions = createLink(titleId,categoryId, "http://whatever.com");
        Integer linkId = JsonPath.read(linkActions.andReturn().getResponse().getContentAsString(), "$.id");
        deleteLinkWithoutTToken(titleId,categoryId,linkId, "http://whateverandwhoever.com");
    }

    @Test
    public void whenDeleteLinkWitInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions linkActions = createLink(titleId,categoryId, "http://whatever.com");
        Integer linkId = JsonPath.read(linkActions.andReturn().getResponse().getContentAsString(), "$.id");
        deleteLinkWithInvalidJWTToken(titleId,categoryId,linkId, "http://whateverandwhoever.com");
    }

    @Test
    public void whenDeleteLinkBySymbolId_thenReturn400() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createLink(titleId,categoryId, "http://whatever.com");
        deleteLinkBySymbolId(titleId,categoryId,"abs", "http://whateverandwhoever.com");
    }

    @Test
    public void whenDeleteLinkByNonExistentId_thenReturn400() throws Exception {
        ResultActions actions = createTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        ResultActions categoryActions = createCategory(titleId, "Category1");
        Integer categoryId = JsonPath.read(categoryActions.andReturn().getResponse().getContentAsString(), "$.id");
        createLink(titleId,categoryId, "http://whatever.com");
        deleteLinkByNonExistentId(titleId,categoryId,10000, "http://whateverandwhoever.com");
    }
}
