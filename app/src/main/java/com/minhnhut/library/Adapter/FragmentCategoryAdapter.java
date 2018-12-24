package com.minhnhut.library.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minhnhut.library.BooksWithCategoryActivity;
import com.minhnhut.library.DataObj.Category;
import com.minhnhut.library.R;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FragmentCategoryAdapter extends RecyclerView.Adapter<FragmentCategoryAdapter.ViewHolder> {

    Context context;
    ArrayList<Category> categories;

    public FragmentCategoryAdapter(Context context, ArrayList<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public FragmentCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_fragment_category, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentCategoryAdapter.ViewHolder viewHolder, int i) {
        Category itemCategory = categories.get(i);

        String category_id = itemCategory.category_id;
        String category_name = itemCategory.category_name;

        viewHolder.tvCategoryId.setText(category_id);
        viewHolder.tvCategoryName.setText(category_name);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategoryName;
        TextView tvCategoryId;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvCategoryId = (TextView) itemView.findViewById(R.id.itemFragmentCategoryId);
            tvCategoryName = (TextView) itemView.findViewById(R.id.itemFragmentCategoryName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String category_id = categories.get(getAdapterPosition()).category_id;
                    Intent intent = new Intent(context.getApplicationContext(),BooksWithCategoryActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("category_id", category_id);
                    context.getApplicationContext().startActivity(intent);
                }
            });
        }
    }
}
