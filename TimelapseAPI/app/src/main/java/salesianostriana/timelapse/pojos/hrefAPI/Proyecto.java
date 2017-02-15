
package salesianostriana.timelapse.pojos.hrefAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Proyecto {

    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("localidad")
    @Expose
    private String localidad;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("_links")
    @Expose
    private Links links;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

}
