package com.minhnhut.library.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.minhnhut.library.DataObj.Image;
import com.minhnhut.library.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.minhnhut.library.DataObj.fBuild.getParams;

public class ShowImageViewDetailBookAdapter extends RecyclerView.Adapter<ShowImageViewDetailBookAdapter.ViewHolder>  {
    Context context;
    ArrayList<Image> images;

    public ShowImageViewDetailBookAdapter(Context context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ShowImageViewDetailBookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View v = layoutInflater.inflate(R.layout.item_image_show_detailbook, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowImageViewDetailBookAdapter.ViewHolder viewHolder, int i) {
        Image itemImageBook = images.get(i);
        Picasso.get().load(itemImageBook.url).fit().centerInside().placeholder(R.drawable.noavatarbook).into(viewHolder.ImageViewImageDetailBook);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        int positionImage;
        ImageView ImageViewImageDetailBook;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageViewImageDetailBook = (ImageView) itemView.findViewById(R.id.ImageViewImageDetailBook);

            ImageViewImageDetailBook.setOnClickListener(new View.OnClickListener() {
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
                    Picasso.get().load(images.get(positionImage).url).fit().centerInside().placeholder(R.drawable.noavatarbook).into(imageViewImageZoom);

                    imageViewImageBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(positionImage > 0){
                                positionImage -= 1;
                                Picasso.get().load(images.get(positionImage).url).fit().centerInside().placeholder(R.drawable.noavatarbook).into(imageViewImageZoom);
                            }else {
                                Toast.makeText(context, "Bạn đang ở đầu danh sách", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    imageViewImageNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(positionImage < images.size()-1){
                                positionImage += 1;
                                Picasso.get().load(images.get(positionImage).url).fit().centerInside().placeholder(R.drawable.noavatarbook).into(imageViewImageZoom);
                            }else {
                                Toast.makeText(context, "Bạn đang ở cuối danh sách", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();

                }
            });
        }

    }
}
