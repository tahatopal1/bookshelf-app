package com.example.bookshelf.model;

import android.widget.Spinner;

import com.example.bookshelf.adapter.BookAdapter;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookListModel {

    // Model consists of some datas/addresses that listing method

    private String username;
    private String category;
    private int    approved;

    private List<Book> bookList;
    private BookAdapter bookAdapter;

    private Spinner mSpinner;

}
