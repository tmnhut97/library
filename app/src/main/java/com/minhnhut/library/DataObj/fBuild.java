package com.minhnhut.library.DataObj;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minhnhut.library.R;

import java.io.ByteArrayOutputStream;
import java.text.Normalizer;
import java.util.Calendar;
import java.util.regex.Pattern;
public class fBuild {

    public static String email_superadmin = "superadmin@gmail.com";
    public static String password_superadmin = "123456";

    public static int Admin = 1;
    public static int User = 0;

    private int mode = 0 ;

    public static DatabaseReference db;
    public static StorageReference storageReference;


    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference db_Users = database.getReference("users");
    public static DatabaseReference db_Books = database.getReference("books");
    public static DatabaseReference db_image = database.getReference("image");
    public static DatabaseReference db_category = database.getReference("categories");

    public static int REQUEST_CODE_CAMERA = 1111;
    public static int REQUEST_CODE_LIBRARY_PICTURE = 2222;

    public static class StringUtils {

        public static String removeAccent(String s) {

            String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("");
        }
    }

    public static void showSnackbarnull(View view, String message, int duration)
    {
        // Create snackbar
        final Snackbar snackbar = Snackbar.make(view, message, duration);

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.GRAY);
        textView.setAllCaps(true);

        snackbar.show();
    }
    public static void showSnackbar(View view, String message)
    {
        // Create snackbar
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        // Set an action on it, and a handler
        snackbar.setAction("Đóng", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        snackbar.setActionTextColor(Color.WHITE);

// styling for rest of text
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.GRAY);
        textView.setAllCaps(true);

// styling for background of snackbar
//        View sbView = snackbarView;
//        sbView.setBackgroundColor(Color.BLUE);

        snackbar.show();
    }

    public static void ChooseDate(final TextView textView, Context context){
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                textView.setText(dayOfMonth + "-" + month + "-" + year);
            }
        },year,month,day);
        datePickerDialog.show();
    }

    public static void deleteImage(String image){
        if (image != null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image);
            storageReference.delete();
        }
    }

    //Animation //
    public static void anim_OutToBot(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
        animate.setDuration(1000);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void anim_hideToRight(View view){
        TranslateAnimation animate = new TranslateAnimation(0,view.getWidth()+30,0,0);
        animate.setDuration(400);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }
    public static void anim_showToRight(View view){
        TranslateAnimation animate = new TranslateAnimation(-view.getWidth()-30,0,0,0);
        animate.setDuration(400);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }



    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (NullPointerException e) {
            e.getMessage();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    public static int getParams(){
        int layout_parms;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layout_parms = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layout_parms = WindowManager.LayoutParams.TYPE_TOAST;
        }
        return layout_parms;
    }


    private void Movefab(View view) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(61,61);
        layoutParams.leftMargin =50;
        layoutParams.topMargin =50;
        view.setOnTouchListener(new View.OnTouchListener() {
            RelativeLayout.LayoutParams params;
            float dx =0, dy = 0, x =0, y=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FloatingActionButton floatingActionButton = (FloatingActionButton) v;
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        params = (RelativeLayout.LayoutParams) floatingActionButton.getLayoutParams();
                        dx = event.getRawX() - params.leftMargin;
                        dy = event.getRawY() - params.topMargin;
                        mode = 1;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == 1){
                            x = event.getRawX();
                            y =event.getRawY();

                            params.leftMargin = (int) (x-dx);
                            params.topMargin = (int) (y-dy);

                            params.rightMargin = 0;
                            params.bottomMargin = 0 ;
                            params.rightMargin = params.leftMargin + ( 5*params.width);
                            params.bottomMargin = params.topMargin + ( 5*params.height);
                            floatingActionButton.setLayoutParams(params);
                        }

                }
                return true;
            }
        });
    }

    public static void dialogChooseImage(Context context, View view, final Activity activity){
        PopupMenu popupMenu = new PopupMenu(context,view);
        popupMenu.getMenuInflater().inflate(R.menu.camera_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuCamera:
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                        break;
                    case R.id.menuLibraryPicture:
                        ActivityCompat.requestPermissions(
                                activity,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_CODE_LIBRARY_PICTURE
                        );
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

//    public static void defUser(final Context context) {
//
//        db_Users.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User us = dataSnapshot.getValue(User.class);
//                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//                mAuth.signInWithEmailAndPassword(us.email, us.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(!task.isSuccessful()){
//                            Toast.makeText(context, "Kiểm tra internet của bạn", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//    }
}
