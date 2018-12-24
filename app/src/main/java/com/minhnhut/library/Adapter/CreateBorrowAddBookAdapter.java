//package com.minhnhut.library.Adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.minhnhut.library.CreateBorrowActivity;
//import com.minhnhut.library.DataObj.Book;
//import com.minhnhut.library.DataObj.BorrowBook;
//import com.minhnhut.library.R;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//
//import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
//
//public class CreateBorrowAddBookAdapter extends RecyclerView.Adapter<CreateBorrowAddBookAdapter.ViewHolder> {
//    Context context;
//    ArrayList<Book> books;
//
//    public CreateBorrowAddBookAdapter(Context context, ArrayList<Book> books) {
//        this.context = context;
//        this.books = books;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
//        View itemView = layoutInflater.inflate(R.layout.item_create_borrow_add_book, viewGroup, false);
//        return new ViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        Book itemBook = books.get(i);
//        String book_id = itemBook.book_id;
//        String author = itemBook.author;
//        String book_name = itemBook.book_name;
//        String book_image = itemBook.book_image;
//
//        viewHolder.tvBookId.setText(book_id);
//        viewHolder.tvAuthor.setText(author);
//        viewHolder.tvbookName.setText(book_name);
//        Picasso.get().load(book_image).fit().centerInside().placeholder(R.drawable.noavatarbook).into(viewHolder.ivBookImage);
//    }
//
//    @Override
//    public int getItemCount() {
//        return books.size();
//    }
//
//    public ArrayList<BorrowBook> arrayListBorrowBook;
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView ivBookImage, ivAddBook;
//        TextView tvbookName, tvAuthor, tvBookId;
//        EditText etQuantity;
//
//        public ViewHolder(@NonNull final View itemView) {
//            super(itemView);
//
//            tvBookId = (TextView) itemView.findViewById(R.id.textViewItemBookId) ;
//            tvbookName = (TextView) itemView.findViewById(R.id.ItemBookBookName) ;
//            tvAuthor = (TextView) itemView.findViewById(R.id.ItemBookAuthor) ;
//            ivBookImage = (ImageView) itemView.findViewById(R.id.ItemImageBook);
//
//            ivAddBook = (ImageView) itemView.findViewById(R.id.imageViewAddBook);
//            etQuantity = (EditText) itemView.findViewById(R.id.EditTextCreateBorrowAddBookQuantity);
//
//            ivAddBook.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    arrayListBorrowBook = new ArrayList<BorrowBook>();
//                    arrayListBorrowBook.add(new BorrowBook(books.get(getAdapterPosition()).book_id,
//                            books.get(getAdapterPosition()).book_name,
//                            books.get(getAdapterPosition()).author,
//                            books.get(getAdapterPosition()).book_image,
//                            Integer.valueOf(etQuantity.getText().toString())));
//                    Intent intent = new Intent(context.getApplicationContext(),CreateBorrowActivity.class);
//                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("arrayListBorrowBook", arrayListBorrowBook);
//                    context.getApplicationContext().startActivity(intent);
//
////                    dataArrayListBorrowBook(arrayList);
////                    Intent intent = new Intent(context.getApplicationContext(),CreateBorrowActivity.class);
////                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
////                    intent.putExtra("book_id", books.get(getAdapterPosition()).book_id);
////                    intent.putExtra("book_name", books.get(getAdapterPosition()).book_name);
////                    intent.putExtra("book_image", books.get(getAdapterPosition()).book_image);
////                    intent.putExtra("author", books.get(getAdapterPosition()).author);
////                    intent.putExtra("quantity", quantity);
////                    context.getApplicationContext().startActivity(intent);
//                }
//            });
//        }
//    }
//}
