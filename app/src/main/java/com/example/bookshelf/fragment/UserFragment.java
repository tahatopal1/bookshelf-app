package com.example.bookshelf.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookshelf.R;
import com.example.bookshelf.adapter.UserListAdapter;
import com.example.bookshelf.dialog.BookListDialog;
import com.example.bookshelf.model.AppUser;
import com.example.bookshelf.model.SessionResult;
import com.example.bookshelf.service.AppService;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class UserFragment extends Fragment {

    private ListView mListView;
    private Spinner mSpinner;

    private Bundle bundle;
    private AppUser appUser;
    private UserListAdapter userListAdapter;
    private RestTemplate restTemplate;
    private AppService appService;

    private int session;
    private int approvedCode;
    private String manager;
    private List<AppUser> appUserList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        bundle = getArguments();
        session = bundle.getInt(getActivity().getString(R.string.session));
        approvedCode = bundle.getInt(getActivity().getString(R.string.approvedcode));
        appService = new AppService(getActivity().getApplicationContext());


        if (session == 0) {
            // Admin session
            mListView = root.findViewById(R.id.user_list_view);
            appUserList = new ArrayList<>();

            appUser = appService.getCurrentUser(getActivity().getIntent().getExtras().getString("userSession"));
            manager = appUser.getUsername();

            appUserList = getUsers(1, approvedCode);
            userListAdapter = new UserListAdapter(getContext(), R.layout.user_list_item, appUserList);
            mListView.setAdapter(userListAdapter);

            if (approvedCode == 0) {
                // Admin session
                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        AppUser appUser = appUserList.get(position);

                        new AlertDialog.Builder(getActivity())
                                .setMessage(getActivity().getString(R.string.accept_user))
                                .setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        SessionResult sessionResult = appService.approveUser(2, appUser.getUsername());

                                        if (sessionResult.getErrorCode() == 1) {
                                            appUserList.remove(position);
                                            userListAdapter.notifyDataSetChanged();
                                        }

                                        Toast.makeText(getContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        SessionResult sessionResult = appService.deleteUser(appUser);

                                        if (sessionResult.getErrorCode() == 1) {
                                            appUserList.remove(position);
                                            userListAdapter.notifyDataSetChanged();
                                        }

                                        Toast.makeText(getContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();

                                    }
                                })
                                .show();

                        return false;
                    }
                });
            } else if (approvedCode == 1) {
                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        AppUser appUser = appUserList.get(position);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                .setTitle(getActivity().getString(R.string.options))
                                .setMessage(getActivity().getString(R.string.choose_option))
                                .setPositiveButton(getActivity().getString(R.string.approved), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        BookListDialog bookListDialog = new BookListDialog(appUser.getUsername(), 1);
                                        bookListDialog.show(getActivity().getSupportFragmentManager(), getActivity().getString(R.string.approved_books));
                                    }
                                })
                                .setNegativeButton(getActivity().getString(R.string.waiting), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        BookListDialog bookListDialog = new BookListDialog(appUser.getUsername(), 0);
                                        bookListDialog.show(getActivity().getSupportFragmentManager(), getActivity().getString(R.string.approved_books));
                                    }
                                })
                                .setNeutralButton(getActivity().getString(R.string.remove_user), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        SessionResult sessionResult = appService.deleteUser(appUser);
                                        Toast.makeText(getContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();

                                        List<AppUser> tempList = getUsers(1, approvedCode);
                                        appUserList.clear();

                                        tempList.forEach(user -> appUserList.add(user));
                                        userListAdapter.notifyDataSetChanged();
                                    }
                                });

                        builder.show();


                        return false;
                    }
                });

            }

        } else if (session == 1) {
            // AppMaster session
            mListView = root.findViewById(R.id.user_list_view);

            manager = getActivity().getString(R.string.appmaster);

            mSpinner = (Spinner) root.findViewById(R.id.spinner_user);
            mSpinner.setVisibility(View.VISIBLE);
            mSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, getActivity().getResources().getStringArray(R.array.roles)));

            appUserList = new ArrayList<>();

            if (approvedCode == 0) {

                appUserList = getUsers(1, 2);
                userListAdapter = new UserListAdapter(getContext(), R.layout.user_list_item, appUserList);
                mListView.setAdapter(userListAdapter);

                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        appUserList.clear();
                        getUsers(position + 1, (position == 0) ? 2 : 0).forEach(appUser -> appUserList.add(appUser));
                        userListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        AppUser appUser = appUserList.get(position);

                        new AlertDialog.Builder(getActivity())
                                .setMessage(getActivity().getString(R.string.accept_user))
                                .setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        SessionResult sessionResult = appService.approveUser(1, appUser.getUsername());

                                        if (sessionResult.getErrorCode() == 1) {
                                            appUserList.remove(position);
                                            userListAdapter.notifyDataSetChanged();
                                        }

                                        Toast.makeText(getContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        SessionResult sessionResult = appService.deleteUser(appUser);

                                        if (sessionResult.getErrorCode() == 1) {
                                            appUserList.remove(position);
                                            userListAdapter.notifyDataSetChanged();
                                        }

                                        Toast.makeText(getContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();

                                    }
                                })
                                .show();

                        return false;
                    }
                });


            } else if (approvedCode == 1) {

                appUserList = getUsers(1, 1);
                userListAdapter = new UserListAdapter(getContext(), R.layout.user_list_item, appUserList);
                mListView.setAdapter(userListAdapter);

                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        appUserList.clear();
                        getUsers(position + 1, 1).forEach(appUser -> appUserList.add(appUser));
                        userListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        AppUser appUser = appUserList.get(position);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                .setTitle(getActivity().getString(R.string.delete_user))
                                .setMessage(getActivity().getString(R.string.will_you_remove))
                                .setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SessionResult sessionResult = appService.deleteUser(appUser);
                                        Toast.makeText(getContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();

                                        List<AppUser> tempList = getUsers(1, approvedCode);
                                        appUserList.clear();

                                        tempList.forEach(user -> appUserList.add(user));
                                        userListAdapter.notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                        builder.show();

                        return false;
                    }
                });
            }

        }


        return root;
    }

    private List<AppUser> getUsers(int authcode, int approved) {

        // Arrays.asList returns a fixed list, we cannot remove any element from it. That's why LinkedList is used.
        List<AppUser> list = new LinkedList<AppUser>(Arrays.asList(appService.getUsers(authcode, approved, manager)));
        return list;

    }
}
