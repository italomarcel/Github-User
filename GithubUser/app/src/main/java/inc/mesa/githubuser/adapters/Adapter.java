package inc.mesa.githubuser.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import inc.mesa.githubuser.R;
import inc.mesa.githubuser.model.User;


public class Adapter extends
        RecyclerView.Adapter<Adapter.ViewHolder> {

    public Context mcontext;
    ViewHolder viewHolder;
    private List<User> mtList;

    public Adapter(List<User> list, Context context) {

        mtList = list;
        mcontext = context;
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, null);
        viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        viewHolder.name.setText(mtList.get(position).getLogin());
        viewHolder.url.setText(mtList.get(position).getUrl());

    }

    @Override
    public int getItemCount() {
        return mtList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView url;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            name = (TextView) itemLayoutView.findViewById(R.id.name);
            url = (TextView) itemLayoutView.findViewById(R.id.url);


        }
    }

}