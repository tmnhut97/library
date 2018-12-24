package com.minhnhut.library;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minhnhut.library.Adapter.ListCategoryAdapter;
import com.minhnhut.library.DataObj.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.minhnhut.library.DataObj.fBuild.ChooseDate;

public class Input_Details_BookActivity extends AppCompatActivity {
    private DatabaseReference db;
    private String book_id;
    private String book_name;
    private String book_image;
    private String author;
    private String nxb;
    private String publishing_year;
    private String language;
    private String quantity;
    private String category_id;
    private String category_name;
    String intent_category_id;
    ArrayList<Category> arrayListCategory;
    ImageView ivResetDetailBookPublishingYear;
    TextView etDetailBookPublishingYear;
    TextView tvInputDetailBookBookName;
    EditText etDetailBookQuantity;
    EditText etDetailBookNXB;
    EditText etDetailBookLanguage;
    Spinner snCategory;
    FloatingActionButton fabSaveDetailBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input__details__book);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Cập nhật thông tin sách");
        GestureActivity(findViewById(R.id.activityInputDetailBook));

        snCategory = (Spinner) findViewById(R.id.TextViewDetailBookCategory);
        etDetailBookPublishingYear = (TextView) findViewById(R.id.EditTextDetailBookPublishingYear) ;
        ivResetDetailBookPublishingYear = (ImageView) findViewById(R.id.ImageViewResetDetailBookPublishingYear) ;
        etDetailBookQuantity = (EditText) findViewById(R.id.EditTextDetailBookQuantity) ;
        etDetailBookNXB = (EditText) findViewById(R.id.EditTextDetailBookNXB) ;
        etDetailBookLanguage = (EditText) findViewById(R.id.EditTextDetailBookLanguage) ;
        tvInputDetailBookBookName = (TextView) findViewById(R.id.TextViewInputDetailBookBookName);
        fabSaveDetailBook = (FloatingActionButton) findViewById(R.id.fabSaveDetailBook);

        getDataDetailsBook();
        SaveData();
        ListCategory();

//
        etDetailBookPublishingYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseDate(etDetailBookPublishingYear, Input_Details_BookActivity.this);
            }
        });
    }

    private void getDataDetailsBook() {
        Intent intent    = getIntent();
        book_id          = intent.getStringExtra("book_id");
        book_name        = intent.getStringExtra("book_name");
        book_image        = intent.getStringExtra("book_image");
        author        = intent.getStringExtra("author");
        nxb        = intent.getStringExtra("nxb");
        publishing_year        = intent.getStringExtra("publishing_year");
        language        = intent.getStringExtra("language");
        quantity        = intent.getStringExtra("quantity");
        intent_category_id        = intent.getStringExtra("category_id");
        category_name        = intent.getStringExtra("category_name");

        tvInputDetailBookBookName.setText(book_name);
        etDetailBookQuantity.setText(quantity);
        etDetailBookNXB.setText(nxb);
        etDetailBookLanguage.setText(language);
        etDetailBookPublishingYear.setText(publishing_year);

        ivResetDetailBookPublishingYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDetailBookPublishingYear.setText(publishing_year);
            }
        });

    }

    private void SaveData() {
        fabSaveDetailBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = etDetailBookQuantity.getText().toString();
                String nxb = etDetailBookNXB.getText().toString();
                String publishing_year = etDetailBookPublishingYear.getText().toString();
                String language = etDetailBookLanguage.getText().toString();
                db = FirebaseDatabase.getInstance().getReference("books");
                Map<String, String> book_details = new HashMap<>();
                book_details.put("book_name", book_name);
                book_details.put("book_image", book_image);
                book_details.put("author", author);
                book_details.put("quantity", quantity);
                book_details.put("nxb", nxb);
                book_details.put("language", language);
                book_details.put("publishing_year", publishing_year);
                book_details.put("category_id", category_id);
                db.child(book_id).setValue(book_details, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null){
                            Toast.makeText(Input_Details_BookActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                            finish();

                        }else { Toast.makeText(Input_Details_BookActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show(); }
                    }
                });
            }
        });
    }


    private void ListCategory() {
        arrayListCategory = new ArrayList<>();
        arrayListCategory.clear();
        final Category cate = new Category(intent_category_id,category_name);
//        arrayListCategory.add(cate);
        if (intent_category_id == "-1"){
            arrayListCategory.add(new Category("0", "Chọn danh mục"));
        }
        else {
            arrayListCategory.add(cate);
        }
        final ListCategoryAdapter listCategoryAdapter = new ListCategoryAdapter(Input_Details_BookActivity.this, arrayListCategory);
        snCategory.setAdapter(listCategoryAdapter);
        db = FirebaseDatabase.getInstance().getReference("categories");
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Category category = dataSnapshot.getValue(Category.class);
                Category catenew = new Category(dataSnapshot.getKey(), category.category_name);
                arrayListCategory.add(catenew);
                listCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                listCategoryAdapter.notifyDataSetChanged();
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
        snCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = arrayListCategory.get(position);
                category_id = category.category_id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
    private void GestureActivity(View view) {
        final GestureDetector gestureDetector = new GestureDetector(this,new Input_Details_BookActivity.BackGesture());
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
                finish();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
