
package salesianostriana.timelapselocal.pojos.hrefAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatosSensores {

    @SerializedName("href")
    @Expose
    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}