package inc.mesa.githubuser.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.List;

import inc.mesa.githubuser.R;
import inc.mesa.githubuser.adapters.Adapter;
import inc.mesa.githubuser.model.User;
import inc.mesa.githubuser.utils.GetUser;
import inc.mesa.githubuser.utils.TaskDelegate;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;


public class MainActivity extends AppCompatActivity implements TaskDelegate {


    static {
        System.loadLibrary("string-android-jni");
    }

    private Adapter adapter;
    private Realm realm;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private List<User> list = new ArrayList<>();
    private SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String query) {
            list = realm.where(User.class).contains("login", query.toLowerCase()).findAll();
            adapter.notifyDataSetChanged();
            return true;

        }

        public boolean onQueryTextSubmit(String query) {

            if (list.size() == 0) {
                list = realm.allObjectsSorted(User.class, "id", Sort.DESCENDING);
                adapter.notifyDataSetChanged();
                GetUser asyncGetUser = new GetUser(MainActivity.this);
                asyncGetUser.execute(query);
                Snackbar.make(recyclerView, R.string.snack, Snackbar.LENGTH_SHORT)
                        .show();
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        //toolbar for custom layout and search
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = (TextView) findViewById(R.id.empty_view);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext())
                .deleteRealmIfMigrationNeeded().build();
        realm = Realm.getInstance(realmConfig);
        list = realm.allObjectsSorted(User.class, "id", Sort.DESCENDING);
        adapter = new Adapter(list);
        recyclerView.setAdapter(adapter);
        showEmptyText();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(listener);
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void showEmptyText() {
        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }


    @Override
    public void taskCompletionResult() {
        list = realm.allObjectsSorted(User.class, "id", Sort.DESCENDING);
        adapter.notifyDataSetChanged();
        //the list can be empty
        showEmptyText();

    }
}
