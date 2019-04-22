package com.ctse.androidgamereviewer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctse.androidgamereviewer.data.Game;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GameViewAdapter extends RecyclerView.Adapter<GameViewAdapter.GameHolder> {

    private List<Game> games = new ArrayList<>();

    @NonNull
    @Override
    public GameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new GameHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameHolder holder, int position) {
        Game currentGame = games.get(position);
        holder.tvTitle.setText(currentGame.getTitle());
        holder.tvDescription.setText(currentGame.getGenre());
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    void setGames(List<Game> games) {
        this.games = games;
        notifyDataSetChanged();
    }

    class GameHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDescription;

        public GameHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.text_view_game_title);
            tvDescription = itemView.findViewById(R.id.text_view_description);
        }
    }
}
