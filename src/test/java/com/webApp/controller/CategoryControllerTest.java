package com.webApp.controller;

import com.jayway.jsonpath.JsonPath;
import com.webApp.model.Category;
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

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/delete_titles.sql","/delete_refresh_token.sql","/delete_user_device.sql","/delete_user_role.sql",
        "/insert_role.sql", "/insert_user.sql", "/insert_user_role.sql"})
@ActiveProfiles(profiles = "test")
public class CategoryController {

    private static final String TITLE_URL = "http://localhost8080/api/user/titles";

    private static final String LOGIN_URL = "http://localhost:8080/api/auth/login";

    private static Set<Category> categories;

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

    private ResultActions createTitle(String title, Set<Category> categories) throws Exception {
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

    @BeforeEach
    public void setup() {
        titleRepository.deleteAll();
        categoryRepository.deleteAll();
        categories = Set.of(new Category( "Articles"),
                new Category( "Videos"),
                new Category( "Books"),
                new Category( "Useful links"));
    }

    @Test
    public void whenCreateCategory_thenReturnCreatedOnes() throws Exception {
        createTitle("Title1", categories);
    }

    @Test
    public void whenGetCategoryById_thenReturnCategory() throws Exception {
        ResultActions actions = createTitle("Title1", categories);
        Integer titleId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getTitle(titleId, "Title1");
        Integer categoryId = JsonPath.read(actions.andReturn().getResponse().getContentAsString(), "$.id");
        getCategory(titleId,categoryId, "Videos");
    }
}
