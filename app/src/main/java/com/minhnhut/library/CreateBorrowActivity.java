//package com.minhnhut.library;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.minhnhut.library.Adapter.CreateBorrowAddBookAdapter;
//import com.minhnhut.library.Adapter.ViewBookCreateBorrowAddBookAdapter;
//import com.minhnhut.library.DataObj.Book;
//import com.minhnhut.library.DataObj.BorrowBook;
//
//import java.util.ArrayList;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
//import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
//
//import static com.minhnhut.library.DataObj.fBuild.db_Books;
//
//public class CreateBorrowActivity extends AppCompatActivity {
//
//    ImageView imageViewSearch;
//    EditText etCode;
//    CardView cardViewUser;
//    CircleImageView ItemAvatarUser;
//    TextView ItemUserName, ItemEmailUser, textSearch;
//    RecyclerView recyclerViewCreateBorrowAddBook, ViewBorrowBook;
//
//    ArrayList<Book> arrayList;
//    ArrayList<BorrowBook> arrayListBorrowBook;
//    CreateBorrowAddBookAdapter createBorrowAddBookAdapter;
//    ViewBookCreateBorrowAddBookAdapter viewBookCreateBorrowAddBookAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_borrow);
//
//        etCode = (EditText) findViewById(R.id.editTextCode);
//        cardViewUser = (CardView) findViewById(R.id.cardViewUser);
//        recyclerViewCreateBorrowAddBook = (RecyclerView) findViewById(R.id.RecyclerViewCreateBorrowAddBook);
//        ViewBorrowBook = (RecyclerView) findViewById(R.id.ViewBorrowBook);
//        imageViewSearch= (ImageView) findViewById(R.id.imageViewSearch);
//        textSearch = (EditText) findViewById(R.id.textSearch);
//
//        openTextSearch();
//
//        initRecyclerViewCreateBorrowAddBook(recyclerViewCreateBorrowAddBook);
////        change_etCode();
//        dataBook();
//
//
//        getdataAddBookBorrow();
//    }
//
//    private void getdataAddBookBorrow() {
//        Intent intent = getIntent();
//        arrayListBorrowBook = (ArrayList<BorrowBook>)getIntent().getSerializableExtra("arrayListBorrowBook");
//        Log.d("vvv", arrayListBorrowBook +"");
////        Intent intent = getIntent();
////        String book_id = intent.getStringExtra("book_id");
////        String book_name = intent.getStringExtra("book_name");
////        String book_image = intent.getStringExtra("book_image");
////        String author = intent.getStringExtra("author");
////        String quantity = intent.getStringExtra("quantity");
////
////        if (book_id != null && quantity != null){
////            ViewBorrowBook.setFocusableInTouchMode(true);
////            ViewBorrowBook.requestFocus();
////
////            ViewBorrowBook.setHasFixedSize(true);
////            ViewBorrowBook.setItemAnimator(new SlideInLeftAnimator());
////            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
////            ViewBorrowBook.setLayoutManager(layoutManager);
////            ViewBorrowBook.setItemAnimator(new SlideInUpAnimator());
////            arrayListBorrowBook = new ArrayList<>();
////            arrayListBorrowBook.add(new BorrowBook(book_id, book_name, author, book_image, Integer.valueOf(quantity)));
////
////
////            viewBookCreateBorrowAddBookAdapter = new ViewBookCreateBorrowAddBookAdapter(getApplicationContext(), arrayListBorrowBook);
////            ViewBorrowBook.setAdapter(viewBookCreateBorrowAddBookAdapter);
////
////            Log.d("vvv", book_id + "/" + quantity );
////        }
//    }
//
//    private void openTextSearch() {
//        imageViewSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                textSearch.setVisibility(View.VISIBLE);
//                textSearch.setFocusableInTouchMode(true);
//                textSearch.requestFocus();
//            }
//        });
//    }
//
//
////    private void change_etCode() {
////        etCode.addTextChangedListener(new TextWatcher() {
////            @Override
////            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
////
////            @Override
////            public void onTextChanged(CharSequence s, int start, int before, int count) {}
////
////            @Override
////            public void afterTextChanged(Editable s) {
////                final String search = s.toString();
////                if (search.length() != 6){
////                    cardViewUser.setVisibility(View.GONE);
////                }else {
////                    dataUser(search);
////                }
////
////            }
////        });
////    }
////    private void dataUser(final String code) {
////        Query filter = FirebaseDatabase.getInstance().getReference("users").orderByChild("code");
////        filter.addChildEventListener(new ChildEventListener() {
////            @Override
////            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////                User user = dataSnapshot.getValue(User.class);
////                if(user.code == Integer.valueOf(code)){
////                    cardViewUser.setVisibility(View.VISIBLE);
////                    ItemUserName = (TextView) findViewById(R.id.ItemUserName) ;
////                    ItemEmailUser = (TextView) findViewById(R.id.ItemEmailUser) ;
////                    ItemAvatarUser = (CircleImageView) findViewById(R.id.ItemAvatarUser);
////                    ItemUserName.setText(user.username);
////                    ItemEmailUser.setText(user.email);
////                    Picasso.get().load(user.avatar).fit().placeholder(R.drawable.avateruser).into(ItemAvatarUser);
////                }
////            }
////
////            @Override
////            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////            }
////            @Override
////            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
////            @Override
////            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {}
////        });
////    }
//
///////// Dialog ADD BOOK //////
////    private void click_fabCreateBorrowAddBook() {
////        fabCreateBorrowAddBook = (ImageView) findViewById(R.id.fabCreateBorrowAddBook) ;
////        fabCreateBorrowAddBook.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                final Dialog dialog = new Dialog(CreateBorrowActivity.this);
////                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////                dialog.setContentView(R.layout.dialog_create_borrow_add_book);
////                dialog.setCanceledOnTouchOutside(true);
////
////                SearchView searchViewSearchBookAdd = (SearchView) dialog.findViewById(R.id.searchViewSearchBookAdd);
////                TextView EditTextCreateBorrowAddBookQuantity = (TextView) dialog.findViewById(R.id.EditTextCreateBorrowAddBookQuantity);
////                RecyclerView recyclerViewCreateBorrowAddBook = (RecyclerView) dialog.findViewById(R.id.recyclerViewCreateBorrowAddBook);
////                ImageView imageViewCannel = (ImageView) dialog.findViewById(R.id.imageViewCannel) ;
////
////                imageViewCannel.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        dialog.cancel();
////                    }
////                });
////                initRecyclerViewCreateBorrowAddBook(recyclerViewCreateBorrowAddBook);
////                dataBook();
////
////                dialog.show();
////            }
////        });
////    }
//
//    private void initRecyclerViewCreateBorrowAddBook(RecyclerView recyclerView){
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemAnimator(new SlideInLeftAnimator());
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new SlideInUpAnimator());
//        arrayList = new ArrayList<>();
//        createBorrowAddBookAdapter = new CreateBorrowAddBookAdapter(getApplicationContext(), arrayList);
//        recyclerView.setAdapter(createBorrowAddBookAdapter);
//    }
//    private void dataBook() {
//        db_Books.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(dataSnapshot.exists()){
//                    Book book = dataSnapshot.getValue(Book.class);
//                    arrayList.add(new Book(dataSnapshot.getKey(), book.book_name, book.author, book.book_image));
//                    createBorrowAddBookAdapter.notifyDataSetChanged();
//                }
//            }
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) { }
//        });
//    }
//
//}
