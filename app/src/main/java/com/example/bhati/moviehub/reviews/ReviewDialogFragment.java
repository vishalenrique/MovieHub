package com.example.bhati.moviehub.reviews;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bhati.moviehub.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewDialogFragment extends DialogFragment {
    private static final String TAG = "ReviewDialogFragment";
    public static final String BUNDLED_RESULT = "bundledResult";
    public static final String BUNDLED_POSITION = "bundledPosition";
    public static final String BUNDLED_NUMBER_OF_COMMENTS = "bundledCommentNumbers";

    CircleImageView mProfileImage;
    TextView mProfileName;
    TextView mProfileCommment;
    TextView mReviewIdentifier;
    private Result mResult;
    private int mPosition;
    private int mSize;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().setTitle("Full review");
        getDialog().setCanceledOnTouchOutside(true);

        View view = inflater.inflate(R.layout.review_fragment,container,false);
        mProfileImage = view.findViewById(R.id.profile_image);
        mProfileName = view.findViewById(R.id.profile_name);
        mProfileCommment = view.findViewById(R.id.profile_comment);
        mReviewIdentifier = view.findViewById(R.id.review_identifier);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mResult = arguments.getParcelable(BUNDLED_RESULT);
            mPosition = arguments.getInt(BUNDLED_POSITION);
            mSize = arguments.getInt(BUNDLED_NUMBER_OF_COMMENTS);
            mProfileImage.setImageResource(R.drawable.ic_favorite_red_48dp);
            mProfileName.setText(mResult.getAuthor());
            mProfileCommment.setText(mResult.getContent());
            mReviewIdentifier.setText(getActivity().getString(R.string.comment_identifier, mPosition, mSize));
        }
        return view;
    }

}
