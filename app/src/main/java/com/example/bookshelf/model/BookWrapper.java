package com.example.bookshelf.model;

import com.example.bookshelf.model.Book;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookWrapper {
    private List<Book> items = new ArrayList<Book>();
}
