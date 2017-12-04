package com.softdesign.devintensive.ui.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.softdesign.devintensive.R;

import java.util.List;

public class RepositoriesAdapter extends BaseAdapter
{
    private List<String> repoList;
    private Context context;
    private LayoutInflater layoutInflater;

    public RepositoriesAdapter(Context context, List<String> repoList)
    {
        this.repoList = repoList;
        this.context = context;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return repoList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return repoList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View itemView = convertView;

        if (itemView == null)
        {
            itemView = layoutInflater.inflate(R.layout.item_repositories_list, parent, false);
        }

        TextView repoName = (TextView) itemView.findViewById(R.id.repositorii_et);
        repoName.setText(repoList.get(position));

        return null;
    }
}
