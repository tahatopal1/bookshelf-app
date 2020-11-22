package com.example.bookshelf.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Book {
    private String id;
    private List<String> author;
    private String category;
    private String publisher;
    private String title;
    private String googleBooksId;
    private String publishedDate;
    private int pageCount;
    private int categoryId;
    private String description;


}
