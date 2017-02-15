package salesianostriana.timelapse.pojos;

/**
 * Created by Isabel on 06/02/2017.
 */

public class Foto {
    Long id;
    String nombre;
    Long fechaMilisegundos;
    Double bateria;
    int subida;

    /*CONSTRUCTORES*/
    public Foto() {
    }

    public Foto(String nombre, Long fechaMilisegundos, Double bateria, int subida) {
        this.nombre = nombre;
        this.fechaMilisegundos = fechaMilisegundos;
        this.bateria = bateria;
        this.subida = subida;
    }

    public Foto(Long id, String nombre, Long fechaMilisegundos, Double bateria, int subida) {
        this.id = id;
        this.nombre = nombre;
        this.fechaMilisegundos = fechaMilisegundos;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String path) {
        this.nombre = path;
    }

    public Long getFechaMilisegundos() {
        return fechaMilisegundos;
    }

    public void setFechaMilisegundos(Long fechaMilisegundos) {
        this.fechaMilisegundos = fechaMilisegundos;
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
                ", nombre='" + nombre + '\'' +
                ", fechaMilisegundos=" + fechaMilisegundos +
                ", bateria=" + bateria +
                ", subida=" + subida +
                '}';
    }
}
