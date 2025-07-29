package Roles;

import AdministradorCRUD.*;
import Conexion.ConexionBaseDatos;
import Imagenes.FondoPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * Clase {@code Administrador} representa el panel principal para el rol Administrador
 * en el sistema SISALUD. Proporciona acceso a la gestión de pacientes, doctores y
 * generación de reportes.
 *
 * <p>Incluye las siguientes funcionalidades:</p>
 * <ul>
 *     <li>Abrir el módulo de gestión de pacientes.</li>
 *     <li>Abrir el módulo de gestión de doctores.</li>
 *     <li>Generar reportes por especialidad y reportes generales.</li>
 *     <li>Cerrar sesión y regresar al formulario de inicio de sesión.</li>
 * </ul>
 *
 * Esta clase utiliza {@link ConexionBaseDatos} para la conexión a la base de datos.
 *
 * @author Bryan
 * @version 1.0
 */
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
    private JButton limpiarButton;
    private JTextField cedulaBuscarHistField;
    private JButton buscarHistorialButton;
    private JButton verCitaButton;
    private JButton eliminarCitaButton;
    private JScrollPane historialScrollPanel;
    private JPanel listaCitasCargarPanel;
    private JTextArea datosCargarPanel;
    private JPanel citasCargarPanel;

    /**
     * Constructor que inicializa la interfaz gráfica para el rol Administrador.
     *
     * @param nombreAdmin Nombre del administrador que inició sesión.
     */

    public Administrador(String nombreAdmin) {
        setTitle("Panel del Administrador- SISALUD");
        setSize(600, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Creamos el panel de fondo
        FondoPanel fondo = new FondoPanel();
        fondo.setImagen("/Imagenes/login.jpg");

        // Hacemos transparente el panel diseñado
        administradorPanel.setOpaque(false);

        // Añadimos el loginPanel (con los botones creados en el diseñador) al panel de fondo
        fondo.add(administradorPanel);

        // Establecemos el fondo como contentPane
        setContentPane(fondo);

        setResizable(false);
        setVisible(true);

        // Mostrar el nombre del usuario que inicia sesión
        nombreAdmCargar.setText("Bienvenid@, " + nombreAdmin);

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

        //Panel de citas
        limpiarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Limpiar paneles
                datosCargarPanel.setText("");
                listaCitasCargarPanel.removeAll();
                listaCitasCargarPanel.revalidate();
                listaCitasCargarPanel.repaint();
            }
        });
        buscarHistorialButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarHistorialCitas();
            }
        });
        eliminarCitaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarCitas();
            }
        });
        verCitaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verCitas();

            }
        });
    }




    /**
     * Metodos para cargar especialidad, generar reportes por especialidad seleccionada,
     * y las añade al combo box {@code especialidadCombo}.
     */


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

    /**
     * Genera un reporte detallado por especialidad seleccionada,
     * mostrando el número de citas atendidas, pendientes y el total por doctor.
     *
     */
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
    /**
     * Genera un reporte general de todas las especialidades,
     * mostrando citas atendidas, pendientes y totales.
     *
     */

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

    private void buscarHistorialCitas() {
        String cedula = cedulaBuscarHistField.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese la cédula.");
            return;
        }

        datosCargarPanel.setText("");
        listaCitasCargarPanel.removeAll();
        listaCitasCargarPanel.revalidate();
        listaCitasCargarPanel.repaint();

        try (Connection conn = ConexionBaseDatos.conectar()) {
            String sqlPaciente = "SELECT nombre, fecha_nacimiento, telefono, correo FROM paciente WHERE ci = ?";
            PreparedStatement stmtPaciente = conn.prepareStatement(sqlPaciente);
            stmtPaciente.setString(1, cedula);
            ResultSet rsPaciente = stmtPaciente.executeQuery();

            if (rsPaciente.next()) {
                String datos = "Paciente: " + rsPaciente.getString("nombre") + "\n"
                        + "Nacimiento: " + rsPaciente.getString("fecha_nacimiento") + "\n"
                        + "Teléfono: " + rsPaciente.getString("telefono") + "\n"
                        + "Correo: " + rsPaciente.getString("correo");
                datosCargarPanel.setText(datos);
            } else {
                JOptionPane.showMessageDialog(null, "Paciente no encontrado.");
                return;
            }

            String sqlCitas = "SELECT c.id_cita, d.nombre AS doctor, c.fecha, c.hora " +
                    "FROM cita c " +
                    "JOIN doctor d ON c.id_doctor = d.id_doctor " +
                    "JOIN paciente p ON c.id_paciente = p.id_paciente " +
                    "WHERE p.ci = ? AND c.fecha >= CURDATE() " +
                    "ORDER BY c.fecha, c.hora";

            PreparedStatement stmtCitas = conn.prepareStatement(sqlCitas);
            stmtCitas.setString(1, cedula);
            ResultSet rsCitas = stmtCitas.executeQuery();

            listaCitasCargarPanel.setLayout(new BoxLayout(listaCitasCargarPanel, BoxLayout.Y_AXIS));
            ButtonGroup grupo = new ButtonGroup();
            boolean hayCitas = false;

            while (rsCitas.next()) {
                hayCitas = true;
                int idCita = rsCitas.getInt("id_cita");
                String detalle = rsCitas.getString("fecha") + " " + rsCitas.getString("hora") +
                        " con Dr. " + rsCitas.getString("doctor");

                JRadioButton radio = new JRadioButton(detalle);
                radio.putClientProperty("idCita", idCita);
                grupo.add(radio);
                listaCitasCargarPanel.add(radio);
            }

            if (!hayCitas) {
                listaCitasCargarPanel.add(new JLabel("No hay citas pendientes."));
            }

            listaCitasCargarPanel.revalidate();
            listaCitasCargarPanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar historial: " + ex.getMessage());
        }
    }



    private void eliminarCitas() {
        for (Component comp : listaCitasCargarPanel.getComponents()) {
            if (comp instanceof JRadioButton) {
                JRadioButton radio = (JRadioButton) comp;
                if (radio.isSelected()) {

                    int idCita = (int) radio.getClientProperty("idCita");
                    try (Connection conn = ConexionBaseDatos.conectar()) {
                        PreparedStatement stmt = conn.prepareStatement("DELETE FROM cita WHERE id_cita = ?");
                        stmt.setInt(1, idCita);
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Cita eliminada correctamente.");
                        buscarHistorialCitas();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage());
                    }
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(null, "Seleccione una cita para eliminar.");
    }



    private void verCitas() {
        for (Component comp : listaCitasCargarPanel.getComponents()) {
            if (comp instanceof JRadioButton) {
                JRadioButton radio = (JRadioButton) comp;
                if (radio.isSelected()) {
                    int idCita = (int) radio.getClientProperty("idCita");

                    try (Connection conn = ConexionBaseDatos.conectar()) {
                        PreparedStatement stmt = conn.prepareStatement(
                                "SELECT p.nombre AS paciente, d.nombre AS doctor, e.nombre AS especialidad, " +
                                        "c.fecha, c.hora " +
                                        "FROM cita c " +
                                        "JOIN paciente p ON c.id_paciente = p.id_paciente " +
                                        "JOIN doctor d ON c.id_doctor = d.id_doctor " +
                                        "JOIN especialidad e ON d.id_especialidad = e.id_especialidad " +
                                        "WHERE c.id_cita = ?");
                        stmt.setInt(1, idCita);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            String mensaje = "Paciente: " + rs.getString("paciente") + "\n"
                                    + "Doctor: " + rs.getString("doctor") + "\n"
                                    + "Especialidad: " + rs.getString("especialidad") + "\n"
                                    + "Fecha: " + rs.getString("fecha") + "\n"
                                    + "Hora: " + rs.getString("hora");
                            JOptionPane.showMessageDialog(null, mensaje, "Detalle de Cita", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error al cargar cita: " + ex.getMessage());
                    }

                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(null, "Seleccione una cita para ver.");
    }




}
