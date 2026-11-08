package com.elibrary.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{validation.title.required}")
    @Size(max = 180, message = "{validation.title.max}")
    @Column(nullable = false, length = 180)
    private String title;

    @NotBlank(message = "{validation.isbn.required}")
    @Size(max = 30, message = "{validation.isbn.max}")
    @Column(nullable = false, unique = true, length = 30)
    private String isbn;

    @NotBlank(message = "{validation.author.required}")
    @Size(max = 160, message = "{validation.author.max}")
    @Column(nullable = false, length = 160)
    private String author;

    @NotBlank(message = "{validation.category.required}")
    @Size(max = 100, message = "{validation.category.max}")
    @Column(nullable = false, length = 100)
    private String category;

    @Size(max = 1000, message = "{validation.description.max}")
    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    public Book() {
    }

    public Book(String title, String isbn, String author, String category, String description) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.category = category;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
