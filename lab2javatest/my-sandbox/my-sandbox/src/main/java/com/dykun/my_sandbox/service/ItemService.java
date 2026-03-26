package com.dykun.my_sandbox.service;

import com.dykun.my_sandbox.model.Item;
import com.dykun.my_sandbox.repository.ItemRepository;
import com.dykun.my_sandbox.request.ItemCreateRequest;
import com.dykun.my_sandbox.response.ApiResponse;
import com.dykun.my_sandbox.response.BaseMetaData;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item getById(String id) {
        return itemRepository.findById(id).orElse(null);
    }

    public ApiResponse<BaseMetaData, Item> getByIdAsApiResponse(String id) {
        Item item = getById(id);
        
        BaseMetaData meta = new BaseMetaData();
        ApiResponse<BaseMetaData, Item> response = new ApiResponse<>();

        if (item != null) {
            meta.setSuccess(true);
            meta.setCode(200);
            meta.setErrorMessage(null);
            response.setData(List.of(item)); 
        } else {
            meta.setSuccess(false);
            meta.setCode(404);
            meta.setErrorMessage("Not found");
            response.setData(Collections.emptyList()); 
        }

        response.setMeta(meta);
        return response;
    }
    public Item createItem(ItemCreateRequest request) {
        Item newItem = new Item(UUID.randomUUID().toString(), request.getName());
        return itemRepository.save(newItem);
    }
    public void deleteItem(String id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
        }
    }
    public Item updateItem(String id, String newName) {
        Item existingItem = getById(id);
        if (existingItem != null) {
            existingItem.setName(newName);
            return itemRepository.save(existingItem);
        }
        return null; 
    }
}