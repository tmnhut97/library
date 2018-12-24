package com.minhnhut.library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minhnhut.library.DataObj.User;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.minhnhut.library.DataObj.fBuild.REQUEST_CODE_CAMERA;
import static com.minhnhut.library.DataObj.fBuild.REQUEST_CODE_LIBRARY_PICTURE;
import static com.minhnhut.library.DataObj.fBuild.StringUtils.removeAccent;
import static com.minhnhut.library.DataObj.fBuild.dialogChooseImage;

public class CreateUserActivity extends AppCompatActivity implements Validator.ValidationListener {
    Validator validator;
    StorageReference storageReference;
    @NotEmpty(message = "Tên người dùng không được rỗng")
    @Length(max = 30, min = 3, message = "Tên người dùng phải từ 3 đến 30 kí tự")
    private EditText etUserName;

    @NotEmpty(message = "Email không được rỗng")
    @Email(message = "Email chưa đúng định dạng")
    private EditText etEmail;

    @NotEmpty(message = "Mật khẩu không được rỗng")
    @Password(min = 6, scheme = Password.Scheme.ANY, message = "Mật khẩu phải trên 6 kí tự")
    @Pattern(regex = "[A-Za-z0-9]+", message = "Mật khẩu không được chứa kí tự đặt biệt")
    private EditText etPassWord;

    @ConfirmPassword(message = "Mật khẩu chưa trùng khớp")
    private EditText etPassword_re;


    private Spinner snLevel;
    CircleImageView civCreateAvatarUser;

    int level;

    Bitmap bitmap;
    ProgressDialog progressDialog;
    String uri_avatar ;
    Button btSubmitCreateBook, btSelectAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Tạo người dùng");
        GestureActivity(findViewById(R.id.activityCreateBook));

        etUserName    = (EditText) findViewById(R.id.textViewCreateUserUserName);
        etEmail       = (EditText) findViewById(R.id.textViewCreateUserEmail);
        etPassWord    = (EditText) findViewById(R.id.textViewCreateUserPassWord);
        etPassword_re = (EditText) findViewById(R.id.textViewCreateUserPassWord_re);
//        etCode = (EditText) findViewById(R.id.textViewCreateUserCode);

        civCreateAvatarUser = (CircleImageView) findViewById(R.id.CircleImageViewCreateAvatarUser);
        snLevel = (Spinner) findViewById(R.id.spinnerCreateUserLevel);
        btSelectAvatar = (Button) findViewById(R.id.buttonSelectAvatar);
        btSubmitCreateBook = (Button) findViewById(R.id.buttonSubmitCreateBook);

        menuLevel();
        addAvatar();
        submit();

    }

    //////AVATAR///////////
    private void addAvatar() {
        btSelectAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseImage(getApplicationContext(), btSelectAvatar, CreateUserActivity.this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ( grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (requestCode == REQUEST_CODE_LIBRARY_PICTURE){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện cho sách"), REQUEST_CODE_LIBRARY_PICTURE);
            }else {
                Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }

        }else {
            if (requestCode == REQUEST_CODE_LIBRARY_PICTURE){
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
            if (requestCode == REQUEST_CODE_CAMERA){
                bitmap = (Bitmap) data.getExtras().get("data");
                civCreateAvatarUser.setImageBitmap(bitmap);
            }else {
                Uri filePath = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(filePath);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    civCreateAvatarUser.setImageBitmap(bitmap);
                }
                catch (FileNotFoundException e) {e.printStackTrace();}
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void submit() {
        validator = new Validator(this);
        validator.setValidationListener(this);
        btSubmitCreateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });
    }

    private void menuLevel() {
        final ArrayList<String> arrayListLevel = new ArrayList<>();
        arrayListLevel.add("User");
        arrayListLevel.add("Editor");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayListLevel);
        snLevel.setAdapter(arrayAdapter);
        snLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String vl = arrayListLevel.get(position);
                if (vl.contains("Editor"))
                {
                    vl = "1";
                }else {
                    vl = "0";
                }
                level = Integer.parseInt(vl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { level = 0;}
        });
    }

    @Override
    public void onValidationSucceeded() {
        final String username = etUserName.getText().toString();
        final String email    = etEmail.getText().toString();
        final String password = etPassWord.getText().toString();
//        final int code = Integer.valueOf(etCode.getText().toString());
        if ( !username.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            final FirebaseAuth mAuth  = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateUserActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if(bitmap == null){
                            uri_avatar = null;
                            final FirebaseUser user = mAuth.getCurrentUser();
                            String Uid = user.getUid();

                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
                            User Us = new User(null,username, email, password, uri_avatar,level);
                            db.child(Uid).setValue(Us, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null){
                                        Toast.makeText(CreateUserActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CreateUserActivity.this, UserActivity.class);
                                        startActivity(intent);
                                    }else { Toast.makeText(CreateUserActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show(); }
                                }
                            });
                        }
                        else
                        {
                            progressDialog = new ProgressDialog(CreateUserActivity.this);
                            progressDialog.setTitle("Vui lòng đợi...");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            Calendar calendar = Calendar.getInstance();
                            String nameavatar = removeAccent(username).toLowerCase();
                            nameavatar = "avatar_" + nameavatar.replace(" ", "_") + calendar.getTimeInMillis() + ".png";

                            storageReference = FirebaseStorage.getInstance().getReference();
                            final StorageReference reference = storageReference.child(nameavatar);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = reference.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(CreateUserActivity.this, "không thành công", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            uri_avatar = uri.toString();
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            String Uid = user.getUid();
                                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
                                            User Us = new User(null,username, email, password, uri_avatar,level);
                                            db.child(Uid).setValue(Us, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    if (databaseError == null){
                                                        Toast.makeText(CreateUserActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(CreateUserActivity.this, UserActivity.class);
                                                        startActivity(intent);
                                                    }else { Toast.makeText(CreateUserActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show(); }
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

                    } else {
                        String ms = task.getException().getMessage().contains("email address is already") ? "Email này đã được sử dụng" : task.getException().getMessage().contains("network error") ? "Kiểm tra internet của bạn" : "Có lỗi xảy ra";
                        Toast.makeText(CreateUserActivity.this, ms, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(CreateUserActivity.this, UserActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void GestureActivity(View view) {
        final GestureDetector gestureDetector = new GestureDetector(this,new CreateUserActivity.BackGesture());
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
                Intent intent = new Intent(CreateUserActivity.this, UserActivity.class);
                startActivity(intent);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
