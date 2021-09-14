package ru.codepinkglitch.auction.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
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
import ru.codepinkglitch.auction.dtos.in.CommissionWrapper;
import ru.codepinkglitch.auction.enums.Status;
import ru.codepinkglitch.auction.services.TestService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class MainControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    TestService testService;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;
    static boolean initIsDone = false;
    static String artistUsername = "Ivan123";
    static String artistPassword = "123";
    static String artistEmail = "Ivan@mail.su";
    static String artistToken;
    static String buyerUsername = "Alexander123";
    static String buyerPassword = "123";
    static String buyerEmail = "Alexander@mail.su";
    static String buyerToken;
    static BigDecimal minimalBid;
    static long commissionId;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp(){
        ConfigurableMockMvcBuilder builder =
                MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                        .apply(documentationConfiguration(this.restDocumentation))
                        .apply(springSecurity());
        this.mockMvc = builder.build();
        if(!initIsDone) {
            testService.initForArtist(artistEmail, artistUsername, artistPassword);
            artistToken = new String(Base64.getEncoder().encode((artistUsername + ":" + artistPassword).getBytes()));
            testService.initForBuyer(buyerEmail, buyerUsername, buyerPassword);
            buyerToken = new String(Base64.getEncoder().encode((buyerUsername + ":" + buyerPassword).getBytes()));
            minimalBid = new BigDecimal("5.5");
            initIsDone = true;
        }
    }


    @Test
    public void createCommission() throws Exception{
        String uri = "/main/";

        CommissionWrapper commissionWrapper = new CommissionWrapper();
        commissionWrapper.setDaysPeriod(1);
        commissionWrapper.setHoursPeriod(1);
        commissionWrapper.setMinutesPeriod(1);
        commissionWrapper.setTags(Arrays.asList("Character", "Cyberpunk"));
        commissionWrapper.setMinimalBid(minimalBid);
        String commissionUri = "https://www.some_hosting.su/cyberpunk_art#666";
        commissionWrapper.setUri(commissionUri);


        String content = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commissionWrapper))
                        .header("Authorization", "Basic " + artistToken))
                .andDo(document("." + uri))
                .andExpect(jsonPath("$.status").value(Status.OPEN.name()))
                .andExpect(jsonPath("$.author.username").value(artistUsername))
                .andExpect(jsonPath("$.author.email").value(artistEmail))
                .andExpect(jsonPath("$.uri").value(commissionUri))
                .andExpect(jsonPath("$.bid.amount").value(minimalBid))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        commissionId = new Long(JsonPath.read(content,"$.id").toString());

    }

    @Test
    public void getCommissionsByTag() throws Exception{
        String uri = "/main/find?tag=Cyberpunk";

        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + buyerToken))
                .andDo(document("." + uri))
                .andExpect(jsonPath("$.[*].author.username").value(artistUsername))
                .andExpect(jsonPath("$.[*].author.email").value(artistEmail))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllCommissions() throws Exception{
        String uri = "/main/all";

        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + buyerToken))
                .andDo(document("." + uri))
                .andExpect(jsonPath("$.[*].author.username").value(artistUsername))
                .andExpect(jsonPath("$.[*].author.email").value(artistEmail))
                .andExpect(status().isOk());
    }

    @Test
    public void bidToCommission() throws Exception{
        BigDecimal newBid = minimalBid.add(new BigDecimal("1"));
        String uri = "http://localhost:8080/main/?bid=" + newBid + "&commissionId=" + commissionId;

        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + buyerToken))
                .andDo(document("." + uri))
                .andExpect(jsonPath("$.bid.buyerUsername").value(buyerUsername))
                .andExpect(jsonPath("$.bid.amount").value(newBid.toString()))
                .andExpect(status().isOk());
    }
}
