package salesianostriana.timelapse.Pojos.API;

/**
 * Created by Isabel on 12/02/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FotoInfo {

    @SerializedName("fecha")
    @Expose
    private Long fecha;

    @SerializedName("foto")
    @Expose
    private String foto;

    @SerializedName("estado_bateria")
    @Expose
    private Double estado_bateria;

    @SerializedName("proyecto")
    @Expose
    private String proyecto;

    /*CONSTRUCTORES*/
    public FotoInfo() {
    }

    public FotoInfo(Long fecha, String foto, Double estado_bateria, String proyecto) {
        this.fecha = fecha;
        this.foto = foto;
        this.estado_bateria = estado_bateria;
        this.proyecto = proyecto;
    }

    /* GETTERS & SETTERS*/
    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Double getEstado_bateria() {
        return estado_bateria;
    }

    public void setEstado_bateria(Double estado_bateria) {
        this.estado_bateria = estado_bateria;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }
}