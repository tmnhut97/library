package com.minhnhut.library;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minhnhut.library.DataObj.User;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

import static com.minhnhut.library.DataObj.fBuild.showSnackbarnull;

public class RegisterActivity extends AppCompatActivity implements Validator.ValidationListener {

    private FirebaseAuth mAuth;
    private DatabaseReference db;
    Validator validator;

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

    Button btRegister;
    TextView tvLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);

        mAuth = FirebaseAuth.getInstance();

        etUserName    = (EditText) findViewById(R.id.editTextUserName);
        etEmail       = (EditText) findViewById(R.id.editTextEmail);
        etPassWord    = (EditText) findViewById(R.id.editTextPassWord);
        etPassword_re = (EditText) findViewById(R.id.editTextPassWord_re);
        btRegister    = (Button) findViewById(R.id.buttonRegister);
        tvLogin       = (TextView) findViewById(R.id.textViewLogin);


        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });
    }


    @Override
    public void onValidationSucceeded() {
        final String username = etUserName.getText().toString();
        final String email    = etEmail.getText().toString();
        final String password = etPassWord.getText().toString();
        if ( !username.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();
                                String Uid = user.getUid();
                                db = FirebaseDatabase.getInstance().getReference("users");
                                User Us = new User(null,username, email, password, null,0);
                                db.child(Uid).setValue(Us, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError == null){
                                            Toast.makeText(RegisterActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                            finish();
                                            showSnackbarnull(findViewById(R.id.activiyRegister), "Thêm thành công", Snackbar.LENGTH_SHORT);
                                        }else { Toast.makeText(RegisterActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show(); }
                                    }
                                });
                            } else {
                                String ms = task.getException().getMessage().contains("email address is already") ? "Email này đã được sử dụng" : task.getException().getMessage().contains("network error") ? "Kiểm tra internet của bạn" : "Có lỗi xảy ra";
                                Toast.makeText(RegisterActivity.this, ms, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }else {Toast.makeText(RegisterActivity.this, "Bạn chưa nhập đủ thông tin", Toast.LENGTH_SHORT).show();}
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
