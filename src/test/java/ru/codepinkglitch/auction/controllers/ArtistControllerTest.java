package ru.codepinkglitch.auction.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.repositories.ArtistRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ArtistControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Converter converter;

    @Autowired
    ArtistRepository artistRepository;

    MockMvc mockMvc;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Before
    public void setUp(){
        ConfigurableMockMvcBuilder builder =
                MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                        .apply(documentationConfiguration(this.restDocumentation));
        this.mockMvc = builder.build();
        init();
    }

    private void init() {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setId(1L);
        BillingDetailsEntity billingDetailsEntity = new BillingDetailsEntity();
        billingDetailsEntity.setId(1L);
        billingDetailsEntity.setName("Vasily Vasiliev");
        billingDetailsEntity.setStreet("Pupkina");
        billingDetailsEntity.setCity("Moscow");
        billingDetailsEntity.setState("Moscow");
        billingDetailsEntity.setZip("111111");
        billingDetailsEntity.setCcNumber("1111 1111 1111 1111 1111");
        billingDetailsEntity.setCcExpiration("10/22");
        billingDetailsEntity.setCcCVV("111");
        artistEntity.setBillingDetails(billingDetailsEntity);
        artistEntity.setCommissions(new ArrayList<>());
        MyAuthority myAuthority = new MyAuthority();
        myAuthority.setId(1L);
        myAuthority.setAuthority(Role.ARTIST.name());
        artistEntity.setUserDetails(new MyUserDetails(Collections.singletonList(myAuthority), "123", "Vasily123"));
        artistEntity.setName("Vasily");
        artistEntity.setSurname("Vasiliev");
        artistEntity.setEmail("vasily123@mail.su");
        artistEntity.setDescription("default");
        artistRepository.save(artistEntity);
    }

    @Test
    @Ignore
    public void getArtist(){
        String uri = "/artist/";

        //mockMvc.perform(MockMvcRequestBuilders.get(uri).with(user()))
    }
}
