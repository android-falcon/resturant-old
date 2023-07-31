package restaurant.apps.falcons.flaconsrestaurant.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.models.Review;

/**
 * Created by Salah on 7/5/17.
 */

public class ReviewsAdapter extends BaseAdapter {

    private List<Review> reviews;
    private static LayoutInflater inflater = null;

    public ReviewsAdapter(Context context, List<Review> reviews) {
        // TODO Auto-generated constructor stub
        this.reviews = reviews;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return reviews.size();
    }

    public Object getItem(int position) {
        return null;
    }

    private class Holder {
        TextView tableId;
        TextView date;
        TextView value;
    }

    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    public View getView(final int position, View convertView, ViewGroup parent) {

        ReviewsAdapter.Holder holder = new ReviewsAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.reviews_list_view_item, null);

        holder.tableId = (TextView) rowView.findViewById(R.id.table_id);
        holder.date = (TextView) rowView.findViewById(R.id.date);

        holder.tableId.setText("Table: " + reviews.get(position).getTableId());
        holder.date.setText(reviews.get(position).getDate());

        holder.value = (TextView) rowView.findViewById(R.id.review_text);
        holder.value.setText(reviews.get(position).getNote());

        return rowView;
    }
}