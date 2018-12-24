//package com.minhnhut.library;
//
//import android.content.Intent;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//
//public class BorrowActivity extends AppCompatActivity {
//
//    FloatingActionButton fabCreateBorrow;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_borrow);
//
//        click_fabCreateBorrow();
//    }
//
//    private void click_fabCreateBorrow() {
//        fabCreateBorrow = (FloatingActionButton) findViewById(R.id.fabCreateBorrow);
//        fabCreateBorrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(BorrowActivity.this, CreateBorrowActivity.class));
//            }
//        });
//    }
//}
