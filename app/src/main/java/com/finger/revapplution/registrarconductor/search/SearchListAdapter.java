package com.finger.revapplution.registrarconductor.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.ajithvgiri.searchdialog.R.layout;
import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends ArrayAdapter {
    public static String TAG = "SearchListAdapter";
    public List<SearchListItem> searchListItems;
    List<SearchListItem> suggestions = new ArrayList();
    SearchListAdapter.CustomFilter filter = new SearchListAdapter.CustomFilter();
    LayoutInflater inflater;
    private int textviewResourceID;

    public SearchListAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
        this.searchListItems = objects;
        this.textviewResourceID = textViewResourceId;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.searchListItems.size();
    }

    public Object getItem(int i) {
        return ((SearchListItem)this.searchListItems.get(i)).getTitle();
    }

    public long getItemId(int i) {
        return (long)((SearchListItem)this.searchListItems.get(i)).getId();
    }

    public int getposition(int id) {
        int position = 0;

        for(int i = 0; i < this.searchListItems.size(); ++i) {
            if(id == ((SearchListItem)this.searchListItems.get(i)).getId()) {
                position = i;
            }
        }

        return position;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflateview = view;
        if(view == null) {
            inflateview = this.inflater.inflate(layout.items_view_layout, (ViewGroup)null);
        }

        TextView tv = (TextView)inflateview.findViewById(this.textviewResourceID);
        tv.setText(((SearchListItem)this.searchListItems.get(i)).getTitle());
        return inflateview;
    }

    public Filter getFilter() {
        return this.filter;
    }

    private class CustomFilter extends Filter {
        private CustomFilter() {
        }

        protected FilterResults performFiltering(CharSequence constraint) {
            SearchListAdapter.this.suggestions.clear();
            if(SearchListAdapter.this.searchListItems != null && constraint != null) {
                for(int i = 0; i < SearchListAdapter.this.searchListItems.size(); ++i) {
                    if(((SearchListItem)SearchListAdapter.this.searchListItems.get(i)).getTitle().toLowerCase().contains(constraint)) {
                        SearchListAdapter.this.suggestions.add(SearchListAdapter.this.searchListItems.get(i));
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = SearchListAdapter.this.suggestions;
            results.count = SearchListAdapter.this.suggestions.size();
            return results;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.count > 0) {
                SearchListAdapter.this.notifyDataSetChanged();
            } else {
                SearchListAdapter.this.notifyDataSetInvalidated();
            }

        }
    }
}
