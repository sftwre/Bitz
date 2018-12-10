package com.bitz.isaacbuitrago.bitz.View;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitz.isaacbuitrago.bitz.R;

/**
 *
 * Adapter for Bitz
 *
 * @author isaacbuitrago
 */
public class BitzInboxAdapter extends RecyclerView.Adapter<BitzInboxAdapter.BitzViewHolder>
{

    /**
     *
     */
    public BitzInboxAdapter()
    {

    }

    @NonNull
    @Override
    public BitzViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bitz_inbox_card, viewGroup, false);

        return new BitzViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BitzViewHolder viewHolder, int i)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    /**
     * Responsible for holding the view that is displayed in a CardView
     *
     */
    public class BitzViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView titleTextView, artistTextView, userNameTextView;

        ImageView albumCoverImageView;

        public BitzViewHolder(@NonNull View itemView)
        {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);

            artistTextView = itemView.findViewById(R.id.artistTextView);

            userNameTextView = itemView.findViewById(R.id.userNameTextView);

            albumCoverImageView = itemView.findViewById(R.id.albumCoverImageView);
        }

        @Override
        public void onClick(View v)
        {

        }
    }

}
