package ru.codepinkglitch.auction.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.ArtistIn;
import ru.codepinkglitch.auction.repositories.UserDetailsRepository;
import ru.codepinkglitch.auction.services.TestService;

import java.util.Base64;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class ArtistControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Converter converter;

    @Autowired
    TestService testService;

    @Autowired
    UserDetailsRepository userDetailsRepository;

    MockMvc mockMvc;
    static ArtistIn savedEntity;
    static String email = "Petr@mail.su";
    static String username = "Petr321";
    static String password = "123";
    static String token;
    static boolean initIsDone = false;
    String uri = "/artist/";

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Before
    public void setUp(){
        ConfigurableMockMvcBuilder builder =
                MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                        .apply(documentationConfiguration(this.restDocumentation))
                        .apply(springSecurity());
        this.mockMvc = builder.build();
        if(!initIsDone){
            savedEntity  = testService.initForArtist(email, username, password);
            token = new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
            initIsDone = true;
    }


    }

    @Test
    public void getArtist() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Basic " + token))
                .andDo(document("." + uri))
                .andDo(print())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(status().isOk());
    }

    @Test
    public void updateArtist() throws Exception{
        email = "VasilyNewMail@mail.ru";
        savedEntity.setEmail(email);

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedEntity))
                .header("Authorization", "Basic " + token))
                .andDo(document("." + uri))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteArtist() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + token))
                .andDo(document("." + uri))
                .andExpect(content().string("Account deleted."))
                .andExpect(status().isOk());

        Assert.assertFalse(userDetailsRepository.existsMyUserDetailsByUsername(username));
    }
}
