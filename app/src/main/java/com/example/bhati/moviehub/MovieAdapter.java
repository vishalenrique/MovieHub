package com.example.bhati.moviehub;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context mContext;
    private List<Result> mResults;
    private onClicked mOnClicked;

    public interface onClicked{
         void onClicked(Result result);
    }

    public MovieAdapter(Context context, List<Result> results, onClicked onClicked) {
        mContext = context;
        mResults = results;
        mOnClicked = onClicked;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.each_item, parent, false);
        return new MovieViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        final Result RESULT = mResults.get(position);

        holder.mTextView.setText(RESULT.getTitle());

        StringBuilder imageUrl = new StringBuilder(MovieAPI.IMAGE_URL);
        imageUrl.append(RESULT.getPosterPath());
        Picasso.with(mContext).load(imageUrl.toString()).into(holder.mImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClicked.onClicked(RESULT);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView mTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_movie_poster);
            mTextView = itemView.findViewById(R.id.tv_movie_title);
        }
    }
}
