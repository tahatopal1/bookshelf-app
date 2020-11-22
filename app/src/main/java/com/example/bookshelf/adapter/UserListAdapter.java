package com.example.bookshelf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookshelf.R;
import com.example.bookshelf.model.AppUser;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<AppUser> {

    private Context mContext;
    private int mResource;

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<AppUser> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    static class ViewHolder{
        TextView tv_username;
        TextView tv_name;
        TextView tv_surname;
        TextView tv_userid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        AppUser appUser = getItem(position);
        ViewHolder viewHolder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = (View) inflater.inflate(mResource, parent, false);

            viewHolder.tv_username = convertView.findViewById(R.id.tv_username);
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
            viewHolder.tv_surname = convertView.findViewById(R.id.tv_surname);
            viewHolder.tv_userid = convertView.findViewById(R.id.tv_id);

            convertView.setTag(viewHolder);

        }else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.tv_username.setText(appUser.getUsername());
        viewHolder.tv_name.setText(appUser.getName());
        viewHolder.tv_surname.setText(appUser.getSurname());
        viewHolder.tv_userid.setText(String.valueOf(appUser.getId()));

        return convertView;
    }
}
