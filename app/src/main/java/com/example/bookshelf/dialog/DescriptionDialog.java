package com.example.bookshelf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.bookshelf.R;

public class DescriptionDialog extends AppCompatDialogFragment {

    private TextView tv_description;
    private Button btn_dismiss;

    StringBuilder stringBuilder;

    public DescriptionDialog(StringBuilder stringBuilder){
        this.stringBuilder = stringBuilder;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_description, null);

        tv_description = view.findViewById(R.id.tv_description);
        btn_dismiss = view.findViewById(R.id.btn_dismiss);

        tv_description.setText(stringBuilder);
        tv_description.setMovementMethod(new ScrollingMovementMethod());

        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);

        return builder.show();
    }

}
