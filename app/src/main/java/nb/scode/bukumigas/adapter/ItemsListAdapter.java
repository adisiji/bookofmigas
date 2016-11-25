package nb.scode.bukumigas.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import nb.scode.bukumigas.R;
import nb.scode.bukumigas.model.Item;

public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.MyViewHolder> {

    private Context mContext;
    private List<Item> itemList;
    private likes like;
    private cardact cardAct;

    public void setLikesInterface(likes like){
        this.like = like;
    }

    public void setCardAct (cardact act){
        this.cardAct = act;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title, date;
        public ImageView thumbnail, mItemLike;
        public CardView cardView;

        public MyViewHolder(View view) {

            super(view);
            mItemLike = (ImageView)view.findViewById(R.id.itemLike) ;
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            cardView = (CardView)view.findViewById(R.id.card_view);
        }
    }


    public ItemsListAdapter(Context mContext, List<Item> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Item item = itemList.get(position);

        holder.title.setText(item.getTitle());
        holder.date.setText(item.getDate());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardAct.onClick(view, position);
            }
        });

        holder.mItemLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like.onClick(view, position);
            }
        });

        if (item.isMyLike()) {

            holder.mItemLike.setImageResource(R.drawable.perk_active);

        } else {

            holder.mItemLike.setImageResource(R.drawable.perk);
        }

        // loading album cover using Glide library
        Glide.with(mContext).load(item.getImgUrl()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {

        return itemList.size();
    }

    public interface likes{
        void onClick(View view, int position);
    }

    public interface cardact{
        void onClick(View view, int position);
    }
}