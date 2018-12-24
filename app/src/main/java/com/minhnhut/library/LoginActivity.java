package com.minhnhut.library;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener{
    private FirebaseAuth mAuth;
    Validator validator;

    @NotEmpty(message = "Email không được rỗng")
    @Email(message = "Email chưa đúng định dạng")
    private EditText etEmail;

    @Password(min = 6, scheme = Password.Scheme.ANY, message = "Mật khẩu phải trên 6 kí tự")
    @Pattern(regex = "[A-Za-z0-9]+", message = "Mật khẩu không được chứa kí tự đặt biệt")
    private EditText etPassWord;
    String userId;
    TextView tvRegister;
    Button btLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);

        checkDrawOverlayPermission();

        mAuth = FirebaseAuth.getInstance();
        etEmail = (EditText) findViewById(R.id.editTextEmail);
        etPassWord = (EditText) findViewById(R.id.editTextPassWord);

        tvRegister = (TextView) findViewById(R.id.textViewRegister);
        btLogin = (Button) findViewById(R.id.buttonLogin);

        validator = new Validator(this);
        validator.setValidationListener(this);


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onValidationSucceeded() {
        String email = etEmail.getText().toString();
        String password = etPassWord.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    String ms = (task.getException().getMessage().contains("network")) ? "Kiểm tra kết nối internet của bạn !" : "Email hoặc mật khẩu không chính xác";
                    Toast.makeText(LoginActivity.this,  ms, Toast.LENGTH_SHORT).show();
                }
            }

        });

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

    public void checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!Settings.canDrawOverlays(LoginActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 111);
            }else {
                return;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        finish();
    }
}
