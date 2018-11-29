package com.bitz.isaacbuitrago.bitz.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitz.isaacbuitrago.bitz.Database.FriendsData;
import com.bitz.isaacbuitrago.bitz.R;

public class SendBitActivity extends AppCompatActivity
{

    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_bit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.friendsRecycleViewer);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FriendsData friends = new FriendsData();

        friends.fetch();

    }

    /**
     * Adapter and ViewHolder for representing a list of friends
     */

    public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendsViewHolder>
    {

        public class FriendsViewHolder extends RecyclerView.ViewHolder
        {
            View mView;

            public FriendsViewHolder(@NonNull View itemView)
            {
                super(itemView);

                mView = itemView;
            }
        }

        public FriendAdapter(View view)
        {


        }

        // Create new views (invoked by the layout manager)
        @Override
        public FriendAdapter.FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            // create a new view
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_text_view, parent, false);

            FriendsViewHolder vh = new FriendsViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(FriendsViewHolder holder, int position)
        {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mTextView.setText(mDataset[position]);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount()
        {
            return mDataset.length;
        }


    }

}
