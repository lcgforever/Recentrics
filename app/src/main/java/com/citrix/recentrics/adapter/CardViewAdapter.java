package com.citrix.recentrics.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;

import com.citrix.recentrics.R;

import java.util.List;

/**
 * A simple adapter that loads a CardView layout with one TextView and two Buttons, and
 * listens to clicks on the Buttons or on the CardView
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
    private List<String> cards;
    private OnItemTouchListener onItemTouchListener;

    public CardViewAdapter(List<String> cards, OnItemTouchListener onItemTouchListener) {
        this.cards = cards;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_layout, viewGroup, false);
//        return new ViewHolder(v);
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.title.setText(cards.get(i));
    }

    @Override
    public int getItemCount() {
        return cards == null ? 0 : cards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private Button button1;
        private Button button2;

        public ViewHolder(View itemView) {
            super(itemView);
//            title = (TextView) itemView.findViewById(R.id.card_view_title);
//            button1 = (Button) itemView.findViewById(R.id.card_view_button1);
//            button2 = (Button) itemView.findViewById(R.id.card_view_button2);

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    onItemTouchListener.onButton1Click(v, getPosition());
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    onItemTouchListener.onButton2Click(v, getPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    onItemTouchListener.onCardViewTap(v, getPosition());
                }
            });
        }
    }
}
