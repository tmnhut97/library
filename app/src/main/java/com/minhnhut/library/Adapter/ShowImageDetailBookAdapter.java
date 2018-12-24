package com.minhnhut.library.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minhnhut.library.DataObj.Image;
import com.minhnhut.library.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.minhnhut.library.DataObj.fBuild.db_image;
import static com.minhnhut.library.DataObj.fBuild.getParams;

public class ShowImageDetailBookAdapter extends RecyclerView.Adapter<ShowImageDetailBookAdapter.ViewHolder>  {
    Context context ;
    ArrayList<Image> arrayList;
    View v;

    int positionImage = 0;

    public ShowImageDetailBookAdapter(Context context, ArrayList<Image> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        v = layoutInflater.inflate(R.layout.item_image_show_detailbook, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Image itemImageBook = arrayList.get(i);
        Picasso.get().load(itemImageBook.url).fit().centerInside().placeholder(R.drawable.noavatarbook).into(viewHolder.ivImageDetailBook);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImageDetailBook;
        ImageView ivAddImageDetailBook;

        public ViewHolder(final View itemView) {
            super(itemView);
            ivImageDetailBook = (ImageView) itemView.findViewById(R.id.ImageViewImageDetailBook);
            ivAddImageDetailBook = (ImageView) itemView.findViewById(R.id.ImageViewAddImageDetailBook);

            ivImageDetailBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context.getApplicationContext());
                    dialog.getWindow().setType(getParams() );
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_zoom_image);
                    final ImageView imageViewImageZoom = (ImageView) dialog.findViewById(R.id.imageViewImageZoom);
                    final ImageView imageViewImageBack = (ImageView) dialog.findViewById(R.id.imageViewImageBack);
                    final ImageView imageViewImageNext = (ImageView) dialog.findViewById(R.id.imageViewImageNext);

                    positionImage = getAdapterPosition();
                    Picasso.get().load(arrayList.get(positionImage).url).fit().centerInside().placeholder(R.drawable.noavatarbook).into(imageViewImageZoom);

                    imageViewImageBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(positionImage > 0){
                                positionImage -= 1;
                                Picasso.get().load(arrayList.get(positionImage).url).fit().centerInside().placeholder(R.drawable.noavatarbook).into(imageViewImageZoom);
                            }else {
                                Toast.makeText(context, "Bạn đang ở đầu danh sách", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    imageViewImageNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(positionImage < arrayList.size()-1){
                                positionImage += 1;
                                Picasso.get().load(arrayList.get(positionImage).url).fit().centerInside().placeholder(R.drawable.noavatarbook).into(imageViewImageZoom);
                            }else {
                                Toast.makeText(context, "Bạn đang ở cuối danh sách", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();

                }
            });

            ivImageDetailBook.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(context,ivImageDetailBook, Gravity.TOP, 0,0);
                    popupMenu.getMenuInflater().inflate(R.menu.only_delete_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Query filter = FirebaseDatabase.getInstance().getReference("image").orderByChild("url").equalTo(arrayList.get(getAdapterPosition()).url);
                            filter.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    db_image.child(dataSnapshot.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(arrayList.get(getAdapterPosition()).url);
                                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    arrayList.remove(getAdapterPosition());
                                                    notifyItemRemoved(getAdapterPosition());
                                                    Toast.makeText(context, "Xoá thành công !!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                            return false;
                        }
                    });

                    popupMenu.show();
                    return true;
                }
            });

        }

    }
}
