package com.dykun.my_sandbox;

import com.dykun.my_sandbox.model.Item;
import com.dykun.my_sandbox.repository.ItemRepository;
import com.dykun.my_sandbox.request.ItemCreateRequest;
import com.dykun.my_sandbox.response.ApiResponse;
import com.dykun.my_sandbox.response.BaseMetaData;
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
class ItemServiceTest {

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

        itemRepository.save(new Item("69aeefcbe5c3dbd26376b0a8", "Target Item for Teacher's Test"));

        for (int i = 2; i <= 30; i++) {
            itemRepository.save(new Item(UUID.randomUUID().toString(), "Item " + i));
        }
    }

    @AfterEach
    void tearsDown(){
        itemRepository.deleteAll();
    }

    @Test
    void whenGetAllItemsListThenSizeIs30() {
        int size = underTest.getAll().size();
        assertEquals(30, size, "Має повертати рівно 30 елементів");
    }

    @Test
    void whenItemIsPresentThenReturnAsOkApiResponse() {
        String id = "69aeefcbe5c3dbd26376b0a8"; 
        
        Item item = underTest.getById(id);
        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(id);
        
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
        assertNotNull(response.getData().get(0));
        assertTrue(response.getMeta().isSuccess());
        assertEquals(200, response.getMeta().getCode());
        assertNull(response.getMeta().getErrorMessage());
        assertEquals(item.getId(), response.getData().get(0).getId());
    }

@Test
    void whenItemIsNotPresentThenReturn400ApiResponseCode_404() {
        String id = "69aeefcbe5c3d";
        
        Item item = underTest.getById(id);
        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(id);
        
        assertNull(item, "Елемент має бути null, оскільки такого ID не існує");
        
        assertNotNull(response);
        assertTrue(response.getData().isEmpty());
        assertFalse(response.getMeta().isSuccess());
        assertEquals(404, response.getMeta().getCode());
        assertNotNull(response.getMeta().getErrorMessage());
        assertEquals("Not found", response.getMeta().getErrorMessage());
    }


    @Test
    void whenCreateItemThenItemIsSavedAndReturned() {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("New Awesome Item");

        Item createdItem = underTest.createItem(request);

        assertNotNull(createdItem.getId(), "ID новоствореного елемента не має бути null");
        assertEquals("New Awesome Item", createdItem.getName(), "Ім'я має збігатися з переданим у реквесті");
        
        assertEquals(31, underTest.getAll().size());
    }
    @Test
    void whenDeleteItemThenSizeIsDecreased() {
        String idToDelete = "69aeefcbe5c3dbd26376b0a8";
        
        assertEquals(30, underTest.getAll().size());

        underTest.deleteItem(idToDelete);

        assertEquals(29, underTest.getAll().size(), "Кількість елементів має зменшитись на 1");
        assertNull(underTest.getById(idToDelete), "Видалений елемент не повинен знаходитись у базі");
    }
    @Test
    void whenUpdateItemThenNameIsChanged() {
        String idToUpdate = "69aeefcbe5c3dbd26376b0a8";
        String newName = "Оновлене супер ім'я";

        Item updatedItem = underTest.updateItem(idToUpdate, newName);

        assertNotNull(updatedItem, "Оновлений елемент не має бути null");
        assertEquals(newName, updatedItem.getName(), "Ім'я елемента мало змінитись");
        
        Item itemFromDb = underTest.getById(idToUpdate);
        assertEquals(newName, itemFromDb.getName());
    }

    @Test
    void whenDeleteNonExistentItemThenSizeRemainsSame() {
        String fakeId = "some-fake-id-123";
        int initialSize = underTest.getAll().size(); 

        underTest.deleteItem(fakeId);

        assertEquals(initialSize, underTest.getAll().size(), "Кількість елементів не повинна змінитись при видаленні неіснуючого ID");
    }
}