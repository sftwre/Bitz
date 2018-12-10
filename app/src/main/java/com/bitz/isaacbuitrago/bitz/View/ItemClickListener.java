package com.bitz.isaacbuitrago.bitz.View;

import android.view.View;

/**
 * Defines the functionality to implement
 * when an Item in a recycler view is clicked.
 *
 * @author isaacbuitrago
 */
public interface ItemClickListener
{
    void onItemRowClicked(View view, int position);
}
