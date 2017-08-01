package ru.rgs.dummy.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author jihor (dmitriy_zhikharev@rgs.ru)
 * Created on 2017-08-01
 */
@Data
public class Person {
    private final String lastName;
    private final String firstName;
    private final LocalDate birthDate;
}
