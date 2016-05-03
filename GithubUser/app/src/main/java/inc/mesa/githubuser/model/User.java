package inc.mesa.githubuser.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Italo on 02/05/16.
 */
public class User extends RealmObject {
    @PrimaryKey
    private int id;
    private String html_url;
    private String login;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return html_url;
    }

    public void setUrl(String url) {
        this.html_url = url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
