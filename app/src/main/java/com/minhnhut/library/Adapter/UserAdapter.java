package com.minhnhut.library.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.minhnhut.library.DataObj.User;
import com.minhnhut.library.R;
import com.minhnhut.library.UpdateUserActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.minhnhut.library.DataObj.fBuild.db_Users;
import static com.minhnhut.library.DataObj.fBuild.deleteImage;
import static com.minhnhut.library.DataObj.fBuild.email_superadmin;
import static com.minhnhut.library.DataObj.fBuild.getParams;
import static com.minhnhut.library.DataObj.fBuild.password_superadmin;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    ArrayList<User> users;
    Context context;

    String muser_email;
    String muser_password;
    FirebaseUser user;
    int lv;
    public UserAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_user, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        User user = users.get(i);

        viewHolder.tvUserId.setText(user.id);
        viewHolder.tvEmailUser.setText(user.email);
        viewHolder.tvUserName.setText(user.username);
        Picasso.get().load(user.avatar).placeholder(R.drawable.avateruser).fit().into(viewHolder.civAvatarUser);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civAvatarUser;
        TextView tvUserName, tvEmailUser, tvUserId;
        ImageView ivDeleteUser, ivEditUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserId = (TextView) itemView.findViewById(R.id.ItemUserId);
            tvEmailUser = (TextView) itemView.findViewById(R.id.ItemEmailUser);
            tvUserName = (TextView) itemView.findViewById(R.id.ItemUserName);
            civAvatarUser = (CircleImageView) itemView.findViewById(R.id.ItemAvatarUser);


            ivEditUser =(ImageView) itemView.findViewById(R.id.imageViewEditUser);
            ivDeleteUser =(ImageView) itemView.findViewById(R.id.imageViewDeleteUser);

            deleteUser();
            editUser();
        }

        private void editUser() {
            ivEditUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(), UpdateUserActivity.class);
                    intent.putExtra("user_id", users.get(getAdapterPosition()).id);
                    intent.putExtra("username", users.get(getAdapterPosition()).username);
                    intent.putExtra("email", users.get(getAdapterPosition()).email);
                    intent.putExtra("level", users.get(getAdapterPosition()).level);
                    intent.putExtra("avatar", users.get(getAdapterPosition()).avatar);
                    intent.putExtra("password", users.get(getAdapterPosition()).password);
                    context.startActivity(intent);
                }
            });
        }

        private void deleteUser() {
            ivDeleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(context.getApplicationContext());
                    dialog.getWindow().setType(getParams());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_delete_category);
                    dialog.setCanceledOnTouchOutside(false);

                    TextView tvDialogDeleteName = (TextView) dialog.findViewById(R.id.TextViewDialogDeleteName);
                    TextView tvDialogDeleteQuestion = (TextView) dialog.findViewById(R.id.TextViewDialogDeleteQuestion);
                    Button btDialogUserDelete = (Button) dialog.findViewById(R.id.buttonSubmitDialogDelete);
                    Button btCannelDialogUserDelete = (Button) dialog.findViewById(R.id.buttonCannelDialogDelete);
                    tvDialogDeleteName.setText(tvUserName.getText());
                    tvDialogDeleteQuestion.setText("Bạn có muốn xóa người dùng này?");

                    btDialogUserDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final FirebaseAuth auth = FirebaseAuth.getInstance();
                            auth.signInWithEmailAndPassword(users.get(getAdapterPosition()).email, users.get(getAdapterPosition()).password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseUser user_delete = auth.getCurrentUser();
                                    user_delete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            final String uri_delete = users.get(getAdapterPosition()).avatar;
                                            db_Users.child(users.get(getAdapterPosition()).id).removeValue(new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    if (databaseError == null){
                                                        deleteImage(uri_delete);
                                                        users.remove(getAdapterPosition());
                                                        notifyItemRemoved(getAdapterPosition());
                                                        auth.signInWithEmailAndPassword(email_superadmin, password_superadmin);
                                                        dialog.cancel();
                                                        Toast.makeText(context, ""+ "Xóa thành công !" , Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(context, ""+ "Xóa không thành công !", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                    btCannelDialogUserDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }

            });
        }
    }
}
