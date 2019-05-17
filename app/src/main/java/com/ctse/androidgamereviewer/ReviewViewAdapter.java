/**
 * CTSE Android Project - Game Reviewer
 * @author IT16037434 Karunaratne D. C.
 * @author IT15146366 Hettiarachchi H. A. I. S.
 *
 * File: ReviewViewAdapter.java
 */
package com.ctse.androidgamereviewer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctse.androidgamereviewer.data.entities.Review;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * To feed all reviews of a game to a list in the ViewGameDetailsActivity. This object creates views
 * for items, and replaces the content of some of the views with new items when the original item is
 * no longer visible. It is a controller for the RecyclerView.
 *
 * <a href="https://developer.android.com/guide/topics/ui/layout/recyclerview"> Documentation</a>
 *
 * @see androidx.recyclerview.widget.RecyclerView.Adapter
 * */

public class ReviewViewAdapter extends RecyclerView.Adapter<ReviewViewAdapter.ReviewHolder> {

    private List<Review> reviews = new ArrayList<>();
    private Context mContext;

    public static final String EXTRA_REVIEW_ID = "com.ctse.androidgamereviewer.REVIEW_ID";
    public static final String EXTRA_REVIEW_LOCAL_ID = "com.ctse.androidgamereviewer.REVIEW_LOCAL_ID";
    public static final String EXTRA_REVIEW_USER_EMAIL = "com.ctse.androidgamereviewer.REVIEW_USER_EMAIL";
    public static final String EXTRA_REVIEW_GAME_ID = "com.ctse.androidgamereviewer.REVIEW_GAME_ID";

    public ReviewViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemReviewView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review, parent, false);
        return new ReviewHolder(itemReviewView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        final Review currentReview = reviews.get(position);
        holder.tvReviewTitle.setText(currentReview.getTitle());
        holder.tvReviewDate.setText(currentReview.getDate());
        holder.tvReviewBody.setText(currentReview.getBody());
        holder.tvRating.setText(currentReview.getRating() + "/5");

        // Start ViewReviewActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewReviewActivity.class);
                intent.putExtra(EXTRA_REVIEW_ID, currentReview.get_id());
                intent.putExtra(EXTRA_REVIEW_LOCAL_ID, currentReview.getId());
                intent.putExtra(EXTRA_REVIEW_USER_EMAIL, currentReview.getUserEmail());
                intent.putExtra(EXTRA_REVIEW_GAME_ID, currentReview.getGameId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    /**
     *  Provides a reference class for each individual item in the list.
     */
    class ReviewHolder extends RecyclerView.ViewHolder {

        private TextView tvReviewTitle;
        private TextView tvReviewDate;
        private TextView tvReviewBody;
        private TextView tvRating;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            tvRating = itemView.findViewById(R.id.text_view_rating);
            tvReviewBody = itemView.findViewById(R.id.text_view_review_body);
            tvReviewDate = itemView.findViewById(R.id.text_view_review_date);
            tvReviewTitle = itemView.findViewById(R.id.text_view_review_title);
        }
    }

}
