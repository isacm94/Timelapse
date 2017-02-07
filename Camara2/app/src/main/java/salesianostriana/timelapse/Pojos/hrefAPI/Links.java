
package salesianostriana.timelapse.Pojos.hrefAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links {

    @SerializedName("self")
    @Expose
    private Self self;
    @SerializedName("proyecto")
    @Expose
    private Proyecto_ proyecto;
    @SerializedName("listaGps")
    @Expose
    private ListaGps listaGps;
    @SerializedName("listaTimelapse")
    @Expose
    private ListaTimelapse listaTimelapse;
    @SerializedName("listaDatosSensores")
    @Expose
    private ListaDatosSensores listaDatosSensores;
    @SerializedName("organizacion")
    @Expose
    private Organizacion organizacion;

    public Self getSelf() {
        return self;
    }

    public void setSelf(Self self) {
        this.self = self;
    }

    public Proyecto_ getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto_ proyecto) {
        this.proyecto = proyecto;
    }

    public ListaGps getListaGps() {
        return listaGps;
    }

    public void setListaGps(ListaGps listaGps) {
        this.listaGps = listaGps;
    }

    public ListaTimelapse getListaTimelapse() {
        return listaTimelapse;
    }

    public void setListaTimelapse(ListaTimelapse listaTimelapse) {
        this.listaTimelapse = listaTimelapse;
    }

    public ListaDatosSensores getListaDatosSensores() {
        return listaDatosSensores;
    }

    public void setListaDatosSensores(ListaDatosSensores listaDatosSensores) {
        this.listaDatosSensores = listaDatosSensores;
    }

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }

}
