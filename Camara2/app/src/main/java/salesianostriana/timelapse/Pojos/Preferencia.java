package salesianostriana.timelapse.Pojos;

/**
 * Created by Isabel on 30/01/2017.
 */

public class Preferencia {
    private String bateria;
    private String calidad;
    private String memoria;
    private String frecuencia;

    /*CONSTRUCTORES*/
    public Preferencia() {
    }

    public Preferencia(String bateria, String calidad, String memoria, String frecuencia) {
        this.bateria = bateria;
        this.calidad = calidad;
        this.memoria = memoria;
        this.frecuencia = frecuencia;
    }

    /*GETTERS & SETTERS*/
    public String getBateria() {
        return bateria;
    }

    public void setBateria(String bateria) {
        this.bateria = bateria;
    }

    public String getCalidad() {
        return calidad;
    }

    public void setCalidad(String calidad) {
        this.calidad = calidad;
    }

    public String getMemoria() {
        return memoria;
    }

    public void setMemoria(String memoria) {
        this.memoria = memoria;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    /*MÉTODOS*/

    @Override
    public String toString() {
        return "Preferencia{" +
                "bateria='" + bateria + '\'' +
                ", calidad='" + calidad + '\'' +
                ", memoria='" + memoria + '\'' +
                ", frecuencia='" + frecuencia + '\'' +
                '}';
    }
}
