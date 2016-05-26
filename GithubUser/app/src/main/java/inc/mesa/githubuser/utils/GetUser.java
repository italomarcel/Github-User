package inc.mesa.githubuser.utils;

import android.os.AsyncTask;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import inc.mesa.githubuser.model.User;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.facebook.FacebookSdk.getApplicationContext;

public class GetUser extends AsyncTask<String, Void, Void> {

    private static final String TAG_ITEMS = "items";
    private static final String BASE_URL = "https://api.github.com/search/users?q=";
    private TaskDelegate task;

    public GetUser(TaskDelegate task) {
        this.task = task;
    }

    @WorkerThread
    private void parseJSON(String json) {
        if (json != null) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray jsonArray = jsonObj.getJSONArray(TAG_ITEMS);
                RealmConfiguration realmConfig = new RealmConfiguration.Builder(
                        getApplicationContext()).deleteRealmIfMigrationNeeded().build();
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
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(String... arg0) {
        WebRequest webreq = new WebRequest();
        String name = arg0[0];
        if (!TextUtils.isEmpty(name)) {
            String jsonStr = webreq.makeWebServiceCall(BASE_URL, name, WebRequest.GET);
            parseJSON(jsonStr);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        task.taskCompletionResult();
    }

}