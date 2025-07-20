import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Recepcionista extends JFrame {
    private JPanel RecepcionistaPanel;
    private JTabbedPane tabbedPane1;
    private JTextField nombreField;
    private JTextField cedulaField;
    private JTextField fechaField;
    private JTextField telefonoField;
    private JTextField direccionField;
    private JTextField correoField;
    private JComboBox generoCombo;
    private JButton guardarButton;
    private JTextField cedulaBuscarField;
    private JButton citaBuscarButton;
    private JComboBox especialidadCombo;
    private JComboBox doctorCombo;
    private JTextField cedulaBuscarHistField;
    private JButton buscarHistorialButton;
    private JTextPane historialPanel;
    private JButton agendarCitaButton;
    private JComboBox diaCombo;
    private JButton limpiarButton;
    private JButton cerrarSesionButton;
    private JPanel registroPanel;
    private JPanel citaPanel;
    private JComboBox horaCombo;
    private JLabel nombreUsuarioCargar;

    public Recepcionista(String nombreUsuario) {
        setTitle("Panel del Recepcionista - SISALUD");
        setContentPane(RecepcionistaPanel);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Mostrar el nombre del usuario que inicia sesión
        nombreUsuarioCargar.setText("Bienvenido, " + nombreUsuario);

        // Pestana de registro de paciente
        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarPaciente();
            }
        });

        //Pestan de agendar cita

        citaBuscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarPaciente();
            }
        });
        cargarEspecialidad();
        cargarDoctores();

        agendarCitaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agendarCita();
                cargarHorasDisponibles();

            }
        });

        especialidadCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarDoctores();
            }
        });
        doctorCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarDiasDisponibles();
            }
        });
        diaCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarHorasDisponibles();
            }
        });
        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new InicioSesionForm();
            }
        });

        //Pestana de historial medico
        buscarHistorialButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarHistorial();

            }
        });
        limpiarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarTodosLosCampos();
            }
        });
    }

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
    private void cargarEspecialidad() {
        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT id_especialidad, nombre FROM especialidad");
             ResultSet rs = ps.executeQuery()) {

            especialidadCombo.removeAllItems();
            while (rs.next()) {
                int id = rs.getInt("id_especialidad");
                String nombre = rs.getString("nombre");
                especialidadCombo.addItem(id + " - " + nombre);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar especialidades: " + e.getMessage());
        }
    }


    private void cargarDoctores() {
        String seleccion = (String) especialidadCombo.getSelectedItem();
        if (seleccion == null || seleccion.isEmpty()) return;

        int idEspecialidad = Integer.parseInt(seleccion.split(" - ")[0]);

        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT id_doctor, nombre, jornada FROM doctor WHERE id_especialidad = ?")) {

            ps.setInt(1, idEspecialidad);
            ResultSet rs = ps.executeQuery();

            doctorCombo.removeAllItems();
            while (rs.next()) {
                int idDoctor = rs.getInt("id_doctor");
                String nombre = rs.getString("nombre");
                String jornada = rs.getString("jornada");
                doctorCombo.addItem(idDoctor + " - " + nombre + " (" + jornada + ")");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar doctores: " + e.getMessage());
        }
    }

    private void cargarDiasDisponibles() {
        diaCombo.removeAllItems();
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 1; i < 5; i++) {
            LocalDate dia = hoy.plusDays(i);
            diaCombo.addItem(dia.format(formatter));
        }
    }

    private void cargarHorasDisponibles() {
        horaCombo.removeAllItems();

        String doctorSeleccionado = (String) doctorCombo.getSelectedItem();
        if (doctorSeleccionado == null) return;

        int idDoctor = Integer.parseInt(doctorSeleccionado.split(" - ")[0]);
        String diaSeleccionado = (String) diaCombo.getSelectedItem();
        if (diaSeleccionado == null) return;

        String jornada = doctorSeleccionado.substring(doctorSeleccionado.indexOf("(") + 1, doctorSeleccionado.indexOf(")"));

        List<String> horas = new ArrayList<>();
        if (jornada.equalsIgnoreCase("Mañana") || jornada.equalsIgnoreCase("Completa")) {
            horas.addAll(generarIntervalos("09:00", "13:00"));
        }
        if (jornada.equalsIgnoreCase("Tarde") || jornada.equalsIgnoreCase("Completa")) {
            horas.addAll(generarIntervalos("14:00", "20:00"));
        }

        // Verificar cuáles ya están agendadas
        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT hora FROM cita WHERE id_doctor = ? AND fecha = ?")) {

            ps.setInt(1, idDoctor);
            ps.setDate(2, java.sql.Date.valueOf(diaSeleccionado));

            ResultSet rs = ps.executeQuery();
            Set<String> horasOcupadas = new HashSet<>();
            DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

            while (rs.next()) {
                String horaBD = rs.getTime("hora").toLocalTime().format(formatoHora);
                horasOcupadas.add(horaBD);
            }

            for (String hora : horas) {
                if (!horasOcupadas.contains(hora)) {
                    horaCombo.addItem(hora);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar disponibilidad: " + e.getMessage());
        }
    }


    private List<String> generarIntervalos(String inicio, String fin) {
        List<String> lista = new ArrayList<>();
        LocalTime horaInicio = LocalTime.parse(inicio);
        LocalTime horaFin = LocalTime.parse(fin);
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        while (!horaInicio.isAfter(horaFin.minusMinutes(30))) {
            lista.add(horaInicio.format(formatoHora));
            horaInicio = horaInicio.plusMinutes(30);
        }
        return lista;
    }


    private void agendarCita() {
        String ciPaciente = cedulaBuscarField.getText().trim();
        if (ciPaciente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula del paciente.");
            return;
        }

        int idDoctor = Integer.parseInt(((String) doctorCombo.getSelectedItem()).split(" - ")[0]);
        String fecha = (String) diaCombo.getSelectedItem();
        String hora = (String) horaCombo.getSelectedItem();

        if (fecha == null || hora == null) {
            JOptionPane.showMessageDialog(this, "Seleccione fecha y hora.");
            return;
        }

        // Obtener ID del paciente
        int idPaciente = -1;
        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT id_paciente FROM paciente WHERE ci = ?")) {

            ps.setString(1, ciPaciente);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idPaciente = rs.getInt("id_paciente");
            } else {
                JOptionPane.showMessageDialog(this, "Paciente no encontrado. Registre al paciente antes de agendar la cita.");
                tabbedPane1.setSelectedComponent(registroPanel); // Cambiar a pestaña de registro
                cedulaField.setText(ciPaciente); // Prellenar cédula en el registro
                return;
            }


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar paciente: " + e.getMessage());
            return;
        }

        // Guardar la cita
        String insert = "INSERT INTO cita (id_paciente, id_doctor, fecha, hora) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement ps = conn.prepareStatement(insert)) {

            ps.setInt(1, idPaciente);
            ps.setInt(2, idDoctor);
            ps.setDate(3, java.sql.Date.valueOf(fecha));
            ps.setTime(4, java.sql.Time.valueOf(hora + ":00"));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cita agendada correctamente.");

            cargarHorasDisponibles(); // Recargar para bloquear la hora usada

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agendar cita: " + e.getMessage());
        }
    }

    private void buscarHistorial() {
        String cedula = cedulaBuscarHistField.getText().trim();

        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula del paciente.");
            return;
        }

        String consulta = """
        SELECT p.nombre AS paciente, p.ci, p.telefono, p.correo,
               d.nombre AS doctor, e.nombre AS especialidad,
               c.fecha, c.hora
        FROM cita c
        JOIN paciente p ON c.id_paciente = p.id_paciente
        JOIN doctor d ON c.id_doctor = d.id_doctor
        JOIN especialidad e ON d.id_especialidad = e.id_especialidad
        WHERE p.ci = ?
        ORDER BY c.fecha, c.hora
    """;

        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement ps = conn.prepareStatement(consulta)) {

            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();

            StringBuilder historial = new StringBuilder();
            boolean hayResultados = false;
            boolean pacienteMostrado = false;

            while (rs.next()) {
                hayResultados = true;

                if (!pacienteMostrado) {
                    historial.append("Datos del Paciente:\n");
                    historial.append("Nombre: ").append(rs.getString("paciente")).append("\n");
                    historial.append("CI: ").append(rs.getString("ci")).append("\n");
                    historial.append("Teléfono: ").append(rs.getString("telefono")).append("\n");
                    historial.append("Correo: ").append(rs.getString("correo")).append("\n\n");
                    historial.append("Historial de Citas:\n");
                    pacienteMostrado = true;
                }

                historial.append("- Doctor: ").append(rs.getString("doctor")).append("\n");
                historial.append("  Especialidad: ").append(rs.getString("especialidad")).append("\n");
                historial.append("  Fecha: ").append(rs.getDate("fecha")).append("\n");
                historial.append("  Hora: ").append(rs.getTime("hora")).append("\n");
                historial.append("  -------------------------\n");
            }

            if (!hayResultados) {
                historialPanel.setText("No se encontraron citas para esta cédula.");
            } else {
                historialPanel.setText(historial.toString());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar historial: " + e.getMessage());
        }
    }
    private void limpiarTodosLosCampos() {
        // Pestaña REGISTRO de pacientes
        nombreField.setText("");
        cedulaField.setText("");
        fechaField.setText("");
        telefonoField.setText("");
        direccionField.setText("");
        correoField.setText("");
        if (generoCombo.getItemCount() > 0) {
            generoCombo.setSelectedIndex(0);
        }

        // Pestaña AGENDAR CITA
        cedulaBuscarField.setText("");
        if (especialidadCombo.getItemCount() > 0) {
            especialidadCombo.setSelectedIndex(0);
        }
        doctorCombo.removeAllItems();
        diaCombo.removeAllItems();
        horaCombo.removeAllItems();

        // Pestaña HISTORIAL
        cedulaBuscarHistField.setText("");
        historialPanel.setText("");
    }


}
