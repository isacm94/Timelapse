package salesianostriana.timelapse.pojos.API;

/**
 * Created by Isabel on 12/02/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FotoInfo {

    @SerializedName("fecha_captura")
    @Expose
    private Long fecha_captura;

    @SerializedName("fecha_subida")
    @Expose
    private String fecha_subida;

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

    public FotoInfo(Long fecha_captura, String fecha_subida, String foto, Double estado_bateria, String proyecto) {
        this.fecha_captura = fecha_captura;
        this.fecha_subida = fecha_subida;
        this.foto = foto;
        this.estado_bateria = estado_bateria;
        this.proyecto = proyecto;
    }

    /* GETTERS & SETTERS*/
    public Long getFechaCaptura() {
        return fecha_captura;
    }

    public void setFechaCaptura(Long fecha) {
        this.fecha_captura = fecha;
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

    public Long getFecha_captura() {
        return fecha_captura;
    }

    public void setFecha_captura(Long fecha_captura) {
        this.fecha_captura = fecha_captura;
    }

    public String getFecha_subida() {
        return fecha_subida;
    }

    public void setFecha_subida(String fecha_subida) {
        this.fecha_subida = fecha_subida;
    }
}