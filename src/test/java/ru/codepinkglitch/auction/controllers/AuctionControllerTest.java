package ru.codepinkglitch.auction.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.codepinkglitch.auction.dtos.in.CommissionWrapper;
import ru.codepinkglitch.auction.enums.ExceptionEnum;
import ru.codepinkglitch.auction.enums.Status;
import ru.codepinkglitch.auction.services.TestService;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class AuctionControllerTest {

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
    static String commissionTag = "character";
    static BigDecimal commissionMinimalBid;
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    @Autowired
    WebApplicationContext webApplicationContext;
    @Autowired
    TestService testService;
    @Autowired
    ObjectMapper objectMapper;
    MockMvc mockMvc;

    @Before
    public void setUp() {
        ConfigurableMockMvcBuilder builder =
                MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                        .apply(documentationConfiguration(this.restDocumentation))
                        .apply(springSecurity());
        this.mockMvc = builder.build();
        if (!initIsDone) {
            testService.initForArtist(artistEmail, artistUsername, artistPassword);
            artistToken = new String(Base64.getEncoder().encode((artistUsername + ":" + artistPassword).getBytes()));
            testService.initForBuyer(buyerEmail, buyerUsername, buyerPassword);
            buyerToken = new String(Base64.getEncoder().encode((buyerUsername + ":" + buyerPassword).getBytes()));
            minimalBid = new BigDecimal("5.5");
            commissionMinimalBid = new BigDecimal("10.0");
            commissionId = testService.initForCommission(artistUsername, new HashSet<>(Collections.singletonList(commissionTag)), commissionMinimalBid).getId();
            initIsDone = true;
        }
    }


    @Test
    public void createCommission() throws Exception {
        String uri = "/api/auction/";

        CommissionWrapper commissionWrapper = new CommissionWrapper();
        commissionWrapper.setDaysPeriod(1);
        commissionWrapper.setHoursPeriod(1);
        commissionWrapper.setMinutesPeriod(1);
        commissionWrapper.setTags(new HashSet<>(Arrays.asList(commissionTag, "Cyberpunk")));
        commissionWrapper.setMinimalBid(minimalBid);


        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commissionWrapper))
                        .header("Authorization", "Basic " + artistToken))
                .andDo(document(uri.replace("/", "\\")))
                .andExpect(jsonPath("$.status").value(Status.OPEN.name()))
                .andExpect(jsonPath("$.author.username").value(artistUsername))
                .andExpect(jsonPath("$.author.email").value(artistEmail))
                .andExpect(jsonPath("$.bid.amount").value(minimalBid))
                .andExpect(status().isOk());

    }

    @Test
    public void getAllCommissions() throws Exception {
        String uri = "/api/auction/all";

        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Basic " + buyerToken))
                .andDo(document(uri.replace("/", "\\")))
                .andExpect(jsonPath("$.[0].author.username").value(artistUsername))
                .andExpect(jsonPath("$.[0].author.email").value(artistEmail))
                .andExpect(status().isOk());
    }

    @Test
    public void bidToCommission() throws Exception {
        BigDecimal newBid = commissionMinimalBid.add(new BigDecimal("1"));
        String uri = "/api/auction/?bid=" + newBid + "&commissionId=" + commissionId;

        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Basic " + buyerToken))
                .andDo(document(uri.replace("/", "\\").replace("?", "(question_mark)")))
                .andExpect(jsonPath("$.bid.buyerUsername").value(buyerUsername))
                .andExpect(jsonPath("$.bid.amount").value(newBid.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void getCommissionsByTag() throws Exception {
        String uri = "/api/auction/find?tag=" + commissionTag;

        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Basic " + buyerToken))
                .andDo(document(uri.replace("/", "\\").replace("?", "(question_mark)")))
                .andExpect(jsonPath("$.[*].author.username").value(artistUsername))
                .andExpect(jsonPath("$.[*].author.email").value(artistEmail))
                .andExpect(status().isOk());
    }

    @Test
    public void catchExceptionOnGetPreview() throws Exception {
        int unreachableCommissionId = 100;
        String uri = "/api/auction/preview?commissionId=" + unreachableCommissionId;

        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .header("Authorization", "Basic " + buyerToken))
                .andDo(document(uri.replace("/", "\\").replace("?", "(question_mark)")))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(ExceptionEnum.COMMISSION_DONT_EXIST_EXCEPTION.getMessage()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void uploadingPreviewImage() throws Exception {
        String uri = "/api/auction/image?commissionId=" + commissionId;

        File file = new File("src/test/java/ru/codepinkglitch/auction/resources/previewImage.jpeg");
        MockMultipartFile mockFile = new MockMultipartFile("file", file.getName(), MediaType.IMAGE_JPEG_VALUE, Files.readAllBytes(file.toPath()));

        String response = mockMvc.perform(multipart(uri)
                        .file(mockFile)
                        .header("Authorization", "Basic " + artistToken))
                .andDo(document(uri.replace("/", "\\").replace("?", "(question_mark)")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assert.assertEquals("Image attached.", response);
    }

    @Test
    public void uploadingFinishedImage() throws Exception {
        String uri = "/api/auction/finishedImage?commissionId=" + commissionId;

        File file = new File("src/test/java/ru/codepinkglitch/auction/resources/finishedPicture.jpeg");
        MockMultipartFile mockFile = new MockMultipartFile("file", file.getName(), MediaType.IMAGE_JPEG_VALUE, Files.readAllBytes(file.toPath()));

        String response = mockMvc.perform(multipart(uri)
                        .file(mockFile)
                        .header("Authorization", "Basic " + artistToken))
                .andDo(document(uri.replace("/", "\\").replace("?", "(question_mark)")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assert.assertEquals("Image attached.", response);
    }

    @Test
    public void getPreviewImage() throws Exception {
        String uri = "/api/auction/preview?commissionId=" + commissionId;

        byte[] previewImage = mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .header("Authorization", "Basic " + buyerToken))
                .andDo(document(uri.replace("/", "\\").replace("?", "(question_mark)")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        File file = new File("src/test/java/ru/codepinkglitch/auction/resources/previewImage.jpeg");

        Assert.assertArrayEquals(Files.readAllBytes(file.toPath()), previewImage);
    }
}
