package com.minhnhut.library;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.minhnhut.library.DataObj.fBuild.db_Users;

public class UpdateUserActivity extends AppCompatActivity {
    TextView tvUserName;
    TextView tvEmail;
    TextView tvWaningChange;

    Spinner snLevel;
    CircleImageView civUpdateAvatarUser;

    int level;
    String user_id;
    String username;
    String email;
    String avatar;
    int user_level;


    Bitmap bitmap;
    String uri_avatar ;
    Button btSubmitUpdateBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Cập nhật người dùng");
        data();
        menuLevel();
        submit();

    }

    private void submit() {
        btSubmitUpdateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db_Users.child(user_id).child("level").setValue(level);
                startActivity(new Intent(UpdateUserActivity.this, UserActivity.class));
            }
        });

    }

    private void menuLevel() {
        final ArrayList<String> arrayListLevel = new ArrayList<>();
        if (user_level == 0){
            arrayListLevel.add("User");
            arrayListLevel.add("Editor");
        }
        else {
            arrayListLevel.add("Editor");
            arrayListLevel.add("User");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayListLevel);
        snLevel.setAdapter(arrayAdapter);
        snLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String vl = arrayListLevel.get(position);
                if (vl.contains("Editor"))
                {
                    vl = "1";
                    tvWaningChange.setText("Người dùng này sẽ được phép chỉnh sửa bao gồm danh mục và sách");
                }else {
                    vl = "0";
                    tvWaningChange.setText("Người dùng này chỉ được phép xem");
                }
                level = Integer.parseInt(vl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { level = 0;}
        });
    }

    private void data() {

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        avatar = intent.getStringExtra("avatar");
        user_level = intent.getIntExtra("level", 0);


        tvUserName    = (TextView) findViewById(R.id.textViewUpdateUserUserName);
        tvEmail       = (TextView) findViewById(R.id.textViewUpdateUserEmail);
        tvWaningChange    = (TextView) findViewById(R.id.textViewWaningChange);

        civUpdateAvatarUser = (CircleImageView) findViewById(R.id.CircleImageViewUpdateAvatarUser);
        snLevel = (Spinner) findViewById(R.id.spinnerUpdateUserLevel);
        btSubmitUpdateBook = (Button) findViewById(R.id.buttonSubmitUpdateBook);

        tvUserName.setText(username);
        tvEmail.setText(email);
        Picasso.get().load(avatar).fit().placeholder(R.drawable.avateruser).into(civUpdateAvatarUser);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void GestureActivity(View view) {
        final GestureDetector gestureDetector = new GestureDetector(this,new UpdateUserActivity.BackGesture());
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
