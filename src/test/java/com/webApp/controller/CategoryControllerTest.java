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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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


    private ResultActions createAdditionalCategory(Integer titleId, String category) throws Exception {
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
               .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(category))
               .andExpect(MockMvcResultMatchers.jsonPath("$.links").isEmpty());

    }

    private ResultActions createAdditionalCategoryWithEmptyBody(Integer titleId, String category) throws Exception {
        return mockMvc
                .perform(
                        post(TITLE_URL + "/" + titleId + "/" + "categories")
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());
    }

    private ResultActions createAdditionalCategoryWithoutJWTToken(Integer titleId, String category) throws Exception {
        return mockMvc
                .perform(
                        post(TITLE_URL + "/" + titleId + "/" + "categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"name\": \"" + category + "\"\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());
    }

    private ResultActions createAdditionalCategoryWithInvalidJWTToken(Integer titleId, String category) throws Exception {
        return mockMvc
                .perform(
                        post(TITLE_URL + "/" + titleId + "/" + "categories")
                                .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI4NTc3NDQ4LCJleHAiOjE2Mjg1NzgzNDgsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.0EC1ELw2Y2XKZo0ZP0Y5xBbtQNxi2x4vIr_6-3Lh1DN5LsmpYn5D5R-kYk3ehcWKt38HK5bAOpZ0w6cLQSlM1w")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"name\": \"" + category + "\"\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());
    }

    private ResultActions createDefaultCategoriesByTitle(String title) throws Exception {
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

    private ResultActions createDefaultCategoriesByTitleWithNoJWTToken(String title) throws Exception {
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

    private ResultActions createDefaultCategoriesByTitleWithInvalidJWTToken(String title) throws Exception {
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

    private ResultActions createDefaultCategoriesByTitleWithEmptyBody() throws Exception {
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

    private void getCategoryWithSymbolId(Integer titleId, String categoryId) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +  titleId + "/" + "categories" + "/" + categoryId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isBadRequest());
    }

    private void getCategoryWithNonExistentId(Integer titleId, Integer categoryId) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +  titleId + "/" + "categories" + "/" + categoryId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isNotFound());
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

    private void getAllCategories(Integer titleId) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" + titleId + "/" + "categories")
                            .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isOk());
    }

    private void getAllCategoriesWithoutJWTToken(Integer titleId) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" + titleId + "/" + "categories")
                )
                .andExpect(status().isUnauthorized());
    }

    private void getAllCategoriesWithInvalidJWTToken(Integer titleId) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" + titleId + "/" + "categories")
                                .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI4NTc3NDQ4LCJleHAiOjE2Mjg1NzgzNDgsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.0EC1ELw2Y2XKZo0ZP0Y5xBbtQNxi2x4vIr_6-3Lh1DN5LsmpYn5D5R-kYk3ehcWKt38HK5bAOpZ0w6cLQSlM1w")
                )
                .andExpect(status().isUnauthorized());
    }

    private void updateCategory(Integer titleId, Integer categoryId, String name) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" + titleId + "/" + "categories" + "/" + categoryId)
                            .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\n" +
                                        "    \"name\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name));
    }

    private void updateCategoryWithEmptyBody(Integer titleId, Integer categoryId, String name) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" + titleId + "/" + "categories" + "/" + categoryId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}")
                )
                .andExpect(status().isBadRequest());
    }

    private void updateCategoryWithoutJWTToken(Integer titleId, Integer categoryId, String name) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" + titleId + "/" + "categories" + "/" + categoryId)
                                 .contentType(MediaType.APPLICATION_JSON)
                                     .content("{\n" +
                                        "    \"name\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());
    }

    private void updateCategoryWithInvalidJWTToken(Integer titleId, Integer categoryId, String name) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" + titleId + "/" + "categories" + "/" + categoryId)
                                .header(HttpHeaders.AUTHORIZATION, "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjI4NTc3NDQ4LCJleHAiOjE2Mjg1NzgzNDgsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIn0.0EC1ELw2Y2XKZo0ZP0Y5xBbtQNxi2x4vIr_6-3Lh1DN5LsmpYn5D5R-kYk3ehcWKt38HK5bAOpZ0w6cLQSlM1w")
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\n" +
                                        "    \"name\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isUnauthorized());
    }

    private void updateCategoryWithCategorySymbolId(Integer titleId, String categoryId, String name) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" + titleId + "/" + "categories" + "/" + categoryId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\n" +
                                        "    \"name\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest());
    }

    private void updateCategoryByNonExistentId(Integer titleId, Integer categoryId, String name) throws Exception {
        mockMvc
                .perform(
                        put(TITLE_URL + "/" + titleId + "/" + "categories" + "/" + categoryId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                                    .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\n" +
                                        "    \"name\": \"" + name + "\"\n" +
                                        "}")
                )
                .andExpect(status().isNotFound());
    }

    private void getCategoryAfterUpdate(Integer titleId, Integer categoryId, String name) throws Exception {
        mockMvc
                .perform(
                        get(TITLE_URL + "/" +  titleId + "/" + "categories" + "/" + categoryId)
                                .header(HttpHeaders.AUTHORIZATION, getJWTToken())
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name));
    }

    @BeforeEach
    public void setup() {
        titleRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void whenCreateCategory_thenReturnCreatedOnes() throws Exception {
        createDefaultCategoriesByTitle("Title1");
    }

    @Test
    public void whenGetCategoryById_thenReturnCategories() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getTitle(titleId, "Title1");
        Integer categoryId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.categories[0].id");
        String name = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.categories[0].name");
        getCategory(titleId,categoryId, name);
    }

    @Test
    public void whenGetCategoryByIdWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getTitle(titleId, "Title1");
        getCategoryWithoutJWTToken(titleId,1);
    }

    @Test
    public void whenGetCategoryWithSymbolId_thenReturn400() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getTitle(titleId, "Title1");
        getCategoryWithSymbolId(titleId,"abs");
    }

    @Test
    public void whenGetCategoryWithNonExistentId_thenReturn404() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getTitle(titleId, "Title1");
        getCategoryWithNonExistentId(titleId,10000);
    }

    @Test
    public void whenGetCategoryByIdWithInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getTitle(titleId, "Title1");
        getCategoryWithInvalidJWTToken(titleId,1);
    }

    @Test
    public void whenCreateAdditionalCategory_thenReturnCreatedOne() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        createAdditionalCategory(titleId, "Category1");
    }

    @Test
    public void whenCreateAdditionalCategoryWithEmptyBody_thenReturn400() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        createAdditionalCategoryWithEmptyBody(titleId, "Category1");
    }

    @Test
    public void whenCreateAdditionalCategoryWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        createAdditionalCategoryWithoutJWTToken(titleId, "Category");
    }

    @Test
    public void whenCreateAdditionalCategoryWithInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        createAdditionalCategoryWithInvalidJWTToken(titleId, "Category");
    }

    @Test
    public void whenCreateCategoriesWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        createDefaultCategoriesByTitleWithNoJWTToken("Title1");
    }

    @Test
    public void whenCreateCategoriesWithInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        createDefaultCategoriesByTitleWithInvalidJWTToken("Title1");
    }

    @Test
    public void whenCreateCategoriesWithEmptyBody_thenReturn400() throws Exception {
        createDefaultCategoriesByTitleWithEmptyBody();
    }

    @Test
    public void whenGetAllCategories_thenReturnAllOfThem() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getAllCategories(titleId);
    }

    @Test
    public void whenGetAllCategoriesWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getAllCategoriesWithoutJWTToken(titleId);
    }

    @Test
    public void whenGetAllCategoriesWithInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getAllCategoriesWithInvalidJWTToken(titleId);
    }

    @Test
    public void whenUpdateCategory_thenReturnUpdatedOne() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        Integer categoryId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.categories[0].id");
        updateCategory(titleId,categoryId, "Category10");
        getCategoryAfterUpdate(titleId,categoryId, "Category10");
    }

    @Test
    public void whenUpdateCategoryWithEmptyBody_thenReturn400() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        Integer categoryId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.categories[0].id");
        updateCategoryWithEmptyBody(titleId,categoryId, "Category10");
    }

    @Test
    public void whenUpdateCategoryWithoutJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        Integer categoryId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.categories[0].id");
        updateCategoryWithoutJWTToken(titleId,categoryId, "Category10");
    }

    @Test
    public void whenUpdateCategoryWithInvalidJWTToken_thenReturnIsUnauthorized() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        Integer categoryId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.categories[0].id");
        updateCategoryWithInvalidJWTToken(titleId,categoryId, "Category10");
    }

    @Test
    public void whenUpdateCategoryWithCategorySymbolId_thenReturn400() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        updateCategoryWithCategorySymbolId(titleId,"abc", "Category10");
    }

    @Test
    public void whenUpdateCategoryByNonExistentId_thenReturn404() throws Exception {
        ResultActions actions = createDefaultCategoriesByTitle("Title1");
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        updateCategoryByNonExistentId(titleId,10000, "Category10");
    }



}
