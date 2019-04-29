package com.ctse.androidgamereviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctse.androidgamereviewer.data.entities.Review;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewViewAdapter extends RecyclerView.Adapter<ReviewViewAdapter.ReviewHolder> {

    private List<Review> reviews = new ArrayList<>();
    private Context mContext;

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
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

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
