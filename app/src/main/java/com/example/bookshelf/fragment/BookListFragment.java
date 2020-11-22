package com.example.bookshelf.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookshelf.R;
import com.example.bookshelf.adapter.BookAdapter;
import com.example.bookshelf.model.AppUser;
import com.example.bookshelf.model.Book;
import com.example.bookshelf.model.BookListModel;
import com.example.bookshelf.model.BookWrapper;
import com.example.bookshelf.service.AppService;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BookListFragment extends Fragment {

    private ListView mListView;
    private EditText mEditText;
    private Spinner  mSpinner;

    private BookAdapter bookAdapter;
    private RestTemplate restTemplate;
    private Timer timer;
    private AppUser appUser;
    private AppService appService;

    private List<Book> bookList;

    private int bookSession;
    private int bookApprovedSession;
    private String username;
    private String subUserSession;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_book, container, false);
        mListView = root.findViewById(R.id.book_list_view);
        mEditText = root.findViewById(R.id.book_shelf_et);
        mSpinner = root.findViewById (R.id.category_spinner);

        bookSession = getArguments().getInt(getActivity().getString(R.string.bookSession));
        appService = new AppService(getActivity().getApplicationContext());
        username = getActivity().getIntent().getExtras().getString(getActivity().getString(R.string.userSession));


        if(bookSession == 0){

            // Admin google books session
            bookList = new ArrayList<>();

            mEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (timer != null) {
                        timer.cancel();
                    }
                }

                @Override
                public void afterTextChanged(final Editable s) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    bookList.clear();
                                    appService.trackList(s.toString()).forEach(book -> bookList.add(book));
                                    bookAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }, 1000);
                }
            });

            bookAdapter = new BookAdapter(getContext(), R.layout.book_list_item, bookList, bookSession, username);
            mListView.setAdapter(bookAdapter);

        }else if(bookSession == 1){

            // Admin shelf in admin session
            bookApprovedSession = getArguments().getInt(getActivity().getString(R.string.bookApprovedSession));
            mEditText.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);

            bookList = new ArrayList<>();
            mSpinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, appService.getCategories()));
            setSpinnerAction(mSpinner, new BookListModel(username, mSpinner.getSelectedItem().toString(), 0, bookList, bookAdapter, mSpinner));


            bookAdapter = new BookAdapter(getContext(), R.layout.book_list_item, bookList, bookSession, username);
            mListView.setAdapter(bookAdapter);

        }else if(bookSession == 2){

            // User shelf in admin session
            bookList = new ArrayList<>();
            bookApprovedSession = getArguments().getInt(getActivity().getString(R.string.bookApprovedSession));
            mEditText.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);
            subUserSession = getArguments().getString(getActivity().getString(R.string.subUserSession));

            mSpinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, appService.getCategories()));
            setSpinnerAction(mSpinner, new BookListModel(subUserSession, mSpinner.getSelectedItem().toString(), bookApprovedSession, bookList, bookAdapter, mSpinner));

            bookAdapter = new BookAdapter(getContext(), R.layout.book_list_item, bookList, bookSession, username, bookApprovedSession);
            mListView.setAdapter(bookAdapter);

        }else if(bookSession == 3){

            // Admin shelf in user session
            bookApprovedSession = getArguments().getInt(getActivity().getString(R.string.bookApprovedSession));
            mEditText.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);

            bookList = new ArrayList<>();
            appUser = appService.getCurrentUser(username);

            mSpinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, appService.getCategories()));
            setSpinnerAction(mSpinner, new BookListModel(appUser.getManager(), mSpinner.getSelectedItem().toString(), 0, bookList, bookAdapter, mSpinner));

            bookAdapter = new BookAdapter(getContext(), R.layout.book_list_item, bookList, bookSession, username, mSpinner);
            mListView.setAdapter(bookAdapter);


        }else if(bookSession == 4){

            // User shelf in user session
            bookApprovedSession = getArguments().getInt(getActivity().getString(R.string.bookApprovedSession));
            mEditText.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);
            bookList = new ArrayList<>();

            mSpinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, appService.getCategories()));
            setSpinnerAction(mSpinner, new BookListModel(username, mSpinner.getSelectedItem().toString(), bookApprovedSession, bookList, bookAdapter, mSpinner));
            bookAdapter = new BookAdapter(getContext(), R.layout.book_list_item, bookList, bookSession, username);
            mListView.setAdapter(bookAdapter);

        }

        return root;
    }





    public void listByCategory(BookListModel bookListModel){

        // Serves the book list by category

        String url = getActivity().getString(R.string.endpoint_home);
        URI targetUrl = null;

        targetUrl = UriComponentsBuilder.fromUriString(url)
                //.path("/usershelf/getBooks")
                .path(getActivity().getString(R.string.endpoint_usershelf).concat(getActivity().getString(R.string.endpoint_getbooks)))
                .queryParam(getActivity().getString(R.string.username), bookListModel.getUsername())
                .queryParam(getActivity().getString(R.string.category), mSpinner.getSelectedItem().toString())
                .queryParam(getActivity().getString(R.string.approved), bookListModel.getApproved())
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

    public void setSpinnerAction(Spinner spinner, BookListModel bookListModel){

        // Dedicates the action on spinner
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listByCategory(bookListModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


}
