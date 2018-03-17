package com.finger.revapplution.registrarconductor.search;

import android.app.Activity;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.ajithvgiri.searchdialog.R.id;
import com.ajithvgiri.searchdialog.R.layout;
import java.util.ArrayList;
import java.util.List;

public class SearchableDialog {
    private static final String TAG = "SearchableDialog";
    List<SearchListItem> searchListItems;
    Activity activity;
    String dTitle;
    OnSearchItemSelected onSearchItemSelected;
    AlertDialog alertDialog;
    int position;
    int style;
    SearchListItem searchListItem = null;
    SearchListAdapter searchListAdapter;
    ListView listView;

    public SearchableDialog(Activity activity, List<SearchListItem> searchListItems, String dialogTitle) {
        this.searchListItems = searchListItems;
        this.activity = activity;
        this.dTitle = dialogTitle;
    }

    public SearchableDialog(Activity activity, List<SearchListItem> searchListItems, String dialogTitle, int style) {
        this.searchListItems = searchListItems;
        this.activity = activity;
        this.dTitle = dialogTitle;
        this.style = style;
    }

    public void setOnItemSelected(OnSearchItemSelected searchItemSelected) {
        this.onSearchItemSelected = searchItemSelected;
    }

    public void show() {
        Builder adb = new Builder(this.activity);
        View view = this.activity.getLayoutInflater().inflate(layout.search_dialog_layout, (ViewGroup)null);
        TextView rippleViewClose = (TextView)view.findViewById(id.close);
        TextView title = (TextView)view.findViewById(id.spinerTitle);
        title.setText(this.dTitle);
        this.listView = (ListView)view.findViewById(id.list);
        final EditText searchBox = (EditText)view.findViewById(id.searchBox);
        this.searchListAdapter = new SearchListAdapter(this.activity, layout.items_view_layout, id.text1, this.searchListItems);
        this.listView.setAdapter(this.searchListAdapter);
        adb.setView(view);
        this.alertDialog = adb.create();
        this.alertDialog.getWindow().getAttributes().windowAnimations = this.style;
        this.alertDialog.setCancelable(false);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView t = (TextView)view.findViewById(id.text1);

                for(int j = 0; j < SearchableDialog.this.searchListItems.size(); ++j) {
                    if(t.getText().toString().equalsIgnoreCase(((SearchListItem)SearchableDialog.this.searchListItems.get(j)).toString())) {
                        SearchableDialog.this.position = j;
                        SearchableDialog.this.searchListItem = (SearchListItem)SearchableDialog.this.searchListItems.get(SearchableDialog.this.position);
                    }
                }

                try {
                    SearchableDialog.this.onSearchItemSelected.onClick(SearchableDialog.this.position, SearchableDialog.this.searchListItem);
                } catch (Exception var8) {
                    Log.e("SearchableDialog", var8.getMessage());
                }

                SearchableDialog.this.alertDialog.dismiss();
            }
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                List<SearchListItem> filteredValues = new ArrayList();

                for(int i = 0; i < SearchableDialog.this.searchListItems.size(); ++i) {
                    if(SearchableDialog.this.searchListItems.get(i) != null) {
                        SearchListItem item = (SearchListItem)SearchableDialog.this.searchListItems.get(i);
                        if(item.getTitle().toLowerCase().trim().contains(searchBox.getText().toString().toLowerCase().trim())) {
                            filteredValues.add(item);
                        }
                    }
                }

                SearchableDialog.this.searchListAdapter = new SearchListAdapter(SearchableDialog.this.activity, layout.items_view_layout, id.text1, filteredValues);
                SearchableDialog.this.listView.setAdapter(SearchableDialog.this.searchListAdapter);
            }
        });
        rippleViewClose.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SearchableDialog.this.alertDialog.dismiss();
            }
        });
        this.alertDialog.show();
    }

    public void clear() {
        this.searchListItems.clear();
    }

    public void refresh() {
        this.searchListAdapter.notifyDataSetChanged();
    }

    public SearchListAdapter getAdapter() {
        return this.searchListAdapter;
    }
}