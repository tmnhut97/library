package com.minhnhut.library;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minhnhut.library.Adapter.ImagesDetailBookAdapter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

import static com.minhnhut.library.DataObj.fBuild.StringUtils.removeAccent;
import static com.minhnhut.library.DataObj.fBuild.db_image;
import static com.minhnhut.library.DataObj.fBuild.storageReference;

public class Images_Detail_BookActivity extends AppCompatActivity {

    String book_id;
    String book_name;

    Button btUpdateImageDetailBook;
    Button btReselectImageDetailBook;


    int erorr = 0;
    ImageView ivt;
    int CAMERA = 111;
    int LIBRARY_PICTURE = 222;

    ArrayList<Bitmap> arrayListImageBitmap = new ArrayList<Bitmap>();
    ImagesDetailBookAdapter imagesDetailBookAdapter;
    RecyclerView recyclerView;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images__detail__book);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Thêm ảnh chi tiết");
        GestureActivity(findViewById(R.id.ImageDetailBookActivity));


        btUpdateImageDetailBook = (Button) findViewById(R.id.ButtonUpdateImageDetailBook);
        btReselectImageDetailBook = (Button) findViewById(R.id.ButonReselectImageDetailBook);


        getDataIntent();
        Click_btReselectImageDetailBook();
        Click_btUpdateImageDetailBook();
    }

    private void Click_btUpdateImageDetailBook() {
        btUpdateImageDetailBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayListImageBitmap.size() <= 0 ){
                    Toast.makeText(Images_Detail_BookActivity.this, "Bạn chưa chọn hình", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (int i= 0; i < arrayListImageBitmap.size(); i++){
                        progressDialog = new ProgressDialog(Images_Detail_BookActivity.this);
                        progressDialog.setTitle("Đang cập nhật "+ i + "/" + arrayListImageBitmap.size() + " hình ảnh");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        Calendar calendar = Calendar.getInstance();
                        String bookNameSave = removeAccent(book_name).replace(" ", "_") + "_detail_" + i + calendar.getTimeInMillis() + ".png";

                        storageReference = FirebaseStorage.getInstance().getReference();
                        final StorageReference reference = storageReference.child(bookNameSave);
                        Bitmap bitmap = arrayListImageBitmap.get(i);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();
                        UploadTask uploadTask = reference.putBytes(data);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(Images_Detail_BookActivity.this, "không thành công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference dbBook = database.getReference("image");
                                        Map<String, String> book = new HashMap<>();
                                        book.put("book_id", book_id);
                                        book.put("url", uri.toString());
                                        db_image.push().setValue(book, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                if (databaseError == null){
                                                    erorr = 0;
                                                }else { erorr++;}
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
                    if ( erorr > 0){Toast.makeText(Images_Detail_BookActivity.this, "Có lỗi xảy ra !!", Toast.LENGTH_SHORT).show(); }
                    else {
                        finish();
                        progressDialog.dismiss();
                        Toast.makeText(Images_Detail_BookActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void Click_btReselectImageDetailBook() {
        btReselectImageDetailBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayListImageBitmap.clear();
                dialogChooseImageStyle();
            }
        });
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        book_id = intent.getStringExtra("book_id");
        book_name = intent.getStringExtra("book_name");

        dialogChooseImageStyle();

    }

    private void dialogChooseImageStyle() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_style_post_image);
        dialog.setCanceledOnTouchOutside(false);

        TextView Cannel = (TextView) dialog.findViewById(R.id.TextViewCannel);
        TextView tvCAMERA = (TextView) dialog.findViewById(R.id.TextViewCAMERA);
        TextView tvLIBRARY = (TextView) dialog.findViewById(R.id.TextViewLIBRARY);

        tvCAMERA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                ActivityCompat.requestPermissions(Images_Detail_BookActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA);
            }
        });
        tvLIBRARY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                ActivityCompat.requestPermissions(
                        Images_Detail_BookActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        LIBRARY_PICTURE
                );
            }
        });
        Cannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });
        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ( grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (requestCode == LIBRARY_PICTURE){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), LIBRARY_PICTURE);
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
        if ( resultCode == RESULT_OK && data != null ){
            if (requestCode == CAMERA){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
              arrayListImageBitmap.add(bitmap);
              initRecyclerView();
            }else {
                // pick one image
                if(data.getData() != null){
                    Uri filePath = data.getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(filePath);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        arrayListImageBitmap.add(bitmap);
                        initRecyclerView();
                    }
                    catch (FileNotFoundException e) {e.printStackTrace();}
                }else if (data.getClipData() != null){

                    ClipData mClipData = data.getClipData();
                    int countItem = mClipData.getItemCount();
                    if (countItem > 10)
                    {
                        Toast.makeText(this, "10 hình thôi bạn !!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Select Picture"), LIBRARY_PICTURE);
                    }
                    else
                    {
                        for (int i = 0; i < countItem;  i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(uri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                arrayListImageBitmap.add(bitmap);
                                initRecyclerView();
                            }
                            catch (FileNotFoundException e) {e.printStackTrace();}
                        }
                    }

                }else {
                    Toast.makeText(this, "Bạn chưa chọn ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
//////////////

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerViewImageDetailBook);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new SlideInDownAnimator());
        imagesDetailBookAdapter = new ImagesDetailBookAdapter(getApplicationContext(), arrayListImageBitmap);
        recyclerView.setAdapter(imagesDetailBookAdapter);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void GestureActivity(View view) {
        final GestureDetector gestureDetector = new GestureDetector(this,new Images_Detail_BookActivity.BackGesture());
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
