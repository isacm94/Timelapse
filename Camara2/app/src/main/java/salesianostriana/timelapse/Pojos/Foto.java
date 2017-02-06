package salesianostriana.timelapse.Pojos;

/**
 * Created by Isabel on 06/02/2017.
 */

public class Foto {
    Long id;
    String path;
    Long fecha;
    Double bateria;
    int subida;

    /*CONSTRUCTORES*/
    public Foto() {
    }

    public Foto(String path, Long fecha, Double bateria, int subida) {
        this.path = path;
        this.fecha = fecha;
        this.bateria = bateria;
        this.subida = subida;
    }

    public Foto(Long id, String path, Long fecha, Double bateria, int subida) {
        this.id = id;
        this.path = path;
        this.fecha = fecha;
        this.bateria = bateria;
        this.subida = subida;
    }

    /*GETTERS & SETTERS*/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    public Double getBateria() {
        return bateria;
    }

    public void setBateria(Double bateria) {
        this.bateria = bateria;
    }

    public int getSubida() {
        return subida;
    }

    public void setSubida(int subida) {
        this.subida = subida;
    }

    /*Metodos*/

    @Override
    public String toString() {
        return "Foto{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", fecha=" + fecha +
                ", bateria=" + bateria +
                ", subida=" + subida +
                '}';
    }
}
