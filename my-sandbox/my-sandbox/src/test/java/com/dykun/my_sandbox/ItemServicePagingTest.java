package com.dykun.my_sandbox;

import com.dykun.my_sandbox.model.Item;
import com.dykun.my_sandbox.repository.ItemRepository;
import com.dykun.my_sandbox.request.ItemPageRequest;
import com.dykun.my_sandbox.response.ApiResponse;
import com.dykun.my_sandbox.response.PaginationMetaData;
import com.dykun.my_sandbox.service.ItemService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServicePagingTest {

    @Autowired
    private ItemService underTest;

    @Autowired
    private ItemRepository itemRepository;

    List<Item> items = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
    }

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        
        itemRepository.save(new Item("69aeefcbe5c3dbd26376b0c5", "Paging Target Item"));
        itemRepository.save(new Item("69aeefcbe5c3dbd26376b0a8", "GetById Target Item"));
        
        for (int i = 3; i <= 30; i++) {
            itemRepository.save(new Item(UUID.randomUUID().toString(), "Item " + i));
        }
    }

    @AfterEach
    void tearsDown(){
        itemRepository.deleteAll();
    }

    @Test
    void whenHappyPathThenOk(){
        ItemPageRequest request = new ItemPageRequest(0,5);
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        
        assertNotNull(response);
        assertNotNull(response.getMeta());

        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertNull(response.getMeta().getErrorMessage());

        assertEquals(0, response.getMeta().getNumber());
        assertEquals(5, response.getMeta().getSize());
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(6, response.getMeta().getTotalPages());
        assertTrue(response.getMeta().isFirst());
        assertFalse(response.getMeta().isLast());

        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());
        assertEquals(5, response.getData().size());
        assertEquals("69aeefcbe5c3dbd26376b0c5", response.getData().get(0).getId());
    }

    @Test
    void whenSizeIs_7_AndPageIs_4_ThenIsLast_TrueAndSizeEquals_2(){
        ItemPageRequest request = new ItemPageRequest(4, 7); 
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        
        assertTrue(response.getMeta().isLast(), "Це має бути остання сторінка");
        assertEquals(2, response.getData().size(), "На останній сторінці має залишитись 2 елементи (30 - 4*7 = 2)");
    }

    @Test
    void whenTheListIsEmptyThenErrorMessageHasTheWarning(){
        itemRepository.deleteAll(); 
        
        ItemPageRequest request = new ItemPageRequest(0, 5);
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        
        assertEquals("The list is empty", response.getMeta().getErrorMessage());
    }

    @Test
    void whenTheListIsEmptyThenMetadataAndDataAreNotNull(){
        itemRepository.deleteAll(); 
        
        ItemPageRequest request = new ItemPageRequest(0, 5);
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        
        assertNotNull(response.getMeta(), "Метадані не повинні бути null");
        assertNotNull(response.getData(), "Дані (список) не повинні бути null, навіть якщо порожні");
    }

    @Test
    void whenPageValueIsOutOfRangeThenErrorMessageHasTheWarning(){
        ItemPageRequest request = new ItemPageRequest(10, 5);
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        
        assertEquals("Page value is out of range", response.getMeta().getErrorMessage());
    }

    @Test
    void whenMiddlePageRequestedThenIsFirstAndIsLastAreFalse() {
        ItemPageRequest request = new ItemPageRequest(1, 10); 
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        
        assertFalse(response.getMeta().isFirst(), "Середня сторінка не може бути першою");
        assertFalse(response.getMeta().isLast(), "Середня сторінка не може бути останньою");
        assertEquals(1, response.getMeta().getNumber());
        assertEquals(10, response.getData().size());
    }

    @Test
    void whenSizeIsLargerThanTotalElementsThenReturnAllElementsOnOnePage() {
        // Просимо сторінку на 50 елементів, хоча в базі всього 30
        ItemPageRequest request = new ItemPageRequest(0, 50);
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        
        assertTrue(response.getMeta().isFirst(), "Це має бути перша сторінка");
        assertTrue(response.getMeta().isLast(), "Вона ж має бути і останньою, бо всі елементи помістились");
        assertEquals(1, response.getMeta().getTotalPages(), "Має бути всього 1 сторінка");
        assertEquals(30, response.getData().size(), "Мають повернутися всі 30 елементів");
    }

    @Test
    void whenExactDivisionLastPageThenSizeIsCorrectAndIsLastTrue() {
        ItemPageRequest request = new ItemPageRequest(2, 10);
        ApiResponse<PaginationMetaData, Item> response = underTest.getItemsPage(request);
        
        assertTrue(response.getMeta().isLast(), "Це має бути остання сторінка");
        assertEquals(10, response.getData().size(), "Розмір має бути рівно 10");
        assertEquals(3, response.getMeta().getTotalPages(), "Всього має бути 3 сторінки");
    }

    @Test
    void whenPageIsNegativeThenThrowIllegalArgumentException() {
        // Spring Data не дозволяє створювати сторінки з негативним індексом
        ItemPageRequest request = new ItemPageRequest(-1, 5);
        
        assertThrows(IllegalArgumentException.class, () -> {
            underTest.getItemsPage(request);
        }, "Має викидатися помилка IllegalArgumentException при негативному номері сторінки");
    }

    @Test
    void whenSizeIsZeroOrNegativeThenThrowIllegalArgumentException() {
        // Spring Data не дозволяє створювати сторінки нульового або негативного розміру
        ItemPageRequest request = new ItemPageRequest(0, 0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            underTest.getItemsPage(request);
        }, "Має викидатися помилка IllegalArgumentException при нульовому розмірі сторінки");
    }
}