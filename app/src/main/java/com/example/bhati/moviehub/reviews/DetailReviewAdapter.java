package com.example.bhati.moviehub.reviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bhati.moviehub.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailReviewAdapter extends RecyclerView.Adapter<DetailReviewAdapter.DetailReviewViewHolder> {

    private List<Result> mResults;
    private Context mContext;
    private onReviewClicked mOnReviewClicked;

    public interface onReviewClicked{
        void onClicked(Result result, int position, int size);
    }

    public DetailReviewAdapter(List<Result> results, Context context, onReviewClicked onReviewClicked) {
        mResults = results;
        mContext = context;
        mOnReviewClicked = onReviewClicked;
    }

    @NonNull
    @Override
    public DetailReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.review_layout, parent, false);
        return new DetailReviewViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailReviewViewHolder holder, int position) {
        final Result result = mResults.get(position);
        holder.mImageView.setImageResource(R.drawable.ic_favorite_red_48dp);
        holder.mProfileName.setText(result.getAuthor());
        holder.mProfileReview.setText(result.getContent());
        final int commentNumber = ++position;
        holder.mReviewIdentifier.setText(mContext.getString(R.string.comment_identifier, commentNumber, mResults.size()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnReviewClicked.onClicked(result, commentNumber,mResults.size());
            }
        });
    }

    public void setResults(List<Result> results) {
        mResults = results;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mResults == null ? 0 : mResults.size();
    }

    public class DetailReviewViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mImageView;
        TextView mProfileName;
        TextView mProfileReview;
        TextView mReviewIdentifier;

        public DetailReviewViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.profile_image);
            mProfileName = itemView.findViewById(R.id.profile_name);
            mProfileReview = itemView.findViewById(R.id.profile_comment);
            mReviewIdentifier = itemView.findViewById(R.id.review_identifier);
        }
    }
}
