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
import ru.codepinkglitch.auction.dtos.in.BuyerIn;
import ru.codepinkglitch.auction.dtos.out.BuyerOut;
import ru.codepinkglitch.auction.repositories.BuyerRepository;
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
public class BuyerControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TestService testService;

    @Autowired
    Converter converter;

    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Autowired
    BuyerRepository buyerRepository;

    MockMvc mockMvc;
    static BuyerOut savedEntity;
    static boolean initIsDone = false;
    static String email = "Vasily@mail.su";
    static String username = "Vasily123";
    static String password = "123";
    static String token;
    String uri = "/buyer/";

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Before
    public void setUp(){
        ConfigurableMockMvcBuilder builder =
                MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                        .apply(documentationConfiguration(this.restDocumentation))
                        .apply(springSecurity());
        this.mockMvc = builder.build();
        if(userDetailsRepository.existsMyUserDetailsByUsername(username)){
            buyerRepository.delete(buyerRepository.findBuyerEntityByUserDetails(userDetailsRepository.findMyUserDetailsByUsername(username)));
        }
        savedEntity = testService.initForBuyer(email, username, password);
        token = new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
        initIsDone = true;
    }


    @Test
    public void getBuyer() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + token))
                .andDo(document(uri.replace("/", "\\")))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(status().isOk());
    }

    @Test
    public void updateBuyer() throws Exception{
        email = "vasily123@mail.ru";
        savedEntity.setEmail(email);

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedEntity))
                .header("Authorization", "Basic " + token))
                .andDo(document(uri.replace("/", "\\")))
                .andDo(print())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(status().isOk());
    }

    @Test
    public void getBids() throws Exception{
        String bidsUri = uri + "bids";

        mockMvc.perform(MockMvcRequestBuilders.get(bidsUri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + token))
                .andDo(document(bidsUri.replace("/", "\\")))
                .andExpect(status().isOk());
    }

    @Test
    public void getWonBids() throws Exception{
        String wonBidsUri = uri + "bids/won";

        mockMvc.perform(MockMvcRequestBuilders.get(wonBidsUri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + token))
                .andDo(document(wonBidsUri.replace("/", "\\")))
                .andExpect(status().isOk());
    }


    @Test
    public void deleteBuyer() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + token))
                .andDo(document(uri.replace("/", "\\")))
                .andDo(print())
                .andExpect(content().string("Account deleted."))
                .andExpect(status().isOk());

        Assert.assertFalse(userDetailsRepository.existsMyUserDetailsByUsername(username));
    }
}
