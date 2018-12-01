package com.bitz.isaacbuitrago.bitz.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bitz.isaacbuitrago.bitz.Model.Friend;
import com.bitz.isaacbuitrago.bitz.R;
import java.util.ArrayList;
import java.util.List;
import static android.view.View.NO_ID;

/**
 * Adapter and ViewHolder for handling the display of a list of friends.
 *
 * @author isaacbuitrago
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendsViewHolder>
{

    private Context mContext;
    private List<Friend> friends;
    private FriendAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener
    {
        public TextView userName, fullName;
        public LinearLayout friendContainer;

        public FriendsViewHolder(View view)
        {
            super(view);

            userName = (TextView) view.findViewById(R.id.userName);

            fullName = (TextView) view.findViewById(R.id.fullName);

            friendContainer = (LinearLayout) view.findViewById(R.id.friendsContainer);

            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view)
        {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


    public FriendAdapter(Context mContext, List<Friend> friends, FriendAdapterListener listener)
    {
        this.mContext = mContext;
        this.friends = friends;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
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

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

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
        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(FriendsViewHolder holder, final int position)
    {

        /**
         * Set the callbacks for handling an click event
         */
        holder.friendContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                listener.onIconClicked(position);
            }
        });


        holder.friendContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                listener.onMessageRowClicked(position);
            }
        });

        holder.friendContainer.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

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

//    private void applyIconAnimation(MyViewHolder holder, int position) {
//        if (selectedItems.get(position, false)) {
//            holder.iconFront.setVisibility(View.GONE);
//            resetIconYAxis(holder.iconBack);
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


    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex()
    {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    @Override
    public long getItemId(int position)
    {
        return NO_ID;
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

    public void toggleSelection(int pos)
    {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections()
    {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount()
    {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems()
    {
        List<Integer> items = new ArrayList<>(selectedItems.size());

        for (int i = 0; i < selectedItems.size(); i++)
        {
            items.add(selectedItems.keyAt(i));
        }

        return items;
    }

    public void removeData(int position)
    {
        friends.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex()
    {
        currentSelectedIndex = -1;
    }

    public interface FriendAdapterListener
    {
        void onIconClicked(int position);

        void onIconImportantClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }
}