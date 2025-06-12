package TemperaturaApp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.ChartUtilities;
import org.jfree.data.category.DefaultCategoryDataset;
import java.io.File;

public class VentanaPrincipal extends JFrame {
    private TemperaturaManager manager;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public VentanaPrincipal() {
        super("Aplicación de Temperaturas");
        manager = new TemperaturaManager();

        // Configuración de ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLayout(new BorderLayout());

        // Modelo y tabla
        modeloTabla = new DefaultTableModel(new String[]{"Ciudad", "Fecha", "Temperatura"}, 0);
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel();

        JButton btnCargar = new JButton("Cargar CSV");
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnGraficar = new JButton("Gráfica de promedios");
        JButton btnGuardarGrafica = new JButton("Guardar gráfica");

        panelBotones.add(btnCargar);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnGraficar);
        panelBotones.add(btnGuardarGrafica);

        add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnCargar.addActionListener(e -> cargarDatos());

        btnAgregar.addActionListener(e -> {
            String ciudad = JOptionPane.showInputDialog(this, "Ciudad:");
            String fecha = JOptionPane.showInputDialog(this, "Fecha (dd/MM/yyyy):");
            String tempStr = JOptionPane.showInputDialog(this, "Temperatura:");

            if (!validarFecha(fecha)) {
                JOptionPane.showMessageDialog(this, "Fecha inválida. Usa el formato dd/MM/yyyy");
                return;
            }

            try {
                double temp = Double.parseDouble(tempStr);
                manager.agregar(ciudad, fecha, temp);
                modeloTabla.addRow(new Object[]{ciudad, fecha, temp});
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Temperatura inválida.");
            }
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                manager.eliminar(fila);
                modeloTabla.removeRow(fila);
            }
        });

        btnGraficar.addActionListener(e -> mostrarGrafica(false));
        btnGuardarGrafica.addActionListener(e -> mostrarGrafica(true));

        setVisible(true);
    }

    private void cargarDatos() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona un archivo CSV");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            manager.cargarDesdeCSV(archivo.getAbsolutePath());
            modeloTabla.setRowCount(0);
            for (Temperatura t : manager.getDatos()) {
                modeloTabla.addRow(new Object[]{t.getCiudad(), t.getFecha(), t.getTemperatura()});
            }
        }
    }

    private boolean validarFecha(String fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(fecha);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void mostrarGrafica(boolean guardarComoImagen) {
        String fechaIni = JOptionPane.showInputDialog(this, "Fecha inicio (dd/MM/yyyy):");
        String fechaFin = JOptionPane.showInputDialog(this, "Fecha fin (dd/MM/yyyy):");

        if (!validarFecha(fechaIni) || !validarFecha(fechaFin)) {
            JOptionPane.showMessageDialog(this, "Fechas inválidas. Usa dd/MM/yyyy");
            return;
        }

        Map<String, Double> promedios = manager.obtenerPromediosPorCiudad(fechaIni, fechaFin);
        if (promedios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos en ese rango.");
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : promedios.entrySet()) {
            dataset.addValue(entry.getValue(), "Temperatura", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Promedios de temperatura",
                "Ciudad",
                "°C",
                dataset
        );

        if (guardarComoImagen) {
            try {
                ChartUtilities.saveChartAsPNG(new File("grafica_promedios.png"), chart, 800, 600);
                JOptionPane.showMessageDialog(this, "Gráfica guardada como grafica_promedios.png");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar la imagen: " + ex.getMessage());
            }
        } else {
            ChartPanel panel = new ChartPanel(chart);
            JFrame ventanaGrafica = new JFrame("Gráfica");
            ventanaGrafica.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ventanaGrafica.setSize(600, 400);
            ventanaGrafica.add(panel);
            ventanaGrafica.setVisible(true);
        }
    }
}
