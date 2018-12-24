package com.minhnhut.library.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.minhnhut.library.Adapter.FragmentBookAdapter;
import com.minhnhut.library.DataObj.Book;
import com.minhnhut.library.R;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static com.minhnhut.library.DataObj.fBuild.StringUtils.removeAccent;
import static com.minhnhut.library.DataObj.fBuild.db_Books;

public class FragmentBook extends Fragment {

    View view;
    ArrayList<Book> arrayList;
    FragmentBookAdapter fragmentBookAdapter;
    RecyclerView recyclerView;
    Book book;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =( SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                final String search = s;
                if (search.length() <= 0){
                    arrayList.clear();
                    data();
                }else {
                    arrayList.clear();
                    Query filter = FirebaseDatabase.getInstance().getReference("books").orderByChild("book_name");
                    filter.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Book book = dataSnapshot.getValue(Book.class);
                            if (removeAccent(book.book_name).toLowerCase().contains(removeAccent(search).toLowerCase())) {
                                arrayList.add(new Book(dataSnapshot.getKey(), book.book_name, book.author, book.book_image));
                                fragmentBookAdapter.notifyDataSetChanged();
                            }
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
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_book, container, false);

        initRecyclerView();
        data();
        return view;
    }

    private void data() {
        db_Books.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                book = dataSnapshot.getValue(Book.class);
                arrayList.add(new Book(dataSnapshot.getKey(), book.book_name, book.author, book.book_image));
                fragmentBookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {fragmentBookAdapter.notifyDataSetChanged();}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void initRecyclerView(){
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerFragmentBook);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new ScaleInLeftAnimator());
        arrayList = new ArrayList<>();
        fragmentBookAdapter = new FragmentBookAdapter(getContext(),arrayList);
        recyclerView.setAdapter(fragmentBookAdapter);
    }
}
