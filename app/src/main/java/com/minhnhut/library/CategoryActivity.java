package com.minhnhut.library;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.minhnhut.library.Adapter.CategoryAdapter;
import com.minhnhut.library.DataObj.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.minhnhut.library.DataObj.fBuild.StringUtils.removeAccent;

public class CategoryActivity extends AppCompatActivity {

    String ID;
    ArrayList<Category> arrayList;
    CategoryAdapter categoryAdapter;
    RecyclerView recyclerView;
    private DatabaseReference db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Danh mục");

        initRecyclerView();
        data();
        FloatingActionButton();
        GestureActivity(findViewById(R.id.CategoryActivity));

    }



    private void FloatingActionButton(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddCategory();
            }
        });
    }

    private void dialogAddCategory() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_category);
        dialog.setCanceledOnTouchOutside(false);



        final EditText etAddCategory = (EditText) dialog.findViewById(R.id.editTextAddCategory);
        final EditText etAddCategory_er = (EditText) dialog.findViewById(R.id.editTextAddCategory_er);

        Button btAddCategory = (Button) dialog.findViewById(R.id.buttonAddCategory);
        Button btCannelAddCategory = (Button) dialog.findViewById(R.id.buttonCannelAddCategory);

        btAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category_name = etAddCategory.getText().toString();
                if (category_name.length() < 3){
                    etAddCategory_er.setText("Tên danh mục phải nhiều hơn 3 kí tự");
                }
                else {
                    db = FirebaseDatabase.getInstance().getReference("categories");
                    ID = db.push().getKey();
                    Map<String, String> category = new HashMap<>();
                    category.put("category_name", category_name);
                    db.child(ID).setValue(category, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null){
                                Toast.makeText(CategoryActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                dialog.cancel();

                            }else { Toast.makeText(CategoryActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show(); }

                        }
                    });
                }

            }
        });
        btCannelAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void data() {
        db = FirebaseDatabase.getInstance().getReference("categories");
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Category category = dataSnapshot.getValue(Category.class);
                arrayList.add(new Category(dataSnapshot.getKey(), category.category_name));
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Category category = dataSnapshot.getValue(Category.class);
                Toast.makeText(CategoryActivity.this, "Danh mục đã được thay đổi thành: " + category.category_name, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerViewCategory);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(arrayList, getApplicationContext());
        recyclerView.setAdapter(categoryAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void GestureActivity(View view) {
        final GestureDetector gestureDetector = new GestureDetector(this,new CategoryActivity.BackGesture());
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }
    class BackGesture extends GestureDetector.SimpleOnGestureListener{

        int SWIPE_THRESHOLD =200;
        int SWIPE_VELOCITY_THRESHOLD =100;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e2.getX() - e1.getX() > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
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
                    Query filter = FirebaseDatabase.getInstance().getReference("categories").orderByChild("category_name");
                    filter.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Category category = dataSnapshot.getValue(Category.class);
                            if (removeAccent(category.category_name).toLowerCase().contains(removeAccent(search).toLowerCase())) {
                                arrayList.add(new Category(dataSnapshot.getKey(), category.category_name));
                                categoryAdapter.notifyDataSetChanged();
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
        return true;
    }
}
