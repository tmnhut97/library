package com.minhnhut.library;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.minhnhut.library.Adapter.ShowImageDetailBookAdapter;
import com.minhnhut.library.DataObj.Book;
import com.minhnhut.library.DataObj.BookDetail;
import com.minhnhut.library.DataObj.Category;
import com.minhnhut.library.DataObj.Image;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.minhnhut.library.DataObj.fBuild.anim_hideToRight;
import static com.minhnhut.library.DataObj.fBuild.anim_showToRight;

public class Details_BookActivity extends AppCompatActivity {
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

    ImageView ivDetailImageBook;
    TextView tvDetailBookBookName;
    TextView tvDetailBookAuthor;
    TextView tvDetailBookQuantity;
    TextView tvDetailBookCategory;
    TextView tvDetailBookNXB;
    TextView tvDetailBookPublishingYear;
    TextView tvDetailBookLanguge;
    Button btInputDetailBook;

    ArrayList<Image> arrayListImage;
    ShowImageDetailBookAdapter showImageDetailBookAdapter;
    RecyclerView recyclerView;



    int PICK_IMAGE_MULTIPLE = 8989;
    ImageView ivAddImageDetailBook;
    final int CAMERA = 11;
    final int LIBRARY_PICTURE = 22;
    Bitmap bitmap;
    int def = 0;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details__book);

        StyleViewDetailBook();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Chi tiết sách");
        Intent intent    = getIntent();
        book_id          = intent.getStringExtra("book_id");

        getdataBook();
        InputDetailBook();

        initRecyclerView();
        getImagesDetailBook();

        GestureActivity(findViewById(R.id.activityDetailBook));

    }

    private void getImagesDetailBook() {
        Query filter = FirebaseDatabase.getInstance().getReference("image").orderByChild("book_id").equalTo(book_id);
        filter.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Image image = dataSnapshot.getValue(Image.class);
                arrayListImage.add(new Image(image.book_id, image.url));
                showImageDetailBookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Toast.makeText(Details_BookActivity.this, "Co hinh bị xoá", Toast.LENGTH_SHORT).show();
                Log.d("delete>>", dataSnapshot+ "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerViewImageDetailBook);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        arrayListImage = new ArrayList<Image>();
        showImageDetailBookAdapter = new ShowImageDetailBookAdapter(getApplicationContext(), arrayListImage);
        recyclerView.setAdapter(showImageDetailBookAdapter);

    }
    private void StyleViewDetailBook() {
        final ImageView ivStyleViewDetailBook = (ImageView) findViewById(R.id.imageViewStyleViewDetailBook);
        final LinearLayout LayoutDetailBookViewList = (LinearLayout) findViewById(R.id.LayoutDetailBookViewList) ;
        final RelativeLayout LayoutDetailBookViewPicture = (RelativeLayout) findViewById(R.id.LayoutDetailBookViewPicture) ;
        ivAddImageDetailBook = (ImageView) findViewById(R.id.ImageViewAddImageDetailBook);


        ivStyleViewDetailBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                def = (def != 1) ? 1 : 0;
                switch (def){
                    case 0:
                        ivStyleViewDetailBook.setImageResource(R.drawable.ic_photo_library);
                        anim_hideToRight(LayoutDetailBookViewPicture);
                        ivAddImageDetailBook.setVisibility(View.GONE);
                        anim_showToRight(LayoutDetailBookViewList);
//                        anim_showToRight(btInputDetailBook);
                        btInputDetailBook.setEnabled(true);
                        break;
                    case 1:
                        ivStyleViewDetailBook.setImageResource(R.drawable.ic_list_text);
                        anim_hideToRight(LayoutDetailBookViewList);
//                        anim_hideToRight(btInputDetailBook);/
                        btInputDetailBook.setEnabled(false);
                        anim_showToRight(LayoutDetailBookViewPicture);
                        ivAddImageDetailBook.setVisibility(View.VISIBLE);
                        ivAddImageDetailBook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Details_BookActivity.this, Images_Detail_BookActivity.class);
                                intent.putExtra("book_id", book_id);
                                intent.putExtra("book_name", book_name);
                                startActivity(intent);
                            }
                        });
                        break;
                }
            }
        });
    }

    private void InputDetailBook() {
        btInputDetailBook = (Button) findViewById(R.id.btInputDetailBook);
        btInputDetailBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Details_BookActivity.this,Input_Details_BookActivity.class);
                intent.putExtra("book_id", book_id);
                intent.putExtra("book_name", book_name);
                intent.putExtra("author", author);
                intent.putExtra("book_image", book_image);
                intent.putExtra("category_id", category_id);
                intent.putExtra("category_name", category_name);
                intent.putExtra("quantity", quantity);
                intent.putExtra("language", language);
                intent.putExtra("publishing_year", publishing_year);
                intent.putExtra("nxb", nxb);
                startActivity(intent);
            }
        });
    }
    private void getdataBook() {
        ivDetailImageBook = (ImageView) findViewById(R.id.imageViewDetailImageBook);
        tvDetailBookBookName = (TextView) findViewById(R.id.TextViewDetailBookBookName);
        tvDetailBookAuthor = (TextView) findViewById(R.id.TextViewDetailBookAuthor);

        tvDetailBookQuantity = (TextView) findViewById(R.id.TextViewDetailBookQuantity);
        tvDetailBookCategory = (TextView) findViewById(R.id.TextViewDetailBookCategory);
        tvDetailBookNXB = (TextView) findViewById(R.id.TextViewDetailBookNXB);
        tvDetailBookPublishingYear = (TextView) findViewById(R.id.TextViewDetailBookPublishingYear);
        tvDetailBookLanguge = (TextView) findViewById(R.id.TextViewDetailBookLanguge);

        db = FirebaseDatabase.getInstance().getReference("books");

        db.child(book_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Book book = dataSnapshot.getValue(Book.class);
                book_name = book.book_name;
                book_image = book.book_image;
                author = book.author;
                tvDetailBookBookName.setText("Tên sách: " +book_name);
                tvDetailBookAuthor.setText("Tác giả: "+author);
                Picasso.get().load(book_image).fit().centerInside().placeholder(R.mipmap.ic_launcher).into(ivDetailImageBook);

                BookDetail bookDetail = dataSnapshot.getValue(BookDetail.class);
                nxb = (bookDetail.nxb == null) ? "Chưa biết" : bookDetail.nxb;
                publishing_year = (bookDetail.publishing_year == null) ? "--/--/----" : bookDetail.publishing_year;
                language = (bookDetail.language == null) ? "Chưa biết" : bookDetail.language;
                quantity = (bookDetail.quantity == null) ? "1" : bookDetail.quantity;
                category_id = (bookDetail.category_id == null) ? "-1" : bookDetail.category_id;

                tvDetailBookNXB.setText("NXB: " + nxb);
                tvDetailBookPublishingYear.setText("Năm XB: " + publishing_year);
                tvDetailBookLanguge.setText("Ngôn ngữ: " + language);
                tvDetailBookQuantity.setText("Số lượng: " + quantity);

                if (category_id == "-1"){
                    tvDetailBookCategory.setText("Thể loại: " + "Chưa biết");
                }else {
                    db = FirebaseDatabase.getInstance().getReference("categories");
                    db.child(category_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Category category = dataSnapshot.getValue(Category.class);
                            category_name = category.category_name;
                            tvDetailBookCategory.setText("Thể loại: " + category_name);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void GestureActivity(View view) {
        final GestureDetector gestureDetector = new GestureDetector(this,new Details_BookActivity.BackGesture());
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
