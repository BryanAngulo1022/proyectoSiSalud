package AdministradorCRUD;
import Conexion.ConexionBaseDatos;
import Roles.Administrador;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class pacientesCRUD extends JFrame{

    private JPanel usuariosCRUDPanel;
    private JTabbedPane tabbedPane1;
    private JButton mostrarPacientesRegistradosButton;
    private JTextArea listaPacientes;
    private JTextField telefonoField;
    private JTextField direccionField;
    private JButton regresarButton;
    private JComboBox generoCombo;
    private JTextField correoField;
    private JButton guardarButton;
    private JTextField nombreField;
    private JTextField cedulaField;
    private JTextField fechaField;
    private JTextField cedulaBuscarField;
    private JButton buscarButton;
    private JButton eliminarButton;
    private JButton buscarActButton;
    private JTextField nacimientoActField;
    private JTextField telefonoActField;
    private JTextField correoActField;
    private JTextField direccionActField;
    private JButton guardarActButton;
    private JTextField cedulaBuscarActField;
    private JTextField nombreActField;
    private JTextField cedulaActField;
    private JComboBox generoActField;
    private JPanel registroPestana;
    private JPanel eliminacionPestana;
    private JPanel actualizacionPestana;
    private JPanel verPestana;

    public pacientesCRUD() {
        setTitle("Panel del Administrador- SISALUD");
        setContentPane(usuariosCRUDPanel);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarPaciente();
            }
        });
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarPaciente();
            }
        });
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarPaciente();
            }
        });
        buscarActButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarPaciente();
                cargarPaciente();
            }
        });
        guardarActButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarPaciente();
            }
        });
        mostrarPacientesRegistradosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarPacientesRegistrados();
            }
        });
        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });


        tabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane1.getSelectedIndex();
                if (selectedIndex != tabbedPane1.indexOfComponent(verPestana)) {
                    listaPacientes.setText(""); // Limpia cuando se cambia de la pestaña "Ver pacientes"
                }
            }
        });
    }




    //Registro pacientes

    private void registrarPaciente() {
        String nombre = nombreField.getText().trim();
        String ci = cedulaField.getText().trim();
        String fechaNacimiento = fechaField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String direccion = direccionField.getText().trim();
        String genero = (String) generoCombo.getSelectedItem();
        String correo = correoField.getText().trim();

        if (nombre.isEmpty() || ci.isEmpty() || fechaNacimiento.isEmpty() || telefono.isEmpty() || genero == null || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos obligatorios deben ser completados.");
            return;
        }

        java.sql.Date fechaSql;
        try {
            fechaSql = java.sql.Date.valueOf(fechaNacimiento); // formato: YYYY-MM-DD
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "La fecha debe estar en formato YYYY-MM-DD.");
            return;
        }

        String sql = "INSERT INTO paciente (nombre, ci, fecha_nacimiento, telefono, direccion, genero, correo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBaseDatos.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, ci);
            ps.setDate(3, fechaSql);
            ps.setString(4, telefono);
            ps.setString(5, direccion);
            ps.setString(6, genero);
            ps.setString(7, correo);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Paciente registrado exitosamente.");
            limpiarCampos();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar paciente: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        nombreField.setText("");
        cedulaField.setText("");
        fechaField.setText("");
        telefonoField.setText("");
        direccionField.setText("");
        correoField.setText("");
        generoCombo.setSelectedIndex(0);
    }

    // Eliminacion de pacientes
    private void buscarPaciente() {
        String cedula = cedulaBuscarField.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula del paciente.");
            return;
        }

        String sql = "SELECT * FROM paciente WHERE ci = ?";

        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Paciente encontrado.");
            } else {
                JOptionPane.showMessageDialog(this, "Paciente no encontrado.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar paciente: " + ex.getMessage());
        }
    }



    private void eliminarPaciente() {
         String cedula = cedulaBuscarField.getText();

        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor ingrese la cédula del paciente a eliminar.");
            return;
        }

        Connection conexion = null;
        PreparedStatement obtenerIdStmt = null;
        PreparedStatement eliminarCitasStmt = null;
        PreparedStatement eliminarStmt = null;

        try {
            conexion = ConexionBaseDatos.conectar();

            // 1. Obtener el ID del paciente a partir de la cédula
            String obtenerIdSQL = "SELECT id_paciente FROM paciente WHERE ci = ?";
            obtenerIdStmt = conexion.prepareStatement(obtenerIdSQL);
            obtenerIdStmt.setString(1, cedula);
            ResultSet rs = obtenerIdStmt.executeQuery();

            if (rs.next()) {
                int idPaciente = rs.getInt("id_paciente");

                // 2. Eliminar las citas del paciente
                String eliminarCitasSQL = "DELETE FROM cita WHERE id_paciente = ?";
                eliminarCitasStmt = conexion.prepareStatement(eliminarCitasSQL);
                eliminarCitasStmt.setInt(1, idPaciente);
                eliminarCitasStmt.executeUpdate();

                // 3. Eliminar el paciente
                String eliminarPacienteSQL = "DELETE FROM paciente WHERE id_paciente = ?";
                eliminarStmt = conexion.prepareStatement(eliminarPacienteSQL);
                eliminarStmt.setInt(1, idPaciente);
                int filasEliminadas = eliminarStmt.executeUpdate();

                if (filasEliminadas > 0) {
                    JOptionPane.showMessageDialog(null, "Paciente y sus citas eliminados correctamente.");
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró el paciente con esa cédula.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el paciente con esa cédula.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar paciente: " + ex.getMessage());
        } finally {
            try {
                if (obtenerIdStmt != null) obtenerIdStmt.close();
                if (eliminarCitasStmt != null) eliminarCitasStmt.close();
                if (eliminarStmt != null) eliminarStmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }


    // Actualizar paciente
    private void cargarPaciente() {
        String cedula = cedulaBuscarActField.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula del paciente para cargar.");
            return;
        }

        String sql = "SELECT * FROM paciente WHERE ci = ?";

        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nombreActField.setText(rs.getString("nombre"));
                cedulaActField.setText(rs.getString("ci"));
                nacimientoActField.setText(rs.getString("fecha_nacimiento"));
                telefonoActField.setText(rs.getString("telefono"));
                direccionActField.setText(rs.getString("direccion"));
                generoActField.setSelectedItem(rs.getString("genero"));
                correoActField.setText(rs.getString("correo"));
            } else {
                JOptionPane.showMessageDialog(this, "Paciente no encontrado.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar paciente: " + ex.getMessage());
        }
    }


    private void actualizarPaciente() {
        String nombre = nombreActField.getText().trim();
        String ci = cedulaActField.getText().trim();
        String fechaNacimiento = nacimientoActField.getText().trim();
        String telefono = telefonoActField.getText().trim();
        String direccion = direccionActField.getText().trim();
        String genero = (String) generoActField.getSelectedItem();
        String correo = correoActField.getText().trim();

        if (nombre.isEmpty() || ci.isEmpty() || fechaNacimiento.isEmpty() || telefono.isEmpty() || genero == null || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos deben estar completos.");
            return;
        }

        java.sql.Date fechaSql;
        try {
            fechaSql = java.sql.Date.valueOf(fechaNacimiento);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD.");
            return;
        }

        String sql = "UPDATE paciente SET nombre = ?, fecha_nacimiento = ?, telefono = ?, direccion = ?, genero = ?, correo = ? WHERE ci = ?";

        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setDate(2, fechaSql);
            ps.setString(3, telefono);
            ps.setString(4, direccion);
            ps.setString(5, genero);
            ps.setString(6, correo);
            ps.setString(7, ci);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(this, "Paciente actualizado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el paciente con esa cédula.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar paciente: " + ex.getMessage());
        }
    }


    //Mostrar pacientes registrados
    private void mostrarPacientesRegistrados() {
        String sql = "SELECT nombre, ci, fecha_nacimiento, telefono, direccion, genero, correo FROM paciente ORDER BY nombre";

        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Nombre: ").append(rs.getString("nombre")).append("\n")
                        .append("Cédula: ").append(rs.getString("ci")).append("\n")
                        .append("Nacimiento: ").append(rs.getString("fecha_nacimiento")).append("\n")
                        .append("Teléfono: ").append(rs.getString("telefono")).append("\n")
                        .append("Dirección: ").append(rs.getString("direccion")).append("\n")
                        .append("Género: ").append(rs.getString("genero")).append("\n")
                        .append("Correo: ").append(rs.getString("correo")).append("\n")
                        .append("--------------------------------------------------\n");
            }

            listaPacientes.setText(sb.toString());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al mostrar pacientes: " + ex.getMessage());
        }
    }



}
