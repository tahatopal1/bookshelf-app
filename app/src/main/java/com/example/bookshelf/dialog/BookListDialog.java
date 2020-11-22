package com.example.bookshelf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.bookshelf.R;
import com.example.bookshelf.adapter.BookAdapter;
import com.example.bookshelf.model.Book;
import com.example.bookshelf.service.AppService;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BookListDialog extends AppCompatDialogFragment {

    private ListView mListView;
    private EditText mEditText;
    private Spinner mSpinner;

    private BookAdapter bookAdapter;
    private RestTemplate restTemplate;
    private AppService appService;

    private List<Book> bookList;

    private int bookApprovedSession;
    private String subUserSession;

    public BookListDialog(String subUserSession, int bookApprovedSession){
        this.subUserSession = subUserSession;
        this.bookApprovedSession = bookApprovedSession;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_book, null);

        mListView = view.findViewById(R.id.book_list_view);
        mEditText = view.findViewById(R.id.book_shelf_et);
        mSpinner = view.findViewById (R.id.category_spinner);

        bookList = new ArrayList<>();
        appService = new AppService(getActivity().getApplicationContext());
        mEditText.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);

        mSpinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, appService.getCategories()));

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String url = getActivity().getString(R.string.endpoint_home);
                URI targetUrl = null;

                targetUrl = UriComponentsBuilder.fromUriString(url)
                        .path(getActivity().getString(R.string.endpoint_usershelf).concat(getActivity().getString(R.string.endpoint_getbooks)))
                        .queryParam(getActivity().getString(R.string.username), subUserSession)
                        .queryParam(getActivity().getString(R.string.category), mSpinner.getSelectedItem().toString())
                        .queryParam(getActivity().getString(R.string.approved), bookApprovedSession)
                        .build()
                        .encode()
                        .toUri();

                try {
                    restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    Book[] books = restTemplate.getForObject(targetUrl, Book[].class);

                    bookList.clear();
                    new LinkedList<Book>(Arrays.asList(books)).forEach(book -> bookList.add(book));
                    bookAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bookAdapter = new BookAdapter(getContext(), R.layout.book_list_item, bookList, 2, subUserSession, bookApprovedSession);
        mListView.setAdapter(bookAdapter);

        builder.setView(view)
                .setTitle(getActivity().getString(R.string.options))
                .setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

}
