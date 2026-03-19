package com.dykun.my_sandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MySandboxApplicationTests {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();

        for (int i = 1; i <= 30; i++) {
            itemRepository.save(new Item("Елемент SQLite номер " + i));
        }
    }

    @Test
    void shouldHaveThirtyElementsInDatabase() {
        long count = itemRepository.count();

        assertEquals(30, count, "У базі даних має бути рівно 30 елементів!");
    }
}