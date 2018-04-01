package com.example.user.modul6;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;



public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {
    Context context;
    List<Post> posts;

    public PopularAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.recentUsername.setText(post.getUsername());
        holder.recentPhotoTitle.setText(post.getPhotoTitle());
        holder.recentPhotoDesc.setText(post.getPhotoDesc());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView recentUsername, recentPhotoDesc, recentPhotoTitle;
        ImageView recentPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            recentUsername = itemView.findViewById(R.id.popularUsername);
            recentPhotoTitle = itemView.findViewById(R.id.popularPhotoTitle);
            recentPhotoDesc = itemView.findViewById(R.id.popularPhotoDesc);
            recentPhoto = itemView.findViewById(R.id.popularPhoto);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            context.startActivity(intent);
        }
    }
}
