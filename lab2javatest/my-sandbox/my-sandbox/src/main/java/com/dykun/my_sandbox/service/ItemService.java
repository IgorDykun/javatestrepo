package com.dykun.my_sandbox.service;

import com.dykun.my_sandbox.model.Item;
import com.dykun.my_sandbox.repository.ItemRepository;
import com.dykun.my_sandbox.request.ItemCreateRequest;
import com.dykun.my_sandbox.request.ItemPageRequest;
import com.dykun.my_sandbox.response.ApiResponse;
import com.dykun.my_sandbox.response.BaseMetaData;
import com.dykun.my_sandbox.response.PaginationMetaData;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public ApiResponse<PaginationMetaData, Item> getItemsPage(ItemPageRequest request) {
        ApiResponse<PaginationMetaData, Item> response = new ApiResponse<>();
        PaginationMetaData meta = new PaginationMetaData();
        
        long totalElements = itemRepository.count();

        if (totalElements == 0) {
            meta.setSuccess(false);
            meta.setCode(404);
            meta.setErrorMessage("The list is empty");
            response.setData(Collections.emptyList());
        } 
        else if (request.getPage() * request.getSize() >= totalElements && request.getPage() != 0) {
            meta.setSuccess(false);
            meta.setCode(400);
            meta.setErrorMessage("Page value is out of range");
            response.setData(Collections.emptyList());
        } 
        else {
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
            Page<Item> pageResult = itemRepository.findAll(pageable);
            
            meta.setSuccess(true);
            meta.setCode(200);
            meta.setErrorMessage(null);
            meta.setNumber(pageResult.getNumber());
            meta.setSize(pageResult.getSize());
            meta.setTotalElements(pageResult.getTotalElements());
            meta.setTotalPages(pageResult.getTotalPages());
            meta.setFirst(pageResult.isFirst());
            meta.setLast(pageResult.isLast());
            
            response.setData(pageResult.getContent());
        }
        
        response.setMeta(meta);
        return response;
    }
}