package com.example.bookshelf.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookshelf.R;
import com.example.bookshelf.model.AppUser;
import com.example.bookshelf.service.AppService;


import java.util.List;


public class MainPageFragment extends Fragment {

    private AppService appService;
    private AppUser appUser;

    private TextView tv_welcome;

    private TextView title1;
    private TextView title2;
    private TextView title3;
    private TextView title4;

    private TextView value1;
    private TextView value2;
    private TextView value3;
    private TextView value4;

    private List<String> titleList;

    private String username;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_page, container, false);

        username = getActivity().getIntent().getExtras().getString(getActivity().getString(R.string.userSession));
        appService = new AppService(getActivity().getApplicationContext());
        appUser = appService.getCurrentUser(username);
        init(root);

        tv_welcome.setText(getActivity().getString(R.string.welcome).concat(" ").concat( appUser.getName()));

        int session = getArguments().getInt(getActivity().getString(R.string.session));
        titleList = appService.getMenuTitles(session);

        title1.setText(titleList.get(0));
        title2.setText(titleList.get(1));

        if (session != 1){
            title3.setText(titleList.get(2));
            title4.setText(titleList.get(3));
        }

        if(session == 1){
            value1.setText(String.valueOf(appService.getBookCount(username, 1)));
            value2.setText(String.valueOf(appService.getBookCount(username, 0)));

            root.findViewById(R.id.big_layout2).setVisibility(View.GONE);

        }else if(session == 2){
            value1.setText(String.valueOf(appService.getBookCount(username, 1)));
            value2.setText(String.valueOf(appService.getBookCount(username, 0)));
            value3.setText(String.valueOf(appService.getUserCount(username, 1, 1)));
            value4.setText(String.valueOf(appService.getUserCount(username, 0, 1)));

        }else if(session == 3){
            value1.setText(String.valueOf(appService.getUserCount(username, 1, 1)));
            value2.setText(String.valueOf(appService.getUserCount(username, 2, 1)));
            value3.setText(String.valueOf(appService.getUserCount(username, 1, 2)));
            value4.setText(String.valueOf(appService.getUserCount(username, 0, 2)));
        }

        return root;
    }

    private void init(View root) {

        tv_welcome = root.findViewById(R.id.tv_welcome);

        title1 = root.findViewById(R.id.tv_title);
        title2 = root.findViewById(R.id.tv_title2);
        title3 = root.findViewById(R.id.tv_title3);
        title4 = root.findViewById(R.id.tv_title4);

        value1 = root.findViewById(R.id.tv_title_value);
        value2 = root.findViewById(R.id.tv_title_value2);
        value3 = root.findViewById(R.id.tv_title_value3);
        value4 = root.findViewById(R.id.tv_title_value4);

    }


}
