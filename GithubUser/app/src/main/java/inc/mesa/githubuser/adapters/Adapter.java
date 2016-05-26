package inc.mesa.githubuser.adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.List;

import inc.mesa.githubuser.R;
import inc.mesa.githubuser.model.User;


public class Adapter extends
        RecyclerView.Adapter<Adapter.ViewHolder> {


    private List<User> mtList;

    public Adapter(List<User> list) {

        mtList = list;
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    public native int getHashFromJni(String url);

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        viewHolder.bindUrl(mtList.get(position).getUrl());
        viewHolder.name.setText(mtList.get(position).getLogin());
        viewHolder.url.setText(mtList.get(position).getUrl());
        viewHolder.hash.setText(String.valueOf(getHashFromJni(mtList.get(position).getUrl())));


    }


    @Override
    public int getItemCount() {
        return mtList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView url;
        TextView hash;
        private String urlToShare;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            name = (TextView) itemLayoutView.findViewById(R.id.name);
            url = (TextView) itemLayoutView.findViewById(R.id.url);
            hash = (TextView) itemLayoutView.findViewById(R.id.hash);
            itemLayoutView.setOnClickListener(this);


        }

        void bindUrl(String url) {
            urlToShare = url;
        }

        @Override
        public void onClick(View v) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(urlToShare))
                    .build();
            ShareDialog shareDialog = new ShareDialog((Activity) v.getContext());
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        }
    }

}