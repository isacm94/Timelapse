
package salesianostriana.timelapselocal.pojos.hrefAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links {

    @SerializedName("self")
    @Expose
    private Self self;
    @SerializedName("proyecto")
    @Expose
    private Proyecto_ proyecto;
    @SerializedName("datos_sensores")
    @Expose
    private DatosSensores datosSensores;
    @SerializedName("timelapse")
    @Expose
    private Timelapse timelapse;
    @SerializedName("gps")
    @Expose
    private Gps gps;
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

    public DatosSensores getDatosSensores() {
        return datosSensores;
    }

    public void setDatosSensores(DatosSensores datosSensores) {
        this.datosSensores = datosSensores;
    }

    public Timelapse getTimelapse() {
        return timelapse;
    }

    public void setTimelapse(Timelapse timelapse) {
        this.timelapse = timelapse;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }

}
