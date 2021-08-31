package ru.codepinkglitch.auction.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.ArtistIn;
import ru.codepinkglitch.auction.dtos.in.BillingDetailsIn;
import ru.codepinkglitch.auction.dtos.in.BuyerIn;


import java.util.ArrayList;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RegisterControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Converter converter;

    MockMvc mockMvc;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Before
    public void setUp(){
        ConfigurableMockMvcBuilder builder =
                MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                        .apply(documentationConfiguration(this.restDocumentation));
        this.mockMvc = builder.build();
    }

    @Test
    public void newBuyer() throws Exception{
        BuyerIn buyerIn = new BuyerIn();
        buyerIn.setId(1L);
        BillingDetailsIn billingDetailsIn = new BillingDetailsIn();
        billingDetailsIn.setId(1L);
        billingDetailsIn.setName("Vasily Vasiliev");
        billingDetailsIn.setStreet("Pupkina");
        billingDetailsIn.setCity("Moscow");
        billingDetailsIn.setState("Moscow");
        billingDetailsIn.setZip("228228");
        billingDetailsIn.setCcNumber("1337 1337 1337 1337 1337");
        billingDetailsIn.setCcExpiration("10/21");
        billingDetailsIn.setCcCVV("111");
        buyerIn.setBillingDetails(billingDetailsIn);
        buyerIn.setCommissionsIds(new ArrayList<>());
        buyerIn.setUsername("Vasily123");
        buyerIn.setPassword("123");
        buyerIn.setName("Vasily");
        buyerIn.setSurname("Vasiliev");
        buyerIn.setEmail("vasily@mail.su");
        buyerIn.setBidsIds(new ArrayList<>());

        String uri = "/register/buyer";
        mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyerIn)))
                .andDo(document("." + uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Vasily123"))
                .andExpect(jsonPath("$.email").value("vasily@mail.su"));
    }

    @Test
    public void newArtist() throws Exception{
        ArtistIn artistIn = new ArtistIn();
        BillingDetailsIn billingDetailsIn = new BillingDetailsIn();
        billingDetailsIn.setId(1L);
        billingDetailsIn.setName("Petro Petrov");
        billingDetailsIn.setStreet("Pupkina");
        billingDetailsIn.setCity("Moscow");
        billingDetailsIn.setState("Moscow");
        billingDetailsIn.setZip("228228");
        billingDetailsIn.setCcNumber("1337 1337 1337 1337 1337");
        billingDetailsIn.setCcExpiration("10/21");
        billingDetailsIn.setCcCVV("111");
        artistIn.setBillingDetails(billingDetailsIn);
        artistIn.setDescription("default");
        artistIn.setId(2L);
        artistIn.setCommissionsIds(new ArrayList<>());
        artistIn.setUsername("Petro123");
        artistIn.setPassword("123");
        artistIn.setName("Petro");
        artistIn.setEmail("petro@mail.su");

        String uri = "/register/artist";
        mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(artistIn)))
                .andDo(document("." + uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Petro123"))
                .andExpect(jsonPath("$.email").value("petro@mail.su"));
    }

}
