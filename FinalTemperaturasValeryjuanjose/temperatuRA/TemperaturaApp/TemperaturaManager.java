package TemperaturaApp;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TemperaturaManager {
    private List<Temperatura> datos;

    public TemperaturaManager() {
        datos = new ArrayList<>();
    }

    // Leer datos desde archivo CSV
    public void cargarDesdeCSV(String nombreArchivo) {
        datos.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            br.readLine(); // Saltar encabezado
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    String ciudad = partes[0];
                    String fecha = partes[1];
                    double temp = Double.parseDouble(partes[2]);
                    datos.add(new Temperatura(ciudad, fecha, temp));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    // Guardar datos en archivo CSV
    public void guardarCSV(String nombreArchivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            bw.write("Ciudad,Fecha,Temperatura\n");
            for (Temperatura t : datos) {
                bw.write(t.getCiudad() + "," + t.getFecha() + "," + t.getTemperatura() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
        }
    }

    // Agregar un nuevo registro
    public void agregar(String ciudad, String fecha, double temperatura) {
        datos.add(new Temperatura(ciudad, fecha, temperatura));
    }

    // Eliminar un registro por índice
    public void eliminar(int index) {
        if (index >= 0 && index < datos.size()) {
            datos.remove(index);
        }
    }

    // Obtener la lista completa de datos
    public List<Temperatura> getDatos() {
        return datos;
    }

    // Calcular promedio de temperaturas por ciudad entre dos fechas
    public Map<String, Double> obtenerPromediosPorCiudad(String fechaInicio, String fechaFin) {
        return datos.stream()
            .filter(t -> t.getFecha().compareTo(fechaInicio) >= 0 && t.getFecha().compareTo(fechaFin) <= 0)
            .collect(Collectors.groupingBy(
                Temperatura::getCiudad,
                Collectors.averagingDouble(Temperatura::getTemperatura)
            ));
    }
}
