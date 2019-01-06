package com.bitz.isaacbuitrago.bitz.View;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bitz.isaacbuitrago.bitz.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Custom adapter that displays an Image View for each entry
 *
 * @author isaacbuitrago
 */
public class ArrayImageAdapter extends ArrayAdapter
{

    private Context mContext;

    private List<Integer> logoList;     // list of resource identifiers for a drawable

    private View.OnClickListener listener;

    public ArrayImageAdapter(@NonNull Context context, int resource,
                             @LayoutRes ArrayList<Integer> objects,
                             View.OnClickListener listener)
    {
        super(context, resource, objects);

        this.mContext = context;

        this.logoList = objects;

        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View listItem = convertView;

        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.service_list_row, parent, false);

        ImageView imageView = listItem.findViewById(R.id.serviceImageView);

        imageView.setImageResource(logoList.get(position));

        CheckBox checkBox = listItem.findViewById(R.id.checkBox);

        checkBox.setOnClickListener(listener);

        return listItem;
    }

}
