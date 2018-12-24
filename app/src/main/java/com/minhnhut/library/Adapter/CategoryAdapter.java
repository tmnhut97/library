package com.minhnhut.library.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minhnhut.library.DataObj.Category;
import com.minhnhut.library.R;

import java.util.ArrayList;

import static com.minhnhut.library.DataObj.fBuild.getParams;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{
    private DatabaseReference db;
    ArrayList<Category> categories;
    Context context;

    public CategoryAdapter(ArrayList<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_category, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Category itemCategory = categories.get(i);

        String category_id = itemCategory.category_id;
        String category_name = itemCategory.category_name;

        viewHolder.tvCategoryId.setText(category_id);
        viewHolder.tvCategoryName.setText(category_name);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView btMore;
        String CategoryId;
        TextView tvCategoryName;
        TextView tvCategoryId;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvCategoryId = (TextView) itemView.findViewById(R.id.textViewItemCategoryId);
            tvCategoryName = (TextView) itemView.findViewById(R.id.textViewItemCategoryName);
            btMore = (ImageView) itemView.findViewById(R.id.imageViewMoreCategory);
            btMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenuMore();
                }
            });
        }
        private void showMenuMore(){
            PopupMenu popupMenu = new PopupMenu(context,btMore);
            popupMenu.getMenuInflater().inflate(R.menu.category_ud_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.menuUpdate:
                            final Dialog dialogUpdateCategory = new Dialog(context.getApplicationContext());
                            dialogUpdateCategory.getWindow().setType(getParams());
                            dialogUpdateCategory.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogUpdateCategory.setContentView(R.layout.dialog_update_category);
                            dialogUpdateCategory.setCanceledOnTouchOutside(false);

                            final TextView tvUpdateCategory_er = (TextView) dialogUpdateCategory.findViewById(R.id.TextViewUpdateCategory_er);
                            TextView tvCategoryNameOld = (TextView) dialogUpdateCategory.findViewById(R.id.TextViewDialogUpdateCategoryNameOld);
                            Button btSubmitDialogUpdateCategory = (Button) dialogUpdateCategory.findViewById(R.id.buttonSubmitDialogUpdateCategory);
                            Button btCannelDialogUpdateCategory = (Button) dialogUpdateCategory.findViewById(R.id.buttonCannelDialogUpdateCategory);
                            final EditText etCategoryNameNew = (EditText) dialogUpdateCategory.findViewById(R.id.TextViewDialogUpdateCategoryNameNew);

                            tvCategoryNameOld.setText(tvCategoryName.getText().toString());
                            btSubmitDialogUpdateCategory.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CategoryId = tvCategoryId.getText().toString();
//                                    String CategoryName = tvCategoryName.getText().toString();
                                    String CategoryNameNew = etCategoryNameNew.getText().toString();
                                    if (CategoryNameNew.length() < 4){
                                        tvUpdateCategory_er.setText("Tên danh mục phải trên 3 kí tự");
                                    }
                                    else {
                                        db = FirebaseDatabase.getInstance().getReference("categories");
                                        db.child(CategoryId).child("category_name").setValue(CategoryNameNew);
                                        categories.set(getAdapterPosition(), new Category(CategoryId,CategoryNameNew));
                                        dialogUpdateCategory.cancel();

                                        notifyItemChanged(getAdapterPosition());
                                    }

                                }
                            });
                            btCannelDialogUpdateCategory.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogUpdateCategory.cancel();
                                }
                            });
                            dialogUpdateCategory.show();
                            break;
                        case R.id.menuDelete:
                            final Dialog dialogDeleteCategory = new Dialog(context.getApplicationContext());
                            dialogDeleteCategory.getWindow().setType(getParams());
                            dialogDeleteCategory.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogDeleteCategory.setContentView(R.layout.dialog_delete_category);
                            dialogDeleteCategory.setCanceledOnTouchOutside(false);

                            TextView tvDialogDeleteCategoryName = (TextView) dialogDeleteCategory.findViewById(R.id.TextViewDialogDeleteName);
                            TextView tvDialogDeleteQuestion = (TextView) dialogDeleteCategory.findViewById(R.id.TextViewDialogDeleteQuestion);
                            Button btDialogCategoryDelete = (Button) dialogDeleteCategory.findViewById(R.id.buttonSubmitDialogDelete);
                            Button btCannelDialogCategoryDelete = (Button) dialogDeleteCategory.findViewById(R.id.buttonCannelDialogDelete);
                            tvDialogDeleteCategoryName.setText(tvCategoryName.getText());
                            tvDialogDeleteQuestion.setText("Bạn có muốn xóa danh mục này?");

                            btDialogCategoryDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CategoryId = tvCategoryId.getText().toString();
                                    db = FirebaseDatabase.getInstance().getReference("categories");
                                    db.child(CategoryId).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError == null){
                                                categories.remove(getAdapterPosition());
                                                notifyItemRemoved(getAdapterPosition());
                                                dialogDeleteCategory.cancel();
                                                Toast.makeText(context, ""+ "Xóa thành công !" , Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(context, ""+ "Xóa không thành công !", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            btCannelDialogCategoryDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogDeleteCategory.cancel();
                                }
                            });

                            dialogDeleteCategory.show();

                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
        }
    }
}
