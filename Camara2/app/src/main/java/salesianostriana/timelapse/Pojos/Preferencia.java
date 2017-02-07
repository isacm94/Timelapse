package salesianostriana.timelapse.Pojos;

import static java.lang.Integer.parseInt;

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
    public int getBateria() {
        if (bateria.equals("")) {
            bateria = "0";
        }
        return parseInt(bateria);
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

    public int getMemoria() {
        if(memoria.equals("")){
            memoria = "0";
        }
        return Integer.parseInt(memoria);
    }

    public void setMemoria(String memoria) {
        this.memoria = memoria;
    }

    public int getFrecuencia() {
        return parseInt(frecuencia);
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
