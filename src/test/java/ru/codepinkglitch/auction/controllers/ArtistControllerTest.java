package ru.codepinkglitch.auction.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.jupiter.api.Order;
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
import ru.codepinkglitch.auction.entities.*;
import ru.codepinkglitch.auction.services.ArtistService;
import ru.codepinkglitch.auction.services.MyUserDetailsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    ArtistService artistService;

    @Autowired
    MyUserDetailsService myUserDetailsService;

    MockMvc mockMvc;
    ArtistEntity artistEntity;
    String username = "Vasily123";
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


    private void init() {
        if(!setupIsDone) {
            artistEntity = new ArtistEntity();
            BillingDetailsEntity billingDetailsEntity = new BillingDetailsEntity();
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
            myAuthority.setAuthority(Role.ARTIST.name());
            artistEntity.setUserDetails(new MyUserDetails(Collections.singletonList(myAuthority), password, username));
            artistEntity.setEmail(email);
            artistEntity.setDescription("default");
            artistService.save(converter.artistToDto(artistEntity));
            setupIsDone = true;
        }
    }

    @Test
    public void getArtist() throws Exception{
        String uri = "/artist/";

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
        String uri = "/artist/";
        email = "VasilyNewMail@mail.ru";
        artistEntity.setEmail(email);

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(converter.artistToDto(artistEntity)))
                .header("Authorization", "Basic " + token))
                .andDo(document("." + uri))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(status().isOk());
    }

    @Test(expected = RuntimeException.class)
    public void zdeleteArtist() throws Exception{
        String uri = "/artist/";

        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + token))
                .andDo(document("." + uri))
                .andExpect(content().string("Account deleted."))
                .andExpect(status().isOk());

        artistService.find(username);
    }
}
