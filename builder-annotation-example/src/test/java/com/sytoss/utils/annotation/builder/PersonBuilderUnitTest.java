package com.sytoss.utils.annotation.builder;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class PersonBuilderUnitTest {

    @Test
    public void whenBuildPersonWithBuilder_thenObjectHasPropertyValues() {
        Person person = new PersonBuilder().withAge(25).withName("John").build();

        assertEquals(25, person.getAge());
        assertEquals("John", person.getName());

    }

    @Test
    public void humanBuilderTest() {
        Human person = new HumanBuilder()
                .withMan(true)
                .withCreateDate(new Date())
                .withAge(25)
                .withName("John").build();

        assertEquals(25, person.getAge());
        assertEquals("John", person.getName());
        assertTrue(person.isMan());
        assertNotNull(person.getCreateDate());
    }
}
