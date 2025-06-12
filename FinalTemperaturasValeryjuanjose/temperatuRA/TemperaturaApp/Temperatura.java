package TemperaturaApp;

public class Temperatura {
    private String ciudad;
    private String fecha; // formato dd/MM/yyyy
    private double temperatura;

    public Temperatura(String ciudad, String fecha, double temperatura) {
        this.ciudad = ciudad;
        this.fecha = fecha;
        this.temperatura = temperatura;
    }

    public String getCiudad() { return ciudad; }
    public String getFecha() { return fecha; }
    public double getTemperatura() { return temperatura; }

    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }

    @Override
    public String toString() {
        return ciudad + ", " + fecha + ", " + temperatura + "°C";
    }
}
