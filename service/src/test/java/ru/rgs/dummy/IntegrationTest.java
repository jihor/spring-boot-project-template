package ru.rgs.dummy;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.rgs.dummy.model.PersonInfo;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.annotation.DirtiesContext.MethodMode.AFTER_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class IntegrationTest {

    private static final String LAST_NAME = "jihor";
    private static final String FIRST_NAME = "dmitry";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1987, 9, 1);

    @Value("${local.server.port}")
    int port;

    @Autowired
    TestRestTemplate testRestTemplate;

    String url;

    @PostConstruct
    public void init() {
        url = "http://localhost:" + port + "/person";
    }

    @Test
    @DirtiesContext(methodMode = AFTER_METHOD)
    public void createAndGetTest() {
        String url = "http://localhost:" + port + "/person";
        HttpEntity<String> request = createEntity(LAST_NAME, FIRST_NAME, BIRTH_DATE);
        ResponseEntity<Void> post1Response = testRestTemplate.postForEntity(url, request, Void.class);
        Assert.assertEquals(201, post1Response.getStatusCodeValue());

        ResponseEntity<PersonInfo> getResponse = testRestTemplate.getForEntity(url + "/" + LAST_NAME + "/" + FIRST_NAME + "/" + BIRTH_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE), PersonInfo.class);
        Assert.assertEquals(200, getResponse.getStatusCodeValue());
        Assert.assertEquals("Not much to say about him", getResponse.getBody().getMessage());
        Assert.assertEquals(LAST_NAME, getResponse.getBody().getPerson().getLastName());
        Assert.assertEquals(FIRST_NAME, getResponse.getBody().getPerson().getFirstName());
        Assert.assertEquals(LocalDate.of(1987, 9, 1), getResponse.getBody().getPerson().getBirthDate());
    }

    @Test
    @DirtiesContext(methodMode = AFTER_METHOD)
    public void doubleCreateTest() {
        HttpEntity<String> request = createEntity(LAST_NAME, FIRST_NAME, BIRTH_DATE);
        ResponseEntity<Void> post1Response = testRestTemplate.postForEntity(url, request, Void.class);
        Assert.assertEquals(201, post1Response.getStatusCodeValue());

        ResponseEntity<Void> post2Response = testRestTemplate.postForEntity(url, request, Void.class);
        Assert.assertEquals(200, post2Response.getStatusCodeValue()); // 2nd attempt must not return 201, because the resource already exists
    }

    @Test
    @DirtiesContext(methodMode = AFTER_METHOD)
    public void notFoundTest() {
        ResponseEntity<PersonInfo> getResponse = testRestTemplate.getForEntity(url + "/notfound/dmitry/1987-09-01", PersonInfo.class);
        Assert.assertEquals(404, getResponse.getStatusCodeValue());
    }

    @Test
    @DirtiesContext(methodMode = AFTER_METHOD)
    public void birthdayTest() {
        LocalDate now = LocalDate.now();
        Month month = now.getMonth();
        int day = now.getDayOfMonth();
        LocalDate date = LocalDate.of(1987, month, day);
        HttpEntity<String> request = createEntity(LAST_NAME, FIRST_NAME, date);
        ResponseEntity<Void> postResponse = testRestTemplate.postForEntity(url, request, Void.class);
        Assert.assertEquals(201, postResponse.getStatusCodeValue());

        ResponseEntity<PersonInfo> getResponse = testRestTemplate.getForEntity(url + "/" + LAST_NAME + "/" + FIRST_NAME + "/" + date.format(DateTimeFormatter.ISO_LOCAL_DATE), PersonInfo.class);
        Assert.assertEquals(200, getResponse.getStatusCodeValue());
        Assert.assertEquals("He has a birthday today", getResponse.getBody().getMessage());
        Assert.assertEquals(LAST_NAME, getResponse.getBody().getPerson().getLastName());
        Assert.assertEquals(FIRST_NAME, getResponse.getBody().getPerson().getFirstName());
        Assert.assertEquals(date, getResponse.getBody().getPerson().getBirthDate());
    }

    private HttpEntity<String> createEntity(String lastName, String firstName, LocalDate birthDate) {
        return createEntity(lastName, firstName, birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    private HttpEntity<String> createEntity(String lastName, String firstName, String birthDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = MessageFormat.format("'{' \"lastName\": \"{0}\", \"firstName\": \"{1}\", \"birthDate\": \"{2}\" '}'", lastName, firstName, birthDate);

        return new HttpEntity<>(body, headers);
    }

}
