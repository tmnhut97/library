package com.minhnhut.library;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.minhnhut.library.Adapter.ShowImageViewDetailBookAdapter;
import com.minhnhut.library.DataObj.Book;
import com.minhnhut.library.DataObj.BookDetail;
import com.minhnhut.library.DataObj.Category;
import com.minhnhut.library.DataObj.Image;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static com.minhnhut.library.DataObj.fBuild.anim_OutToBot;
import static com.minhnhut.library.DataObj.fBuild.anim_hideToRight;
import static com.minhnhut.library.DataObj.fBuild.anim_showToRight;
import static com.minhnhut.library.DataObj.fBuild.db_Books;

public class ViewDetailBookActivity extends AppCompatActivity {
    int def = 0;
    String book_id;
    ImageView ivDetailImageBook;
    TextView tvDetailBookBookName, tvDetailBookAuthor;
    TextView tvDetailBookQuantity, tvDetailBookCategory, tvDetailBookNXB, tvDetailBookPublishingYear, tvDetailBookLanguge;

    RecyclerView recyclerView;
    ArrayList<Image> arrayListImage;
    ShowImageViewDetailBookAdapter showImageViewDetailBookAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail_book);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Chi tiết sách");

        GestureActivity(findViewById(R.id.activityViewDetailBook));

        Intent intent    = getIntent();
        book_id          = intent.getStringExtra("book_id");

        StyleViewDetailBook();
        getdataBook();

        initRecyclerView();
        getImagesDetailBook();
    }


    private void getImagesDetailBook() {
        Query filter = FirebaseDatabase.getInstance().getReference("image").orderByChild("book_id").equalTo(book_id);
        filter.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    recyclerView.setVisibility(View.VISIBLE);
                    Image image = dataSnapshot.getValue(Image.class);
                    arrayListImage.add(new Image(image.book_id, image.url));
                    showImageViewDetailBookAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerViewViewImageDetailBook);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        arrayListImage = new ArrayList<Image>();
        showImageViewDetailBookAdapter = new ShowImageViewDetailBookAdapter(getApplicationContext(), arrayListImage);
        recyclerView.setAdapter(showImageViewDetailBookAdapter);

    }

    private void StyleViewDetailBook() {

        final ImageButton ivStyleViewDetailBook = (ImageButton) findViewById(R.id.imageButtonViewBookStyleViewBook);
        ivStyleViewDetailBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                def = (def != 1) ? 1 : 0;
                switch (def){
                    case 0:
                        ivStyleViewDetailBook.setImageResource(R.drawable.ic_photo_library);
                        anim_hideToRight(findViewById(R.id.ViewDetailBookTextPicture));
                        anim_showToRight(findViewById(R.id.ViewDetailBookTextList));
                        break;
                    case 1:
                        ivStyleViewDetailBook.setImageResource(R.drawable.ic_list_text);
                        anim_hideToRight(findViewById(R.id.ViewDetailBookTextList));
                        anim_showToRight(findViewById(R.id.ViewDetailBookTextPicture));
                        break;
                }
            }
        });
    }

    private void getdataBook() {
        ivDetailImageBook = (ImageView) findViewById(R.id.imageViewImageBook);
        tvDetailBookBookName = (TextView) findViewById(R.id.textViewViewBookName);
        tvDetailBookAuthor = (TextView) findViewById(R.id.textViewViewAuthor);

        tvDetailBookQuantity = (TextView) findViewById(R.id.textViewViewDetailBookQuantity);
        tvDetailBookCategory = (TextView) findViewById(R.id.textViewViewDetailBookCategory);
        tvDetailBookNXB = (TextView) findViewById(R.id.textViewViewDetailBookNXB);
        tvDetailBookPublishingYear = (TextView) findViewById(R.id.textViewViewDetailBookPublishingYear);
        tvDetailBookLanguge = (TextView) findViewById(R.id.textViewViewDetailBookLanguge);


        db_Books.child(book_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Book book = dataSnapshot.getValue(Book.class);
                tvDetailBookBookName.setText(book.book_name);
                tvDetailBookAuthor.setText("Tác giả: " + book.author);
                Picasso.get().load(book.book_image).fit().centerInside().placeholder(R.mipmap.ic_launcher).into(ivDetailImageBook);

                BookDetail bookDetail = dataSnapshot.getValue(BookDetail.class);
                String nxb = (bookDetail.nxb == null) ? "Chưa biết" : bookDetail.nxb;
                String publishing_year = (bookDetail.publishing_year == null) ? "dd/mm/yyyy" : bookDetail.publishing_year;
                String language = (bookDetail.language == null) ? "Chưa biết" : bookDetail.language;
                String quantity = (bookDetail.quantity == null) ? "1" : bookDetail.quantity;
                String category_id = (bookDetail.category_id == null) ? "-1" : bookDetail.category_id;

                tvDetailBookNXB.setText("NXB: " + nxb);
                tvDetailBookPublishingYear.setText("Năm XB: " + publishing_year);
                tvDetailBookLanguge.setText("Ngôn ngữ: " + language);
                tvDetailBookQuantity.setText("Số lượng: " + quantity);

                if (category_id == "-1"){
                    tvDetailBookCategory.setText("Thể loại: " + "Chưa biết");
                }else {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("categories");
                    db.child(category_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Category category = dataSnapshot.getValue(Category.class);
                            tvDetailBookCategory.setText("Thể loại: " + category.category_name);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
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
            Intent intent = new Intent(ViewDetailBookActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void GestureActivity(View view) {
        final GestureDetector gestureDetector = new GestureDetector(this,new ViewDetailBookActivity.BackGesture());
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
                Intent intent = new Intent(ViewDetailBookActivity.this, HomeActivity.class);
                anim_OutToBot(findViewById(R.id.activityViewDetailBook));
                startActivity(intent);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
