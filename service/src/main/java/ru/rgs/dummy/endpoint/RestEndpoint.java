package ru.rgs.dummy.endpoint;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.rgs.dummy.config.properties.DummyProperties;
import ru.rgs.dummy.model.Person;
import ru.rgs.dummy.model.PersonInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jihor (dmitriy_zhikharev@rgs.ru)
 * Created on 2017-08-01
 */
@RestController
public class RestEndpoint {
    private final DummyProperties properties;

    private final Set<Person> persons = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public RestEndpoint(DummyProperties properties) {
        this.properties = properties;
    }

    @RequestMapping(value = "/person", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addPerson(@RequestBody Person person) throws URISyntaxException {
        boolean add = persons.add(person);
        return add ?
               ResponseEntity.created(new URI("/person")).build() :
               ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/person/{lastName}/{firstName}/{birthDateAsString}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonInfo> getPerson(@PathVariable String lastName, @PathVariable String firstName, @PathVariable String birthDateAsString) {
        LocalDate birthDate = LocalDate.parse(birthDateAsString);
        Person person = new Person(lastName, firstName, birthDate);
        return persons.contains(person) ?
               ResponseEntity.ok(createInfo(person)) :
               ResponseEntity.notFound().build();
    }

    private PersonInfo createInfo(Person person) {
        String msg = LocalDate.now().getMonth().equals(person.getBirthDate().getMonth())
                             && LocalDate.now().getDayOfMonth() == person.getBirthDate().getDayOfMonth() ?
                     properties.getInner().getHappyBirthdayMsg() :
                     properties.getSimpleMsg();
        return new PersonInfo(person, msg);
    }
}
