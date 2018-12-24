package com.minhnhut.library;

import android.app.Dialog;
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
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minhnhut.library.Adapter.BookAdapter;
import com.minhnhut.library.Adapter.PagerAdapter;
import com.minhnhut.library.DataObj.Book;
import com.minhnhut.library.DataObj.Category;
import com.minhnhut.library.DataObj.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.minhnhut.library.DataObj.fBuild.REQUEST_CODE_CAMERA;
import static com.minhnhut.library.DataObj.fBuild.REQUEST_CODE_LIBRARY_PICTURE;
import static com.minhnhut.library.DataObj.fBuild.StringUtils.removeAccent;
import static com.minhnhut.library.DataObj.fBuild.db_Users;
import static com.minhnhut.library.DataObj.fBuild.deleteImage;
import static com.minhnhut.library.DataObj.fBuild.dialogChooseImage;

public class HomeActivity extends AppCompatActivity {
    String uid;
    String urlAvatarDelete;

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;


    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

    ViewPager viewPager;
    TabLayout tabLayout;
    NavigationView navigationView;

    ArrayList<Book> arrayList;
    BookAdapter bookAdapter;
    RecyclerView recyclerView;

    // hearder nav
    View nav_hearder;
    CircleImageView NavAvatarUser;
    TextView  tvNav_UserName;
    TextView  tvNav_Email;
    ImageView ivEditAvatar;
    ImageView ivEditUserName;
    Bitmap bitmap;
    ProgressDialog progressDialog;
    StorageReference storageReference;

    int level;
    ArrayList<Category> arrayListCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationView = findViewById(R.id.NavigationView);

        user();
        initView();
        initNav();
        selectItemNav();
        navigationView.getMenu().hasVisibleItems();

    }

    private void user() {

        nav_hearder = (View) navigationView.getHeaderView(0);
        NavAvatarUser = nav_hearder.findViewById(R.id.CircleImageViewCreateAvatarUser);
        tvNav_UserName  = nav_hearder.findViewById(R.id.textViewNav_UserName);
        tvNav_Email  = nav_hearder.findViewById(R.id.textViewNav_Email);
        ivEditUserName = nav_hearder.findViewById(R.id.imageViewEditUserName);
        editUserName();
        ivEditAvatar  = nav_hearder.findViewById(R.id.imageViewEditAvatar);
        EditAvatar();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            uid = user.getUid();
            db_Users.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User us = dataSnapshot.getValue(User.class);
                    tvNav_UserName.setText(us.username);
                    tvNav_Email.setText(us.email);
                    urlAvatarDelete = us.avatar;
                    Picasso.get().load(us.avatar).fit().placeholder(R.drawable.avateruser).into(NavAvatarUser);
                    level = us.level;
                    UserPermission();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
    }

    private void editUserName() {
        ivEditUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_username);
                dialog.setCanceledOnTouchOutside(true);

                final EditText etUserName = (EditText) dialog.findViewById(R.id.EditTextEditUserName) ;
                Button btSubmit = (Button) dialog.findViewById(R.id.buttonSubmitEditUserName) ;
                etUserName.setText(tvNav_UserName.getText());
                btSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db_Users.child(uid).child("username").setValue(etUserName.getText().toString());
                    }
                });

                dialog.show();
            }
        });

    }

    private void UserPermission() {

        switch (level){
            case -1:
                break;
            case 1:
                navigationView.getMenu().removeItem(R.id.Users);
                break;
            default:
                navigationView.getMenu().removeItem(R.id.Categories);
                navigationView.getMenu().removeItem(R.id.Users);
                navigationView.getMenu().removeItem(R.id.Books);
                break;
        }
    }

    private void EditAvatar() {
        ivEditAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseImage(getApplicationContext(), ivEditAvatar, HomeActivity.this);
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
                dialogUpdateAvatar();
            }else {
                Uri filePath = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(filePath);
                    bitmap = BitmapFactory.decodeStream(inputStream);
//                    NavAvatarUser.setImageBitmap(bitmap);
                    dialogUpdateAvatar();
                }
                catch (FileNotFoundException e) {e.printStackTrace();}
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void dialogUpdateAvatar() {
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review_avatar);

        ImageView ivAvatar = (ImageView) dialog.findViewById(R.id.CircleImageViewCreateAvatarUser);
        Button btSubmit = (Button) dialog.findViewById(R.id.ButtonSubmitUpdateAvatarUser);
        Button btBack = (Button) dialog.findViewById(R.id.ButtonBackUpdateAvatarUser);
        Button btReselect = (Button) dialog.findViewById(R.id.buttonReSelectAvatar);

        ivAvatar.setImageBitmap(bitmap);
        btReselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện cho sách"), REQUEST_CODE_LIBRARY_PICTURE);
            }
        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setTitle("Đang thay đổi ảnh đại diện...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Calendar calendar = Calendar.getInstance();
                String nameavatar = removeAccent(tvNav_UserName.getText().toString()).toLowerCase();
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
                        Toast.makeText(HomeActivity.this, "không thành công", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        deleteImage(urlAvatarDelete);
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final String uid = user.getUid();
                                db_Users.child(uid).child("avatar").setValue(uri.toString(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError == null){
//                                            NavAvatarUser.setImageBitmap(bitmap);
                                            Toast.makeText(HomeActivity.this, "Upload thành công", Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                            progressDialog.dismiss();
                                        }else { Toast.makeText(HomeActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show(); }
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
        });

        dialog.show();
    }
    private void selectItemNav() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.Categories:
                        startActivity(new Intent(HomeActivity.this, CategoryActivity.class));
                        break;
                    case R.id.Logout:
                        dialogLogout();
                        break;
                    case R.id.Books:
                        startActivity(new Intent(HomeActivity.this, BookActivity.class));
                        break;
                    case R.id.Users:
                        startActivity(new Intent(HomeActivity.this, UserActivity.class));
                        break;
                    case R.id.About:
                        about_us();
                        break;
//                    case R.id.Borrow:
//                        startActivity(new Intent(HomeActivity.this, BorrowActivity.class));
//                        break;
                    default:
                        Toast.makeText(HomeActivity.this, "abc", Toast.LENGTH_SHORT).show();
                        break;
                }


                drawerLayout.closeDrawers();
                return false;
            }
        });
    }

    private void about_us() {
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void dialogLogout() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCanceledOnTouchOutside(false);

        Button btOk = (Button) dialog.findViewById(R.id.buttonOk);
        Button btCannel = (Button) dialog.findViewById(R.id.buttonCannel);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });
        btCannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    private void initNav() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)){ return true; }
        return super.onOptionsItemSelected(item);
    }

    private void initView(){
        viewPager = (ViewPager) findViewById(R.id.ViewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        tabLayout.setupWithViewPager(viewPager, true);
        setupTabIcons();
    }
    private void setupTabIcons() {
        int[] tabIcons = {R.drawable.ic_book ,R.drawable.ic_categories_24dp };
        for(int i = 0; i < 2; i++){
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }



}
