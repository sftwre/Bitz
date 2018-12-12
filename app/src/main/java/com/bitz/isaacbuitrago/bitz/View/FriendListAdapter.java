package com.bitz.isaacbuitrago.bitz.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bitz.isaacbuitrago.bitz.Model.Friend;
import com.bitz.isaacbuitrago.bitz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter and ViewHolder for handling the display of a list of friends.
 *
 * @author isaacbuitrago
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendsViewHolder>
{

    private Context mContext;
    private List<Friend> friends;
    private ItemClickListener listener;
    private SparseBooleanArray selectedItems;

    public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView userName, fullName;
        public LinearLayout friendContainer;

        @SuppressLint("ResourceAsColor")
        public FriendsViewHolder(View view)
        {
            super(view);

            userName = (TextView) view.findViewById(R.id.userName);

            fullName = (TextView) view.findViewById(R.id.fullName);

            friendContainer = (LinearLayout) view.findViewById(R.id.friendsContainer);

            friendContainer.setOnClickListener(this);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view)
        {
            if(listener != null)
                listener.onItemRowClicked(view, getAdapterPosition());
        }
    }

    public Friend getItem(int position)
    {
        return friends.get(position);
    }


    public FriendListAdapter(Context mContext, List<Friend> friends, ItemClickListener listener)
    {
        this.mContext = mContext;
        this.friends = friends;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_row, parent, false);

        return new FriendsViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final FriendsViewHolder holder, final int position)
    {
        Friend friend = friends.get(position);

        // displaying text view data
        holder.userName.setText(friend.getUserName());
        holder.fullName.setText(friend.getFullName());

    }


//        // change the font style depending on message read status
//        applyReadStatus(holder, message);
//
//        // handle message star
//        applyImportant(holder, message);
//
//        // handle icon animation
//        applyIconAnimation(holder, position);
//
//        // display profile image
//        applyProfilePicture(holder, message);
//
//    private void applyProfilePicture(MyViewHolder holder, Message message) {
//        if (!TextUtils.isEmpty(message.getPicture())) {
//            Glide.with(mContext).load(message.getPicture())
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .transform(new CircleTransform(mContext))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(holder.imgProfile);
//            holder.imgProfile.setColorFilter(null);
//            holder.iconText.setVisibility(View.GONE);
//        } else {
//            holder.imgProfile.setImageResource(R.drawable.bg_circle);
//            holder.imgProfile.setColorFilter(message.getColor());
//            holder.iconText.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void applyIconAnimation(FriendsViewHolder holder, int position) {
//        if (selectedItems.get(position, false)) {
//            holder.iconFront.setVisibility(View.GONE);
//            resetIconYAxis(holder);
//            holder.iconBack.setVisibility(View.VISIBLE);
//            holder.iconBack.setAlpha(1);
//            if (currentSelectedIndex == position) {
//                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
//                resetCurrentIndex();
//            }
//        } else {
//            holder.iconBack.setVisibility(View.GONE);
//            resetIconYAxis(holder.iconFront);
//            holder.iconFront.setVisibility(View.VISIBLE);
//            holder.iconFront.setAlpha(1);
//            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
//                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
//                resetCurrentIndex();
//            }
//        }
//    }


    @Override
    public long getItemId(int position)
    {
        return friends.get(position).hashCode();
    }

    public void toggleSelection(int pos)
    {
        if (selectedItems.get(pos, false))
        {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }

        notifyItemChanged(pos);
    }

    public void clearSelections()
    {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount()
    {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems()
    {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());

        for (int i = 0; i < selectedItems.size(); i++)
        {
            items.add(selectedItems.keyAt(i));
        }

        return items;
    }

    public boolean inSelectionArray(int position)
    {
        return selectedItems.get(position, false);
    }

//    private void applyImportant(MyViewHolder holder, Message message) {
//        if (message.isImportant()) {
//            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_black_24dp));
//            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_selected));
//        } else {
//            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_border_black_24dp));
//            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_normal));
//        }
//    }
//
//    private void applyReadStatus(MyViewHolder holder, Message message) {
//        if (message.isRead()) {
//            holder.from.setTypeface(null, Typeface.NORMAL);
//            holder.subject.setTypeface(null, Typeface.NORMAL);
//            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
//            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.message));
//        } else {
//            holder.from.setTypeface(null, Typeface.BOLD);
//            holder.subject.setTypeface(null, Typeface.BOLD);
//            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.from));
//            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
//        }
//    }

    @Override
    public int getItemCount()
    {
        return friends.size();
    }

}