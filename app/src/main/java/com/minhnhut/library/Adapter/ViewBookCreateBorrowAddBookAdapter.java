package com.minhnhut.library.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.minhnhut.library.DataObj.BorrowBook;
import com.minhnhut.library.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewBookCreateBorrowAddBookAdapter extends RecyclerView.Adapter<ViewBookCreateBorrowAddBookAdapter.ViewHolder> {

    Context context;
    ArrayList<BorrowBook> arrayList;

    public ViewBookCreateBorrowAddBookAdapter(Context context, ArrayList<BorrowBook> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewBookCreateBorrowAddBookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_view_book_create_borrow_add_book, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewBookCreateBorrowAddBookAdapter.ViewHolder viewHolder, int i) {
        BorrowBook itemBook = arrayList.get(i);
        String book_id = itemBook.book_id;
        String author = itemBook.author;
        String book_name = itemBook.book_name;
        String book_image = itemBook.book_image;

        viewHolder.tvBookId.setText(book_id);
        viewHolder.tvAuthor.setText(author);
        viewHolder.tvbookName.setText(book_name);
        Picasso.get().load(book_image).fit().centerInside().placeholder(R.drawable.noavatarbook).into(viewHolder.ivBookImage);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookImage, ivRemoveBook;
        TextView tvbookName, tvAuthor, tvBookId;
        EditText etQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookId = (TextView) itemView.findViewById(R.id.textViewItemBookId) ;
            tvbookName = (TextView) itemView.findViewById(R.id.ItemBookBookName) ;
            tvAuthor = (TextView) itemView.findViewById(R.id.ItemBookAuthor) ;
            ivBookImage = (ImageView) itemView.findViewById(R.id.ItemImageBook);

            ivRemoveBook = (ImageView) itemView.findViewById(R.id.imageViewRemoveBook);
            etQuantity = (EditText) itemView.findViewById(R.id.EditTextCreateBorrowAddBookQuantity);
        }

    }
}
