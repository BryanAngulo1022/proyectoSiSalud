package Roles;

import AdministradorCRUD.*;
import Conexion.ConexionBaseDatos;
import imagenes.FondoPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Administrador extends JFrame {
    private JPanel administradorPanel;
    private JTabbedPane tabbedPane1;
    private JButton cerrarSesionButton;
    private JButton PacientesButton;
    private JButton verReporteButton;
    private JComboBox especialidadCombo;
    private JLabel nombreAdmCargar;
    private JButton DoctoresButton;
    private JPanel gestionPanel;
    private JPanel reportePanel;
    private JTable reporteEspecialidadTable;
    private JScrollPane tablaReporte;
    private JTable reporteGeneralTable;
    private JButton verReporteButton1;

    public Administrador(String nombreAdmin) {
        setTitle("Panel del Administrador- SISALUD");
        setSize(600, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Creamos el panel de fondo
        FondoPanel fondo = new FondoPanel();
        fondo.setImagen("/imagenes/login.jpg");

        // Hacemos transparente el panel diseñado
        administradorPanel.setOpaque(false);

        // Añadimos el loginPanel (con los botones creados en el diseñador) al panel de fondo
        fondo.add(administradorPanel);

        // Establecemos el fondo como contentPane
        setContentPane(fondo);

        setResizable(false);
        setVisible(true);

        // Mostrar el nombre del usuario que inicia sesión
        nombreAdmCargar.setText("Bienvenido, " + nombreAdmin);

        //Regresar a iniciar sesion
        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new InicioSesionForm();

            }
        });



        //Panel de pacientes
        PacientesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new pacientesCRUD();

            }
        });
        //Panel de Doctores
        DoctoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new doctoresCRUD();
            }
        });






        //Panel de Reportes

        tabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = tabbedPane1.getSelectedIndex();

                if (index == 1) { // Pestaña 1 (segunda, índice 0 es la primera)
                    cargarEspecialidades();
                }
            }
        });
        verReporteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    generarReporte();
            }
        });
        verReporteButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarReporteGeneral();
            }
        });
    }




    private void cargarEspecialidades() {
        try (Connection conn = ConexionBaseDatos.conectar()) {
            String sql = "SELECT nombre FROM especialidad";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Limpiar los ComboBox primero
            especialidadCombo.removeAllItems();

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                especialidadCombo.addItem(nombre);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar especialidades: " + e.getMessage());
        }
    }

    private void generarReporte() {
        String especialidadSeleccionada = (String) especialidadCombo.getSelectedItem();

        if (especialidadSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una especialidad.");
            return;
        }

        try (Connection conn = ConexionBaseDatos.conectar()) {
            String sql = "SELECT d.nombre AS doctor, " +
                    "COUNT(c.id_cita) AS total_citas, " +
                    "SUM(CASE WHEN c.fecha < CURDATE() THEN 1 ELSE 0 END) AS citas_atendidas, " +
                    "SUM(CASE WHEN c.fecha >= CURDATE() THEN 1 ELSE 0 END) AS citas_pendientes " +
                    "FROM doctor d " +
                    "JOIN especialidad e ON d.id_especialidad = e.id_especialidad " +
                    "LEFT JOIN cita c ON d.id_doctor = c.id_doctor " +
                    "WHERE e.nombre = ? " +
                    "GROUP BY d.nombre";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, especialidadSeleccionada);
            ResultSet rs = stmt.executeQuery();

            // Columnas en el orden que pediste
            String[] columnas = {"Doctor", "Atendidas", "Pendientes", "Total"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

            int totalGeneral = 0;
            int totalAtendidas = 0;
            int totalPendientes = 0;

            while (rs.next()) {
                String doctor = rs.getString("doctor");
                int atendidas = rs.getInt("citas_atendidas");
                int pendientes = rs.getInt("citas_pendientes");
                int total = rs.getInt("total_citas");

                // Acumular totales
                totalAtendidas += atendidas;
                totalPendientes += pendientes;
                totalGeneral += total;

                modelo.addRow(new Object[]{doctor, atendidas, pendientes, total});
            }

            // Agregar la fila final con totales generales
            modelo.addRow(new Object[]{"TOTAL", totalAtendidas, totalPendientes, totalGeneral});

            // Asignar el modelo al JTable
            reporteEspecialidadTable.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage());
        }
    }


    private void generarReporteGeneral() {
        try (Connection conn = ConexionBaseDatos.conectar()) {
            String sql = "SELECT e.nombre AS especialidad, " +
                    "SUM(CASE WHEN c.fecha < CURDATE() THEN 1 ELSE 0 END) AS atendidas, " +
                    "SUM(CASE WHEN c.fecha >= CURDATE() THEN 1 ELSE 0 END) AS pendientes, " +
                    "COUNT(c.id_cita) AS total " +
                    "FROM especialidad e " +
                    "LEFT JOIN doctor d ON e.id_especialidad = d.id_especialidad " +
                    "LEFT JOIN cita c ON d.id_doctor = c.id_doctor " +
                    "GROUP BY e.nombre";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Columnas de la tabla
            String[] columnas = {"Especialidad", "Atendidas", "Pendientes", "Total"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

            int totalAtendidas = 0;
            int totalPendientes = 0;
            int totalGeneral = 0;

            while (rs.next()) {
                String especialidad = rs.getString("especialidad");
                int atendidas = rs.getInt("atendidas");
                int pendientes = rs.getInt("pendientes");
                int total = rs.getInt("total");

                // Acumular totales
                totalAtendidas += atendidas;
                totalPendientes += pendientes;
                totalGeneral += total;

                modelo.addRow(new Object[]{especialidad, atendidas, pendientes, total});
            }

            // Fila final con totales generales
            modelo.addRow(new Object[]{"TOTAL", totalAtendidas, totalPendientes, totalGeneral});

            // Asignar el modelo a la tabla general (debes tener un JTable en tu formulario)
            reporteGeneralTable.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte general: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        new Administrador("JUAN");
    }

}
