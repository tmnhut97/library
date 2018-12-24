package com.minhnhut.library.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minhnhut.library.DataObj.Book;
import com.minhnhut.library.R;
import com.minhnhut.library.ViewDetailBookActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FragmentBookAdapter extends RecyclerView.Adapter<FragmentBookAdapter.ViewHolder> {

    Context context;
    ArrayList<Book> books;

    public FragmentBookAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_fragment_book, viewGroup, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Book itemBook = books.get(i);
        String book_id = itemBook.book_id;
        String author = itemBook.author;
        String book_name = itemBook.book_name;
        String book_image = itemBook.book_image;

        viewHolder.tvBookId.setText(book_id);
        viewHolder.tvAuthor.setText(book_name);
        viewHolder.tvbookName.setText(author);
        Picasso.get().load(book_image).fit().centerInside().placeholder(R.drawable.noavatarbook).into(viewHolder.ivBookImage);
    }
    @Override
    public int getItemCount() {
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookId, tvAuthor, tvbookName;
        ImageView ivBookImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookId = (TextView) itemView.findViewById(R.id.textViewItemFragmentBookId);
            tvAuthor = (TextView) itemView.findViewById(R.id.ItemFragmentBookBookName);
            tvbookName = (TextView) itemView.findViewById(R.id.ItemFragmentBookAuthor);
            ivBookImage = (ImageView) itemView.findViewById(R.id.ItemFragmentImageBook);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(),ViewDetailBookActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("book_id", tvBookId.getText().toString());
                    context.getApplicationContext().startActivity(intent);

                }
            });
        }
    }


}
