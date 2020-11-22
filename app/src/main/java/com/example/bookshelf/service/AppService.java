package com.example.bookshelf.service;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import com.example.bookshelf.R;
import com.example.bookshelf.model.AppUser;
import com.example.bookshelf.model.Book;
import com.example.bookshelf.model.BookWrapper;
import com.example.bookshelf.model.SessionResult;
import com.example.bookshelf.model.UserShelf;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AppService {

    Context context;
    private String endpoint_home;

    // The class where the methods are virtualized

    private RestTemplate restTemplate;

    public AppService(Context context){
        this.context = context;
        endpoint_home = context.getString(R.string.endpoint_home);
    }

    public UserShelf prepareShelf(Book mBook, String username, String category) {

        // Prepares a UserShelf object

        UserShelf userShelf = new UserShelf();

        mBook.setId(null);
        mBook.setCategory(category);
        userShelf.setApproved(0);

        userShelf.setBook(mBook);
        userShelf.setUsername(username);
        return userShelf;
    }


    public List<String> getCategories() {
        restTemplate = new RestTemplate();
        String url = endpoint_home.concat(context.getString(R.string.endpoint_book)).concat(context.getString(R.string.endpoint_categories));
        String[] categoryArray = new String[0];

        try {
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            categoryArray = restTemplate.getForObject(url, String[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Arrays.asList(categoryArray);
    }

    public AppUser getCurrentUser(String username) {
        restTemplate = new RestTemplate();

        URI targetUrl = UriComponentsBuilder.fromUriString(endpoint_home)
                //.path("/user/get-user")
                .path(context.getString(R.string.endpoint_user).concat(context.getString(R.string.endpoint_getuser)))
                .queryParam(context.getString(R.string.username), username)
                .build()
                .encode()
                .toUri();

        return restTemplate.getForObject(targetUrl, AppUser.class);
    }

    public List<String> getMenuTitles(int session) {
        restTemplate = new RestTemplate();

        URI targetUrl = UriComponentsBuilder.fromUriString(endpoint_home)
                //.path("/common/menu-titles")
                .path(context.getString(R.string.endpoint_common).concat(context.getString(R.string.endpoint_menutitles)))
                .queryParam(context.getString(R.string.session), session)
                .build()
                .encode()
                .toUri();

        return new LinkedList<>(Arrays.asList(restTemplate.getForObject(targetUrl, String[].class)));
    }

    public int getBookCount(String username, int approved) {
        restTemplate = new RestTemplate();

        URI targetUrl = UriComponentsBuilder.fromUriString(endpoint_home)
                //.path("/usershelf/getBookCount")
                .path(context.getString(R.string.endpoint_usershelf).concat(context.getString(R.string.endpoint_getbookCount)))
                .queryParam(context.getString(R.string.username), username)
                .queryParam(context.getString(R.string.approved), approved)
                .build()
                .encode()
                .toUri();

        return restTemplate.getForObject(targetUrl, Integer.class);
    }

    public int getUserCount(String username, int approved, int authcode) {

        restTemplate = new RestTemplate();
        URI targetUrl = UriComponentsBuilder.fromUriString(endpoint_home)
                //.path("/user/user-count")
                .path(context.getString(R.string.endpoint_user).concat(context.getString(R.string.endpoint_usercount)))
                .queryParam(context.getString(R.string.username), username)
                .queryParam(context.getString(R.string.approved), approved)
                .queryParam(context.getString(R.string.authcode), authcode)
                .build()
                .encode()
                .toUri();

        return restTemplate.getForObject(targetUrl, Integer.class);

    }

    public AppUser[]  getUsers(int authcode, int approved, String manager) {

        restTemplate = new RestTemplate();
        URI targetUrl = UriComponentsBuilder.fromUriString(endpoint_home)
                //.path("user/get-non-approved")
                .path(context.getString(R.string.endpoint_user).concat(context.getString(R.string.get_non_approved)))
                .queryParam(context.getString(R.string.approved), approved)
                .queryParam(context.getString(R.string.authcode), authcode)
                .queryParam(context.getString(R.string.manager), manager)
                .build()
                .encode()
                .toUri();

        return restTemplate.getForObject(targetUrl, AppUser[].class);

    }

    public String stringAbsolute(String str){
        if(str != null)
            return str;
        return "";
    }

    public void addFilter(EditText editText, Boolean numEnable) {

        String regex;

        if (numEnable)
            regex = "[a-zA-Z0-9 ]+";
        else
            regex = "[a-zA-Z ]+";

        // Prevents typing number on field
        editText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {

                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z0-9 ]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });
    }

    public SessionResult login(String username, String password){

        restTemplate = new RestTemplate();

        AppUser appUser = new AppUser();
        String url = endpoint_home
                                .concat(context.getString(R.string.endpoint_user)
                                .concat(context.getString(R.string.endpoint_login)));

        appUser.setUsername(username);
        appUser.setPassword(password);

        return restTemplate.postForObject(url, appUser, SessionResult.class);
    }

    public SessionResult update(AppUser appUser){

        restTemplate = new RestTemplate();
        String url = endpoint_home
                        .concat(context.getString(R.string.endpoint_user)
                        .concat(context.getString(R.string.endpoint_update)));

        return restTemplate.postForObject(url, appUser, SessionResult.class);

    }

    public SessionResult signup(AppUser appUser){

        restTemplate = new RestTemplate();
        String url = endpoint_home
                .concat(context.getString(R.string.endpoint_user)
                        .concat(context.getString(R.string.endpoint_signup)));

        return restTemplate.postForObject(url, appUser, SessionResult.class);

    }

    public Boolean validateManager(String manager) {

        restTemplate = new RestTemplate();

        URI targetUrl = UriComponentsBuilder.fromUriString(endpoint_home)
                .path(context.getString(R.string.endpoint_user).concat(context.getString(R.string.validate_manager)))
                .queryParam(context.getString(R.string.manager), manager)
                .build()
                .encode()
                .toUri();

        Boolean bool = restTemplate.getForObject(targetUrl, Boolean.class);
        return bool;
    }

    public SessionResult deleteUserShelf(Book book){

        restTemplate = new RestTemplate();
        //String url = "http://10.0.2.2:4444/usershelf/delete";

        return restTemplate.postForObject(endpoint_home.concat(context.getString(R.string.endpoint_usershelf))
                                                                .concat(context.getString(R.string.endpoint_delete)), book, SessionResult.class);
    }

    public SessionResult approveUser(int approved, String username){

        restTemplate = new RestTemplate();
        URI targetUrl = UriComponentsBuilder.fromUriString(endpoint_home)
                //.path("/user/approve")
                .path(context.getString(R.string.endpoint_user).concat(context.getString(R.string.endpoint_approve)))
                .queryParam(context.getString(R.string.approved), approved)
                .queryParam(context.getString(R.string.username), username)
                .build()
                .encode()
                .toUri();

        return restTemplate.getForObject(targetUrl, SessionResult.class);
    }

    public SessionResult deleteUser(AppUser appUser){

        restTemplate = new RestTemplate();
        return restTemplate
                .postForObject(endpoint_home.concat(context.getString(R.string.endpoint_user)
                        .concat(context.getString(R.string.endpoint_delete))), appUser, SessionResult.class);

    }

    public List<Book> trackList(String str) {
        restTemplate = new RestTemplate();
        List<Book> bookList = new ArrayList<>();
        URI targetUrl = null;

        targetUrl = UriComponentsBuilder.fromUriString(endpoint_home)
                //.path("/book/getBooks")
                .path(context.getString(R.string.endpoint_book).concat(context.getString(R.string.endpoint_getbooks)))
                .queryParam("q", str)
                .build()
                .encode()
                .toUri();

        try {
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            BookWrapper bookWrapper = restTemplate.getForObject(targetUrl, BookWrapper.class);
            bookList = bookWrapper.getItems();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return bookList;
    }


}
