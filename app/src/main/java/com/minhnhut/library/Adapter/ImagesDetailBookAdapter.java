package com.minhnhut.library.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.minhnhut.library.R;

import java.util.ArrayList;

public class ImagesDetailBookAdapter extends RecyclerView.Adapter<ImagesDetailBookAdapter.ViewHolder> {
    Context context;
    ArrayList<Bitmap> arrayList;

    public ImagesDetailBookAdapter(Context context, ArrayList<Bitmap> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_image_detailbook, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Bitmap itemImageBook = arrayList.get(i);

        viewHolder.ivImageDetailBook.setImageBitmap(itemImageBook);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImageDetailBook;
        ImageView ivDeleteItemBook;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImageDetailBook = (ImageView)  itemView.findViewById(R.id.ImageViewImageDetailBook);
            ivDeleteItemBook = (ImageView)  itemView.findViewById(R.id.imageViewDeleteItemBook);

            ivDeleteItemBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });

        }
    }
}
