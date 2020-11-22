package com.example.bookshelf.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookshelf.R;
import com.example.bookshelf.dialog.CategoryChooseDialog;
import com.example.bookshelf.dialog.DescriptionDialog;
import com.example.bookshelf.model.Book;
import com.example.bookshelf.service.AppService;
import com.example.bookshelf.model.SessionResult;

import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    private Context mContext;
    private RestTemplate restTemplate;
    private AppService appService;

    private Spinner mSpinner;

    private int mResource;
    private int mBookSession;
    private int mBookApprovedSession;

    private String mUsername;

    private List<Book> mObjects;

    public BookAdapter(@NonNull Context context, int resource, @NonNull List<Book> objects, int bookSession, String username) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mBookSession = bookSession;
        mUsername = username;
        mObjects = objects;
    }

    public BookAdapter(@NonNull Context context, int resource, @NonNull List<Book> objects, int bookSession, String username, int bookApprovedSession) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mBookSession = bookSession;
        mUsername = username;
        mObjects = objects;
        mBookApprovedSession = bookApprovedSession;
    }

    public BookAdapter(Context context, int resource, List<Book> objects, int bookSession, String username, Spinner spinner) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mBookSession = bookSession;
        mUsername = username;
        mObjects = objects;
        mSpinner = spinner;
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView authorTextView;
        TextView publisherTextView;
        TextView yearTextView;
//        TextView  descriptionTextView;

        ImageView bookImageView;
        Button descButton;
        Button saveButton;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Book book = getItem(position);
        ViewHolder viewHolder = new ViewHolder();
        appService = new AppService(mContext);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = (View) inflater.inflate(mResource, parent, false);

            viewHolder.bookImageView = convertView.findViewById(R.id.book_image_view);
            viewHolder.titleTextView = convertView.findViewById(R.id.book_title);
            viewHolder.authorTextView = convertView.findViewById(R.id.book_author);
            viewHolder.publisherTextView = convertView.findViewById(R.id.book_publisher);
            viewHolder.yearTextView = convertView.findViewById(R.id.book_year);
            viewHolder.descButton = convertView.findViewById(R.id.btn_desc);
            viewHolder.saveButton = convertView.findViewById(R.id.btn_save);

            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.titleTextView.setText(book.getTitle());
        viewHolder.authorTextView.setText(getContext().getResources()
                .getString(R.string.author)
                .concat("\t")
                .concat(appService.stringAbsolute(book.getAuthor().toString())
                        .replace("[", "")
                        .replace("]", "")));

        viewHolder.publisherTextView.setText(getContext().getResources()
                .getString(R.string.publisher)
                .concat(appService.stringAbsolute(book.getPublisher())));

        viewHolder.yearTextView.setText(getContext().getResources()
                .getString(R.string.year)
                .concat(appService.stringAbsolute(book.getPublishedDate())));

        viewHolder.descButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (book.getDescription() != null) {
                    DescriptionDialog descriptionDialog = new DescriptionDialog(new StringBuilder(book.getDescription()));
                    descriptionDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "");
                } else
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.there_is_no_desc), Toast.LENGTH_LONG).show();
            }
        });

        if (mBookSession == 0) {
            viewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CategoryChooseDialog categoryChooseDialog = new CategoryChooseDialog(book, mUsername);
                    categoryChooseDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "Categories");
                }
            });
        } else if (mBookSession == 1) {

            viewHolder.saveButton.setText(convertView.getResources().getString(R.string.del));
            Drawable img = getContext().getResources().getDrawable(R.drawable.baseline_delete_black_18dp);
            viewHolder.saveButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

            viewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog
                            .Builder(((AppCompatActivity) mContext))
                            .setMessage(getContext().getResources().getString(R.string.discard_book))
                            .setPositiveButton(getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    SessionResult sessionResult = appService.deleteUserShelf(book);

                                    if (sessionResult.getErrorCode() == 1) {
                                        mObjects.remove(position);
                                        notifyDataSetChanged();
                                    }

                                    Toast.makeText(getContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton(getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            });


        } else if (mBookSession == 2) {
            if (mBookApprovedSession == 1) {

                viewHolder.saveButton.setText(convertView.getResources().getString(R.string.del));
                Drawable img = getContext().getResources().getDrawable(R.drawable.baseline_delete_black_18dp);
                viewHolder.saveButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                viewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog
                                .Builder(((AppCompatActivity) mContext))
                                .setMessage(getContext().getResources().getString(R.string.discard_book))
                                .setPositiveButton(getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        SessionResult sessionResult = appService.deleteUserShelf(book);

                                        if (sessionResult.getErrorCode() == 1) {
                                            mObjects.remove(position);
                                            notifyDataSetChanged();
                                        }

                                        Toast.makeText(getContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                });


            } else if (mBookApprovedSession == 0) {

                viewHolder.saveButton.setText(convertView.getResources().getString(R.string.approve));
                Drawable img = getContext().getResources().getDrawable(R.drawable.baseline_done_black_18dp);
                viewHolder.saveButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                viewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog
                                .Builder(((AppCompatActivity) mContext))
                                .setMessage(getContext().getResources().getString(R.string.approve_book))
                                .setPositiveButton(getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        restTemplate = new RestTemplate();

                                        String url = getContext().getResources().getString(R.string.endpoint_home)
                                                .concat(getContext().getResources().getString(R.string.endpoint_usershelf))
                                                .concat(getContext().getResources().getString(R.string.endpoint_approve));

                                        SessionResult sessionResult = restTemplate.postForObject(url, book, SessionResult.class);

                                        if (sessionResult.getErrorCode() == 1) {
                                            mObjects.remove(position);
                                            notifyDataSetChanged();
                                        }

                                        Toast.makeText(getContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                });

            }

            viewHolder.saveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            viewHolder.descButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

        } else if (mBookSession == 3) {

            viewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setMessage(getContext().getResources().getString(R.string.save_book))
                            .setNegativeButton(getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton(getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveBook(book, mUsername);
                                }
                            })
                            .show();
                }
            });

        } else if (mBookSession == 4) {

            viewHolder.saveButton.setText(getContext().getResources().getString(R.string.del));
            Drawable img = getContext().getResources().getDrawable(R.drawable.baseline_delete_black_18dp);
            viewHolder.saveButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

            viewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog
                            .Builder(((AppCompatActivity) mContext))
                            .setMessage(getContext().getResources().getString(R.string.discard_book))
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    SessionResult sessionResult = appService.deleteUserShelf(book);

                                    if (sessionResult.getErrorCode() == 1) {
                                        mObjects.remove(position);
                                        notifyDataSetChanged();
                                    }

                                    Toast.makeText(getContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton(getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            });
        }

        return convertView;

    }

    private void saveBook(Book mBook, String username) {
        restTemplate = new RestTemplate();
        appService = new AppService(mContext);

        String url = getContext().getResources().getString(R.string.endpoint_home)
                .concat(getContext().getResources().getString(R.string.endpoint_usershelf))
                .concat(getContext().getResources().getString(R.string.endpoint_savebook));

        try {

            switch (restTemplate.postForObject(new URI(url), appService.prepareShelf(mBook, username, mSpinner.getSelectedItem().toString()), Integer.class)) {
                case 0:
                    Toast.makeText(((AppCompatActivity) mContext).getApplicationContext(), getContext().getResources().getString(R.string.problem_occured), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(((AppCompatActivity) mContext).getApplicationContext(), getContext().getResources().getString(R.string.book_added), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(((AppCompatActivity) mContext).getApplicationContext(), getContext().getResources().getString(R.string.book_exists), Toast.LENGTH_SHORT).show();
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
