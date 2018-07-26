package com.example.bhati.moviehub.videos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bhati.moviehub.network.MovieAPI;
import com.example.bhati.moviehub.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {

    private List<Result> mResults;
    private Context mContext;
    private String mPosterPath;
    private OnClickTrailer mOnClickTrailer;

    public interface OnClickTrailer{
        void onClick(String key);
    }

    public DetailAdapter(List<Result> results, Context context, String posterPath, OnClickTrailer onClickTrailer) {
        mResults = results;
        mContext = context;
        mPosterPath = posterPath;
        mOnClickTrailer = onClickTrailer;
    }
    public void setResults(List<Result> results) {
        mResults = results;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.each_item_detail, parent, false);
        return new DetailViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        final Result result = mResults.get(position);
        holder.mTextView.setText(result.getName());

        Picasso.with(mContext)
                .load(MovieAPI.IMAGE_URL + mPosterPath)
                .into(holder.mImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickTrailer.onClick(result.getKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResults==null?0:mResults.size();
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;
        TextView mTextView;
        public DetailViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_movie_poster_detail);
            mTextView = itemView.findViewById(R.id.tv_movie_title_detail);

        }
    }
}
