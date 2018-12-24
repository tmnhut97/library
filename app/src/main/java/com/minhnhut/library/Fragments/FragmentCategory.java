package com.minhnhut.library.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.minhnhut.library.Adapter.FragmentCategoryAdapter;
import com.minhnhut.library.DataObj.Category;
import com.minhnhut.library.R;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static com.minhnhut.library.DataObj.fBuild.db_category;

public class FragmentCategory extends Fragment {
    View view;
    RecyclerView recyclerView;
    FragmentCategoryAdapter fragmentCategoryAdapter;
    ArrayList<Category> arrayList;
    Category category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_category, container, false);
        initRecyclerView();
        data();
        return view;
    }


    private void data() {
        db_category.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                category = dataSnapshot.getValue(Category.class);
                arrayList.add(new Category(dataSnapshot.getKey(), category.category_name));
                fragmentCategoryAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void initRecyclerView(){
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerFragmentCategory);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new ScaleInLeftAnimator());
        arrayList = new ArrayList<>();
        fragmentCategoryAdapter = new FragmentCategoryAdapter(getContext(),arrayList);
        recyclerView.setAdapter(fragmentCategoryAdapter);
    }
}
