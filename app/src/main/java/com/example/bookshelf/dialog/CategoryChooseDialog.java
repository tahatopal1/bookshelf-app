package com.example.bookshelf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.bookshelf.R;
import com.example.bookshelf.model.Book;
import com.example.bookshelf.model.UserShelf;
import com.example.bookshelf.service.AppService;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class CategoryChooseDialog extends AppCompatDialogFragment {

    private RestTemplate restTemplate;
    private AppService appService;

    private Spinner mSpinner;
    private Book mBook;
    private Button mButton;

    private String username;

    public CategoryChooseDialog(Book book, String username){
        this.mBook = book;
        this.username = username;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.category_choose_dialog, null);

        mSpinner = view.findViewById(R.id.category_spinner);
        mButton  = view.findViewById(R.id.category_button);
        appService = new AppService(getActivity().getApplicationContext());

        mSpinner.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, appService.getCategories()));

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBook(mBook, username);
                dismiss();
            }
        });

        builder.setView(view);

        return builder.create();

    }

    private void saveBook(Book mBook, String username) {
        restTemplate = new RestTemplate();

        String url = getActivity().getString(R.string.endpoint_home)
                .concat(getActivity().getString(R.string.endpoint_usershelf))
                .concat(getActivity().getString(R.string.endpoint_savebook));

        mBook.setCategory(mSpinner.getSelectedItem().toString());

        try{

            switch (restTemplate.postForObject(new URI(url), prepareShelf(mBook, username), Integer.class)){
                case 0:
                    Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.problem_occured), Toast.LENGTH_SHORT).show(); break;
                case 1:
                    Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.book_added), Toast.LENGTH_SHORT).show(); break;
                case 2:
                    Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.book_exists), Toast.LENGTH_SHORT).show(); break;

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private UserShelf prepareShelf(Book mBook, String username){
        UserShelf userShelf = new UserShelf();
        userShelf.setApproved(1);
        userShelf.setBook(mBook);
        userShelf.setUsername(username);
        return userShelf;
    }
}
