package com.minhnhut.library;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minhnhut.library.DataObj.Book;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.minhnhut.library.DataObj.fBuild.StringUtils.removeAccent;
import static com.minhnhut.library.DataObj.fBuild.deleteImage;

public class Update_BookActivity extends AppCompatActivity {
    String book_id;
    String urlImageDelete;
    DatabaseReference db;
    TextView tvUpdateBookName;
    TextView tvUpdateBookName_er;
    TextView tvUpdateAuthor;
    TextView tvUpdateAuthor_er;
    ImageView ivUpdateBookImage;

    final int CAMERA = 11;
    final int LIBRARY_PICTURE = 22;
    Bitmap bitmap;

    Button btSubmitUpdateBook;
//    Button btCannelUpdateBook;
    private StorageReference storageReference;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sửa sách");
        GestureActivity(findViewById(R.id.activityUpdateBook));

        tvUpdateBookName = (TextView) findViewById(R.id.TextViewUpdateBookName);
        tvUpdateBookName_er = (TextView) findViewById(R.id.TextViewUpdateBookName_er);
        tvUpdateAuthor = (TextView) findViewById(R.id.TextViewUpdateAuthor);
        tvUpdateAuthor_er = (TextView) findViewById(R.id.TextViewUpdateAuthor_er);

        ivUpdateBookImage = (ImageView) findViewById(R.id.ImageViewUpdateBookImage);
        btSubmitUpdateBook = (Button) findViewById(R.id.buttonSubmitUpdateBook);
//        btCannelUpdateBook = (Button) findViewById(R.id.buttonCannelUpdateBook);/

        getdataIntent();

        ivUpdateBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseImage();
            }
        });

        btSubmitUpdateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
//        btCannelUpdateBook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

    }

    private  void submit(){
        String bookName = tvUpdateBookName.getText().toString();
        String author = tvUpdateAuthor.getText().toString();
        if (bookName.length() < 4 && author.length() < 4){
            tvUpdateBookName_er.setText("Tên sách phải trên 3 kí tự");
            tvUpdateAuthor_er.setText("Tên tác giả phải trên 3 kí tự");
        }else if (bookName.length() < 4){
            tvUpdateBookName_er.setText("Tên sách phải trên 3 kí tự");
            tvUpdateAuthor_er.setText("");
        }else if (author.length() < 4){
            tvUpdateBookName_er.setText("");
            tvUpdateAuthor_er.setText("Tên tác giả phải trên 3 kí tự");
        }else
        {
            progressDialog = new ProgressDialog(Update_BookActivity.this);
            progressDialog.setTitle("Đang thay đổi thông tin sách...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Calendar calendar = Calendar.getInstance();
            bookName = removeAccent(bookName).toLowerCase();
            String bookNameSave = bookName.replace(" ", "_") + calendar.getTimeInMillis() + ".png";

            storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference reference = storageReference.child(bookNameSave);

            ivUpdateBookImage.setDrawingCacheEnabled(true);
            ivUpdateBookImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) ivUpdateBookImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = reference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(Update_BookActivity.this, "không thành công", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    deleteImage(urlImageDelete);
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference dbBook = database.getReference("books");

                            Map<String, String> book = new HashMap<>();
                            book.put("book_name", tvUpdateBookName.getText().toString());
                            book.put("author", tvUpdateAuthor.getText().toString());
                            book.put("book_image", uri.toString());
                            dbBook.child(book_id).setValue(book, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null){
                                        Toast.makeText(Update_BookActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Update_BookActivity.this, BookActivity.class));
                                        progressDialog.dismiss();
                                    }else { Toast.makeText(Update_BookActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show(); }
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
    private void getdataIntent() {
        Intent intent    = getIntent();
        book_id   = intent.getStringExtra("book_id");

        db = FirebaseDatabase.getInstance().getReference("books");
        db.child(book_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Book book = dataSnapshot.getValue(Book.class);
                tvUpdateBookName.setText(book.book_name);
                tvUpdateAuthor.setText(book.author);
                urlImageDelete = book.book_image;
                Picasso.get().load(book.book_image).fit().centerInside().placeholder(R.drawable.noavatarbook).into(ivUpdateBookImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void dialogChooseImage(){
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(),ivUpdateBookImage);
        popupMenu.getMenuInflater().inflate(R.menu.camera_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuCamera:
                        ActivityCompat.requestPermissions(Update_BookActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA);
                        break;
                    case R.id.menuLibraryPicture:
                        ActivityCompat.requestPermissions(
                                Update_BookActivity.this,
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
                ivUpdateBookImage.setImageBitmap(bitmap);
            }else {
                Uri filePath = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(filePath);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    ivUpdateBookImage.setImageBitmap(bitmap);

                }
                catch (FileNotFoundException e) {e.printStackTrace();}
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            Intent intent = new Intent(Update_BookActivity.this, BookActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void GestureActivity(View view) {
        final GestureDetector gestureDetector = new GestureDetector(this,new Update_BookActivity.BackGesture());
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
                Intent intent = new Intent(Update_BookActivity.this, BookActivity.class);
                startActivity(intent);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

}
