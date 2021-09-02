package ru.codepinkglitch.auction.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
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
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.services.BuyerService;
import ru.codepinkglitch.auction.services.MyUserDetailsService;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestMethodOrder(OrderAnnotation.class)
public class BuyerControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Converter converter;

    @Autowired
    MyUserDetailsService myUserDetailsService;

    @Autowired
    BuyerService buyerService;

    MockMvc mockMvc;
    BuyerEntity buyerEntity;
    BuyerIn saved;
    String username = "Vasily321";
    String password = "123";
    String email = "Vasily@mail.su";
    String token = new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
    static boolean setupIsDone = false;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Before
    public void setUp(){
        ConfigurableMockMvcBuilder builder =
                MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                        .apply(documentationConfiguration(this.restDocumentation))
                        .apply(springSecurity());
        this.mockMvc = builder.build();
        init();
    }

    private void init(){
        if(!setupIsDone){
            buyerEntity = new BuyerEntity();
            BillingDetailsEntity billingDetailsEntity = new BillingDetailsEntity();
            billingDetailsEntity.setName("Vasily Vasiliev");
            billingDetailsEntity.setStreet("Pupkina");
            billingDetailsEntity.setCity("Moscow");
            billingDetailsEntity.setState("Moscow");
            billingDetailsEntity.setZip("111111");
            billingDetailsEntity.setCcNumber("1111 1111 1111 1111 1111");
            billingDetailsEntity.setCcExpiration("10/22");
            billingDetailsEntity.setCcCVV("111");
            buyerEntity.setBillingDetails(billingDetailsEntity);
            buyerEntity.setCommissions(new ArrayList<>());
            buyerEntity.setBids(new ArrayList<>());
            MyAuthority myAuthority = new MyAuthority();
            myAuthority.setAuthority(Role.BUYER.name());
            buyerEntity.setUserDetails(new MyUserDetails(Collections.singletonList(myAuthority), password, username));
            buyerEntity.setEmail(email);
            saved = buyerService.save(converter.buyerToDto(buyerEntity));
            setupIsDone = true;
        }
    }

    @Test
    @Order(1)
    public void getBuyer() throws Exception{
        String uri = "/buyer/";

        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + token))
                .andDo(document("." + uri))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(status().isOk());
    }

    @Test
    public void updateBuyer() throws Exception{
        String uri = "/buyer/";
        email = "VasilyNewMail@mail.ru";
        saved.setEmail(email);

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saved))
                .header("Authorization", "Basic " + token))
                .andDo(document("." + uri))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(status().isOk());
    }

    @Test(expected = RuntimeException.class)
    @Order(2)
    public void deleteBuyer() throws Exception{
        String uri = "/buyer";

        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + token))
                .andDo(document("." + uri))
                .andExpect(content().string("Account deleted."))
                .andExpect(status().isOk());

        buyerService.find(username);
    }
}
