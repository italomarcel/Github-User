package inc.mesa.githubuser.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import inc.mesa.githubuser.R;
import inc.mesa.githubuser.adapters.Adapter;
import inc.mesa.githubuser.model.User;
import inc.mesa.githubuser.utils.WebRequest;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;


public class MainActivity extends AppCompatActivity {

    private static final String TAG_ITEMS = "items";
    private static final String BASE_URL = "https://api.github.com/search/users?q=";
    public SearchView search;
    boolean found = false;
    private Adapter mAdapter;
    private Realm realm;
    private RecyclerView mRecyclerView;
    private TextView emptyView;

    private RealmConfiguration realmConfig;
    private List<User> list = new ArrayList<>();
    private SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String query) {
            query = query.toLowerCase();

            final List<User> filteredList = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {

                final String text = list.get(i).getLogin().toLowerCase();
                if (text.contains(query)) {
                    filteredList.add(list.get(i));
                    found = true;
                } else {
                    found = false;
                }
            }

            mAdapter = new Adapter(filteredList, MainActivity.this);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            return true;

        }

        public boolean onQueryTextSubmit(String query) {

            if (!found) {
                mAdapter = new Adapter(list, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                new GetUser().execute(query);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        search = (SearchView) findViewById(R.id.search);
        search.setOnQueryTextListener(listener);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        emptyView = (TextView) findViewById(R.id.empty_view);
        realmConfig = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
        realm = Realm.getInstance(realmConfig);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = realm.allObjectsSorted(User.class, "id", Sort.DESCENDING);
        mAdapter = new Adapter(list, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        showEmptyText();
    }

    @WorkerThread
    private void ParseJSON(String json) {
        if (json != null) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray jsonArray = jsonObj.getJSONArray(TAG_ITEMS);
                Realm realm = Realm.getInstance(realmConfig);
                realm.beginTransaction();
                realm.createOrUpdateObjectFromJson(User.class, jsonArray.getJSONObject(0));
                realm.commitTransaction();
                realm.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void showEmptyText() {
        if (list.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }


    private class GetUser extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Executing on background...", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(String... arg0) {
            WebRequest webreq = new WebRequest();
            String name = arg0[0];

            if (!TextUtils.isEmpty(name)) {
                String jsonStr = webreq.makeWebServiceCall(BASE_URL, name, WebRequest.GET);
                ParseJSON(jsonStr);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            list = realm.allObjectsSorted(User.class, "id", Sort.DESCENDING);
            mAdapter = new Adapter(list, getApplicationContext());
            mRecyclerView.setAdapter(mAdapter);
            showEmptyText();


        }

    }

}
