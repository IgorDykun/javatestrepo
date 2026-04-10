package com.dykun.my_sandbox;

import com.dykun.my_sandbox.model.Item;
import com.dykun.my_sandbox.repository.ItemRepository;
import com.dykun.my_sandbox.request.ItemCreateRequest;
import com.dykun.my_sandbox.request.ItemPageRequest;
import com.dykun.my_sandbox.response.ApiResponse;
import com.dykun.my_sandbox.response.PaginationMetaData;
import com.dykun.my_sandbox.service.ItemService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoggingTest {

    @Autowired
    private ItemService underTest;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        itemRepository.save(new Item("69aeefcbe5c3dbd26376b0a8", "Iggy"));
        
        for (int i = 2; i <= 10; i++) {
            itemRepository.save(new Item(UUID.randomUUID().toString(), "Item " + i));
        }
    }

    @AfterEach
    void tearsDown() {
        itemRepository.deleteAll();
    }

    @Test
    void testLoggingOutputBeforeMethodGetById(CapturedOutput output) {
        Item item = underTest.getById("69aeefcbe5c3dbd26376b0a8");
        assertNotNull(item);
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("ItemService.getById"));
        assertTrue(output.toString().contains("69aeefcbe5c3dbd26376b0a8"));
    }

    @Test
    void testLoggingOutputAfterMethodGetById(CapturedOutput output) {
        Item item = underTest.getById("69aeefcbe5c3dbd26376b0a8");
        assertNotNull(item);
        assertTrue(output.toString().contains("ItemService.getById"));
        assertTrue(output.toString().contains("completed successfully"));
        assertTrue(output.toString().contains("69aeefcbe5c3dbd26376b0a8"));
        assertTrue(output.toString().contains("Iggy"));
    }

    @Test
    void testLoggingOutputBeforeMethodGetItemsPage(CapturedOutput output) {
        ItemPageRequest request = new ItemPageRequest(0, 5);
        ApiResponse<PaginationMetaData, Item> page = underTest.getItemsPage(request);
        assertNotNull(page);
        assertTrue(output.toString().contains("ItemService.getItemsPage"));
        assertTrue(output.toString().contains("0"));
        assertTrue(output.toString().contains("5"));
    }

    @Test
    void testLoggingOutputAfterMethodGetItemsPage(CapturedOutput output) {
        ItemPageRequest request = new ItemPageRequest(0, 5);
        ApiResponse<PaginationMetaData, Item> page = underTest.getItemsPage(request);
        
        assertNotNull(page);
        assertTrue(output.toString().contains("ItemService.getItemsPage"));
        assertTrue(output.toString().contains("completed successfully"));
        assertTrue(output.toString().contains("Returned items: 5"));
    }

    @Test
    void testLoggingOutputBeforeMethodCreateItem(CapturedOutput output) {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("New Logged Item");
        
        Item item = underTest.createItem(request);
        
        assertNotNull(item);
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("ItemService.createItem"));
        assertTrue(output.toString().contains("New Logged Item"));
    }

    @Test
    void testLoggingOutputAfterMethodCreateItem(CapturedOutput output) {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("Another Item");
        
        Item item = underTest.createItem(request);
        
        assertNotNull(item);
        assertTrue(output.toString().contains("ItemService.createItem"));
        assertTrue(output.toString().contains("completed successfully"));
        assertTrue(output.toString().contains(item.getId())); 
    }

    @Test
    void testLoggingOutputBeforeMethodDeleteItem(CapturedOutput output) {

        underTest.deleteItem("69aeefcbe5c3dbd26376b0a8");
        
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("ItemService.deleteItem"));
        assertTrue(output.toString().contains("69aeefcbe5c3dbd26376b0a8"));
    }

    @Test
    void testLoggingOutputAfterMethodDeleteItem(CapturedOutput output) {
        underTest.deleteItem("69aeefcbe5c3dbd26376b0a8");
        
        assertTrue(output.toString().contains("ItemService.deleteItem"));
        assertTrue(output.toString().contains("completed successfully"));
        assertTrue(output.toString().contains("69aeefcbe5c3dbd26376b0a8"));
    }
}