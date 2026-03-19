package com.dykun.my_sandbox;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    public Item() {}

    public Item(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}