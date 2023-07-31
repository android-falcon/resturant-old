package restaurant.apps.falcons.flaconsrestaurant.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import restaurant.apps.falcons.flaconsrestaurant.R;
import restaurant.apps.falcons.flaconsrestaurant.activities.InvoiceActivity;
import restaurant.apps.falcons.flaconsrestaurant.models.*;

import java.util.List;
import java.util.Locale;

/**
 * Created by pure_ on 01/08/2016.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private List<Item> items;
    private Context context;
    private int selectedIndex = -1;
    private int selectedChildIndex = -1;
    private InvoiceActivity invoice;

    public int getSelectedChildIndex() {
        return selectedChildIndex;
    }

    public void setSelectedChildIndex(int selectedChildIndex) {
        this.selectedChildIndex = selectedChildIndex;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public ExpandableListAdapter(InvoiceActivity invoice, Context context) {
        this.items = InvoiceActivity.currentTable[0].getItems();
        this.invoice = invoice;
        this.context = context;
    }


    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public int getChildrenCount(int i) {
        List<Modifier> modifiers = items.get(i).getModifiers();
        List<Answer> answers = items.get(i).getAnswers();
        return modifiers.size() + answers.size();
    }

    @Override
    public Object getGroup(int i) {
        return items.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.items_listview_item_view, null);
        }
        final Item item = items.get(i);

        TextView tv1 = (TextView) view.findViewById(R.id.text1);
        tv1.setText(item.getName());

        TextView tv2 = (TextView) view.findViewById(R.id.text2);
        tv2.setText(String.format(Locale.ENGLISH, "%.3f", (item.getQty() * item.getPrice())));

        TextView tv3 = (TextView) view.findViewById(R.id.text3);

        TextView itemNotes = (TextView) view.findViewById(R.id.item_notes);
        if (items.get(i).getNotes() != null && !items.get(i).getNotes().equals("null") && !items.get(i).getNotes().equals("")) {
            itemNotes.setVisibility(View.VISIBLE);
            itemNotes.setText(items.get(i).getNotes());
        } else {
            itemNotes.setVisibility(View.GONE);
        }

        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.isOld()) {
                    Toast.makeText(context, "Not Allowed", Toast.LENGTH_LONG).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.set_quantity);
                final EditText editText = new EditText(context);
                editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80));
                editText.setText(String.valueOf(item.getQty()));
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                builder.setView(editText);
                builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String value = editText.getText().toString();
                        if (value.length() == 0) {
                            Toast.makeText(context, R.string.msg_input_error, Toast.LENGTH_LONG).show();
                            return;
                        }
                        try {
                            float fValue = Float.valueOf(value);
                            if (fValue > 0) {
                                item.setQty(fValue);
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, R.string.msg_input_error, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        notifyDataSetChanged();
                        invoice.setTotals();
                    }
                });
                builder.setNegativeButton(context.getString(R.string.back), null);
                builder.show();

                editText.selectAll();

            }
        });
        tv3.setText(String.format(Locale.ENGLISH, "%.2f", item.getQty()));

        View v = view.findViewById(R.id.layout);
        if (selectedIndex == i && selectedChildIndex == -1) {
            v.setBackgroundResource(R.color.trans_black2);
        } else {
            v.setBackgroundResource(R.color.transperant);
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.items_listview_item_view, null);
        }

        TextView tv2 = (TextView) view.findViewById(R.id.text2);


        TextView tv3 = (TextView) view.findViewById(R.id.text3);


        TextView tv1 = (TextView) view.findViewById(R.id.text1);
        tv1.setBackgroundResource(R.color.transperant);

        TextView itemNotes = (TextView) view.findViewById(R.id.item_notes);
        itemNotes.setVisibility(View.GONE);

        if (i1 < items.get(i).getModifiers().size()) {
            Modifier modifier = items.get(i).getModifiers().get(i1);
            tv1.setText(String.format(" < %s > %s", modifier.getType(), modifier.getDesc()));
            //tv2.getLayoutParams().width = 0;
            //tv3.getLayoutParams().width = 0;
            tv3.getLayoutParams().height = 45;
            tv2.getLayoutParams().height = 45;
            tv2.setText("");
            tv3.setText("");
        } else {
            Item item = items.get(i);
            Answer answer = item.getAnswers().get(i1 - items.get(i).getModifiers().size());
            if (answer.getqId().startsWith("F*")) {
                SubItem subItem = answer.getItem();
                float price = subItem.useNewPrice() ? subItem.getNewPrice() : subItem.getPrice();
                tv1.setText(subItem.getName());
                tv2.setText(String.format(Locale.ENGLISH, "%.3f", (item.getQty() * price)));
                tv3.setText(String.format(Locale.ENGLISH, "%.2f", item.getQty()));
                tv3.getLayoutParams().height = 50;
                tv2.getLayoutParams().height = 50;
                //  tv2.getLayoutParams().width = 120;
                //  tv3.getLayoutParams().width = 120;
            } else {
                tv1.setText(answer.getDesc());
                //tv2.getLayoutParams().width = 0;
                //tv3.getLayoutParams().width = 0;
                tv3.getLayoutParams().height = 45;
                tv2.getLayoutParams().height = 45;
                tv2.setText("");
                tv3.setText("");
            }
        }

        tv1.setTextColor(Color.RED);
        tv1.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        tv1.setGravity(Gravity.LEFT);

        View v = view.findViewById(R.id.layout);
        if (selectedIndex == i && selectedChildIndex == i1) {
            v.setBackgroundResource(R.color.trans_black2);
        } else {
            v.setBackgroundResource(R.color.transperant);
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
