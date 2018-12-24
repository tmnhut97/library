package com.minhnhut.library;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minhnhut.library.Adapter.BookAdapter;
import com.minhnhut.library.DataObj.Book;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static com.minhnhut.library.DataObj.fBuild.StringUtils.removeAccent;

public class BookActivity extends AppCompatActivity {

    int mode = 0;
    int DRAG =1;

    Book book;
    String book_key;
    ArrayList<Book> arrayList;
    BookAdapter bookAdapter;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    private DatabaseReference db;

    final int CAMERA = 11;
    final int LIBRARY_PICTURE = 22;
    ImageView ivDialogBookImage;
    Bitmap bitmap;

    private StorageReference storageReference;
    ProgressDialog progressDialog;

    GestureDetector gestureDetector;
    int SWIPE_THRESHOLD =200;
    int SWIPE_VELOCITY_THRESHOLD =100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sách");

        initRecyclerView();
        data();
        FloatingActionButton();
        GestureActivity(findViewById(R.id.BookActivity));
    }


    private void data() {
        db = FirebaseDatabase.getInstance().getReference("books");
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    recyclerView.setVisibility(View.VISIBLE);
                    book = dataSnapshot.getValue(Book.class);
                    arrayList.add(new Book(dataSnapshot.getKey(), book.book_name, book.author, book.book_image));
                    bookAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Book book = dataSnapshot.getValue(Book.class);
                Toast.makeText(BookActivity.this, "Cập nhật thành công", Toast.LENGTH_LONG).show();
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


    private void FloatingActionButton(){

        fab = (FloatingActionButton) findViewById(R.id.fabAddBook);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialogCreateBook = new Dialog(BookActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialogCreateBook.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogCreateBook.setContentView(R.layout.dialog_create_book);
                dialogCreateBook.setCanceledOnTouchOutside(false);

                final TextView tvDialogCreateBookName = (TextView) dialogCreateBook.findViewById(R.id.TextViewDialogCreateBookName);
                final TextView tvDialogCreateBookName_er = (TextView) dialogCreateBook.findViewById(R.id.TextViewDialogCreateBookName_er);
                final TextView tvDialogCreateAuthor = (TextView) dialogCreateBook.findViewById(R.id.TextViewDialogCreateAuthor);
                final TextView tvDialogCreateAuthor_er = (TextView) dialogCreateBook.findViewById(R.id.TextViewDialogCreateAuthor_er);
                ivDialogBookImage = (ImageView) dialogCreateBook.findViewById(R.id.ImageViewDialogBookImage);

                Button btSubmitDialogCreateBook = (Button) dialogCreateBook.findViewById(R.id.buttonSubmitDialogCreateBook);
                Button btCannelDialogCreateBook = (Button) dialogCreateBook.findViewById(R.id.buttonCannelDialogCreateBook);

                ivDialogBookImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogChooseImage();
                    }
                });

                btSubmitDialogCreateBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String bookName = tvDialogCreateBookName.getText().toString();
                        String author = tvDialogCreateAuthor.getText().toString();

                        if (bookName.length() < 4 && author.length() < 4){
                            tvDialogCreateBookName_er.setText("Tên sách phải trên 3 kí tự");
                            tvDialogCreateAuthor_er.setText("Tên tác giả phải trên 3 kí tự");
                        }else if (bookName.length() < 4){
                            tvDialogCreateBookName_er.setText("Tên sách phải trên 3 kí tự");
                            tvDialogCreateAuthor_er.setText("");
                        }else if (author.length() < 4){
                            tvDialogCreateBookName_er.setText("");
                            tvDialogCreateAuthor_er.setText("Tên tác giả phải trên 3 kí tự");
                        }else
                        {
                            progressDialog = new ProgressDialog(BookActivity.this);
                            progressDialog.setTitle("Đang thêm sách...");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

//                            Calendar calendar = Calendar.getInstance();
                            bookName = removeAccent(bookName).toLowerCase();
                            String bookNameSave = bookName.replace(" ", "_") + Calendar.getInstance().getTimeInMillis() + ".png";
                            storageReference = FirebaseStorage.getInstance().getReference();
                            final StorageReference reference = storageReference.child(bookNameSave);

                            ivDialogBookImage.setDrawingCacheEnabled(true);
                            ivDialogBookImage.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) ivDialogBookImage.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = reference.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    Toast.makeText(BookActivity.this, "không thành công", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri uri) {
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference dbBook = database.getReference("books");

                                            Map<String, String> book = new HashMap<>();
                                            final String book_name = tvDialogCreateBookName.getText().toString();
                                            book.put("book_name",  book_name.substring(0, 1).toUpperCase() + book_name.substring(1));
                                            book.put("author", tvDialogCreateAuthor.getText().toString());
                                            book.put("book_image", uri.toString());
                                            final String bookId = dbBook.push().getKey();
                                            dbBook.child(bookId).setValue(book, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    if (databaseError == null){
                                                        final Snackbar snackbar = Snackbar.make(findViewById(R.id.BookActivity), "Thêm thành công", Snackbar.LENGTH_LONG);
                                                        snackbar.setAction( "Thêm thông tin ?", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                snackbar.dismiss();
                                                                Intent intent = new Intent(BookActivity.this, Input_Details_BookActivity.class);
                                                                intent.putExtra("book_id",bookId);
                                                                intent.putExtra("book_image", uri.toString());
                                                                intent.putExtra("author", tvDialogCreateAuthor.getText().toString());
                                                                intent.putExtra("book_name",book_name.substring(0, 1).toUpperCase() + book_name.substring(1));
                                                                startActivity(intent);
                                                            }
                                                        });
                                                        snackbar.setActionTextColor(Color.GREEN);
                                                        snackbar.show();
                                                        dialogCreateBook.cancel();
                                                        progressDialog.dismiss();
                                                    }else { Toast.makeText(BookActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show(); }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Vui lòng đợi "+(int)progress+"%");
                                }
                            });
                        }
                    }
                });
                btCannelDialogCreateBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogCreateBook.cancel();
                    }
                });
                dialogCreateBook.show();

            }
        });


    }


    private void dialogChooseImage(){
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(),ivDialogBookImage);
        popupMenu.getMenuInflater().inflate(R.menu.camera_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuCamera:
                        ActivityCompat.requestPermissions(BookActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA);
                        break;
                    case R.id.menuLibraryPicture:
                        ActivityCompat.requestPermissions(
                            BookActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            LIBRARY_PICTURE
                        );
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ( grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (requestCode == LIBRARY_PICTURE){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện cho sách"), LIBRARY_PICTURE);
            }else {
                Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);
            }

        }else {
            if (requestCode == LIBRARY_PICTURE){
                Toast.makeText(this, "Bạn chưa cho phép truy thư viện ảnh", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Bạn chưa cho phép truy camera ", Toast.LENGTH_SHORT).show();
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ( resultCode == RESULT_OK && data != null){
            if (requestCode == CAMERA){
                bitmap = (Bitmap) data.getExtras().get("data");
                ivDialogBookImage.setImageBitmap(bitmap);
            }else {
                Uri filePath = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(filePath);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    ivDialogBookImage.setImageBitmap(bitmap);

                }
                catch (FileNotFoundException e) {e.printStackTrace();}
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerViewBook);
        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        arrayList = new ArrayList<>();
        bookAdapter = new BookAdapter(arrayList, getApplicationContext());
        recyclerView.setAdapter(bookAdapter);
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
                    Query filter = FirebaseDatabase.getInstance().getReference("books").orderByChild("book_name");
                    filter.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Book book = dataSnapshot.getValue(Book.class);
                            if (removeAccent(book.book_name).toLowerCase().contains(removeAccent(search).toLowerCase())) {
                                arrayList.add(new Book(dataSnapshot.getKey(), book.book_name, book.author, book.book_image));
                                bookAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            Intent intent = new Intent(BookActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void GestureActivity(View view) {
        final GestureDetector gestureDetector = new GestureDetector(this,new BookActivity.BackGesture());
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
                Intent intent = new Intent(BookActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
