package com.minhnhut.library.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minhnhut.library.DataObj.Book;
import com.minhnhut.library.Details_BookActivity;
import com.minhnhut.library.R;
import com.minhnhut.library.Update_BookActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.minhnhut.library.DataObj.fBuild.deleteImage;
import static com.minhnhut.library.DataObj.fBuild.getParams;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    ArrayList<Book> books;
    Context context;
    DatabaseReference db;
    int CAMERA = 11;
    int LIBRARY_PICTURE = 22;
    String uriImageDelete;


    public BookAdapter(ArrayList<Book> books, Context context) {

        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_book, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Book itemBook = books.get(i);
        String book_id = itemBook.book_id;
        String author = itemBook.author;
        String book_name = itemBook.book_name;
        String book_image = itemBook.book_image;

        viewHolder.tvBookId.setText(book_id);
        viewHolder.tvbookName.setText(book_name);
        viewHolder.tvAuthor.setText(author);
        Picasso.get().load(book_image).fit().centerInside().placeholder(R.drawable.noavatarbook).into(viewHolder.ivBookImage);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        String BookId;
        TextView tvBookId, tvAuthor, tvbookName;
        ImageView ivBookImage;
        ImageView ivMoreBook;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookId = (TextView) itemView.findViewById(R.id.textViewItemBookId);
            tvbookName = (TextView) itemView.findViewById(R.id.ItemBookBookName);
            tvAuthor = (TextView) itemView.findViewById(R.id.ItemBookAuthor);
            ivBookImage = (ImageView)  itemView.findViewById(R.id.ItemImageBook);
            ivMoreBook = (ImageView) itemView.findViewById(R.id.imageViewMoreBook);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(),Details_BookActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("book_id", tvBookId.getText().toString());
                    context.getApplicationContext().startActivity(intent);
                }
            });

            ivMoreBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenuMoreBook();
                }
            });
        }
        private void showMenuMoreBook(){
            PopupMenu popupMenu = new PopupMenu(context,ivMoreBook);
            popupMenu.getMenuInflater().inflate(R.menu.category_ud_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.menuUpdate:
                            Intent intent = new Intent(context.getApplicationContext(), Update_BookActivity.class);
                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("book_id", tvBookId.getText().toString());
                            context.getApplicationContext().startActivity(intent);

                            break;
                        case R.id.menuDelete:

                            final Dialog dialogDeleteBook = new Dialog(context.getApplicationContext());
                            dialogDeleteBook.getWindow().setType(getParams());
                            dialogDeleteBook.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogDeleteBook.setContentView(R.layout.dialog_delete_category);
                            dialogDeleteBook.setCanceledOnTouchOutside(false);

                            TextView tvDialogDeleteName = (TextView) dialogDeleteBook.findViewById(R.id.TextViewDialogDeleteName);
                            TextView tvDialogDeleteQuestion = (TextView) dialogDeleteBook.findViewById(R.id.TextViewDialogDeleteQuestion);
                            Button btDialogBookDelete = (Button) dialogDeleteBook.findViewById(R.id.buttonSubmitDialogDelete);
                            Button btCannelDialogBookDelete = (Button) dialogDeleteBook.findViewById(R.id.buttonCannelDialogDelete);
                            tvDialogDeleteName.setText(tvbookName.getText());
                            tvDialogDeleteQuestion.setText("Bạn có muốn xóa sách này?");

                            btDialogBookDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BookId = tvBookId.getText().toString();
                                    db = FirebaseDatabase.getInstance().getReference("books");
                                    db.child(BookId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Book book = dataSnapshot.getValue(Book.class);
                                            uriImageDelete = book.book_image;
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                                    });
                                    db.child(BookId).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError == null){
                                                deleteImage(uriImageDelete);
                                                books.remove(getAdapterPosition());
                                                notifyItemRemoved(getAdapterPosition());
                                                dialogDeleteBook.cancel();
                                                Toast.makeText(context, ""+ "Xóa thành công !" , Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(context, ""+ "Xóa không thành công !", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            btCannelDialogBookDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogDeleteBook.cancel();
                                }
                            });

                            dialogDeleteBook.show();

                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
        }

    }
}
