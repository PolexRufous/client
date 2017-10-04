package com.thinkjava.client.entities;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Person {
    private String id;
    private String name;
    private String gender;
    private Long missCount;

}
