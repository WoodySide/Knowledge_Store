package com.webApp.controller;

import com.jayway.jsonpath.JsonPath;
import com.webApp.model.DeviceType;
import com.webApp.payload.DeviceInfo;
import com.webApp.repository.CategoryRepository;
import com.webApp.repository.TitleRepository;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/delete_category.sql","/delete_titles.sql","/delete_refresh_token.sql","/delete_user_device.sql","/delete_user_role.sql",
        "/insert_role.sql", "/insert_user.sql", "/insert_user_role.sql"})
@ActiveProfiles(profiles = "test")
public class CategoryControllerTest {

    private static final String TITLE_URL = "http://localhost8080/api/user/titles";

    private static final String LOGIN_URL = "http://localhost:8080/api/auth/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

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

    private ResultActions createCategoriesByTitle(String title) throws Exception {
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

    private ResultActions createCategoriesByTitleWithNoJWTToken(String title) throws Exception {
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

    private ResultActions createCategoriesByTitleWithInvalidJWTToken(String title) throws Exception {
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

    private ResultActions createCategoriesByTitleWithEmptyBody() throws Exception {
        return mockMvc
                .perform(
                        post(TITLE_URL + "/")
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}")

                )
                .andExpect(status().isBadRequest());
    }

    private void getCategory(Integer titleId, Integer categoryId, String name) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +  titleId + "/" + "categories" + "/" + categoryId)
                            .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.links").isEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private void getCategoryWithoutJWTToken(Integer titleId, Integer categoryId) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +  titleId + "/" + "categories" + "/" + categoryId)
                )
                .andExpect(status().isUnauthorized());
    }

    private void getCategoryWithInvalidJWTToken(Integer titleId, Integer categoryId) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +  titleId + "/" + "categories" + "/" + categoryId)
                        .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI4NDk1MzY0LCJleHAiOjE2Mjg0OTYyNjQsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.ERheYQHTkPK9yGRMpsWaQ0oHdyxtfVVvHlDTUk7kK8JunZxmd9mxmu4XV-EmdBLrLwlq5SIFQ02ES2LC16ITlw")
                )
                .andExpect(status().isUnauthorized());
    }

    @BeforeEach
    public void setup() {
        titleRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void whenCreateCategory_thenReturnCreatedOnes() throws Exception {
        createCategoriesByTitle("Title1");
    }

    @Test
    public void whenGetCategoryById_thenReturnCategories() throws Exception {
        ResultActions actions = createCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getTitle(titleId, "Title1");
        Integer categoryId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.categories[0].id");
        String name = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.categories[0].name");
        getCategory(titleId,categoryId, name);
    }

    @Test
    public void whenGetCategoryByIdWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getTitle(titleId, "Title1");
        getCategoryWithoutJWTToken(titleId,1);
    }

    @Test
    public void whenGetCategoryByIdWithInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getTitle(titleId, "Title1");
        getCategoryWithInvalidJWTToken(titleId,1);
    }

    @Test
    public void whenCreateCategoriesWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        createCategoriesByTitleWithNoJWTToken("Title1");
    }

    @Test
    public void whenCreateCategoriesWithInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        createCategoriesByTitleWithInvalidJWTToken("Title1");
    }

    @Test
    public void whenCreateCategoriesWithEmptyBody_thenReturn400() throws Exception {
        createCategoriesByTitleWithEmptyBody();
    }
}
