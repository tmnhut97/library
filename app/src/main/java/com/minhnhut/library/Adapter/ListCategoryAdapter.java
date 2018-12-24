package com.minhnhut.library.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.minhnhut.library.DataObj.Category;
import com.minhnhut.library.R;

import java.util.ArrayList;

public class ListCategoryAdapter extends ArrayAdapter<Category> {
    public ListCategoryAdapter(Context context, ArrayList<Category> arrayList) {
        super(context, 0, arrayList);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_list_category, parent, false
            );
        }
        TextView category_id = convertView.findViewById(R.id.textViewItemCategoryId);
        TextView category_name = convertView.findViewById(R.id.textViewItemCategoryName);
        Category category = (Category) getItem(position);

        if (category != null) {
            category_id.setText(category.category_id);
            category_name.setText(category.category_name);
        }

        return convertView;
    }
}
