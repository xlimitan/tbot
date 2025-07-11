package com.tbot.tbot.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Category parent;

    // Список дочерних категорий.
    // mappedBy = "parent" — связь устанавливается по полю parent в дочерней категории.
    // cascade = CascadeType.ALL — все операции (persist, remove и др.) применяются к дочерним категориям.
    // orphanRemoval = true — при удалении дочерней категории из списка она также удаляется из базы данных.
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }
}
