package salesianostriana.timelapse.Pojos;

/**
 * Created by Isabel on 12/02/2017.
 */

public class PreferenciaAPI {
    String token;
    String url;

    /*CONSTRUCTORES*/
    public PreferenciaAPI() {
    }

    public PreferenciaAPI(String token, String url) {
        this.token = token;
        this.url = url;
    }

    /*GETTERS & SETTERS*/
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /*MÉTODOS*/

    @Override
    public String toString() {
        return "PreferenciaAPI{" +
                "token='" + token + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
