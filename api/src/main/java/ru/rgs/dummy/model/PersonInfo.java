package ru.rgs.dummy.model;

import lombok.Data;

/**
 * @author jihor (dmitriy_zhikharev@rgs.ru)
 * Created on 2017-08-01
 */
@Data
public class PersonInfo {
    private final Person person;
    private final String message;
}
