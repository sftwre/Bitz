package com.bitz.isaacbuitrago.bitz.View;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bitz.isaacbuitrago.bitz.Model.Bit;
import com.bitz.isaacbuitrago.bitz.R;
import java.util.List;

/**
 *
 * Adapter for Bitz
 *
 * @author isaacbuitrago
 */
public class BitzInboxAdapter extends RecyclerView.Adapter<BitzInboxAdapter.BitzViewHolder>
{

    Context mContext;
    List<Bit> bitz;
    ItemClickListener listener;


    /**
     * Constructor
     */
    public BitzInboxAdapter(Context mContext, List<Bit> bitz, ItemClickListener listener)
    {
        this.mContext = mContext;

        this.bitz = bitz;

        this.listener = listener;
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
    public void onBindViewHolder(@NonNull BitzViewHolder viewHolder, int position)
    {
        Bit bit = bitz.get(position);

        viewHolder.artistTextView.setText(bit.getArtist());

        viewHolder.userNameTextView.setText(bit.getSendingUser());

        viewHolder.titleTextView.setText(bit.getTrackTitle());

        // TODO dowload album cover
    }

    @Override
    public int getItemCount()
    {
        return bitz.size();
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

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if(listener != null)
                listener.onItemRowClicked(v, getAdapterPosition());
        }
    }

}
