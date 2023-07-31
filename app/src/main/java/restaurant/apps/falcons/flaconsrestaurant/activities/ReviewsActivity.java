package restaurant.apps.falcons.flaconsrestaurant.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.adapters.ReviewsAdapter;
import restaurant.apps.falcons.flaconsrestaurant.models.Review;
import restaurant.apps.falcons.flaconsrestaurant.util.DataManager;

/**
 * Created by Salah on 7/5/17.
 */

public class ReviewsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviews_activity);

        (new GetReviews()).execute();
    }

    private class GetReviews extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return DataManager.getData("select TABLE_ID,NOTES,MDATE from WAITERHEADER");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                List<Review> reviews = new ArrayList<>();

                JSONArray resultArr = new JSONArray(result);
                for (int i = 0; i < resultArr.length(); i++) {

                    JSONObject object = resultArr.getJSONObject(i);

                    Review review = new Review();
                    review.setTableId(object.getString("TABLE_ID"));

                    String date = object.getString("MDATE");
                    review.setDate(date.substring(0, date.length() - 10) + " " + date.substring(date.length() - 2));

                    String notes = object.getString("NOTES");
                    review.setNote(notes);

                    if (notes != null && !notes.equals("null") && !notes.equals("")) {
                        reviews.add(review);
                    }
                }

                ListView lv = (ListView) findViewById(R.id.reviewsLv);
                ReviewsAdapter reviewsAdapter = new ReviewsAdapter(ReviewsActivity.this, reviews);
                lv.setAdapter(reviewsAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}