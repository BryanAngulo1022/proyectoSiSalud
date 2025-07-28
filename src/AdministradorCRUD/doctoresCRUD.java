package AdministradorCRUD;
import Conexion.ConexionBaseDatos;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Validaciones.Validador;

public class doctoresCRUD extends JFrame {
    private JPanel doctoresCRUDPanel;
    private JTabbedPane tabbedPane1;
    private JTextField nombreField;
    private JButton regresarButton;
    private JTextArea listaDoctores;
    private JTextField correoField;
    private JTextField telefonoField;
    private JButton guardarButton;
    private JTextField cedulaField;
    private JComboBox jornadaCombo;
    private JComboBox especialidadCombo;
    private JButton buscarCargarButton;
    private JButton guardarActualizarButton;
    private JTextField cedulaBuscarActField;
    private JTextField nombreActField;
    private JTextField cedulaActField;
    private JComboBox especialidadActCombo;
    private JComboBox jornadaActCombo;
    private JTextField correoActField;
    private JTextField telefonoActField;
    private JButton eliminarButton;
    private JButton mostrarDoctoresRegistradosButton;
    private JPanel verPestana;

    public doctoresCRUD() {
        setTitle("Panel del Administrador- SISALUD");
        setContentPane(doctoresCRUDPanel);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        cargarEspecialidades();

        //Registro de doctores
        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarDoctor();
            }
        });

        //Actualizar y eliminar doctores
        buscarCargarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarCargarDoctor();
            }
        });
        guardarActualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarDoctor();


            }
        });
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarDoctor();
            }
        });

        //Mostrar doctores registrados
        mostrarDoctoresRegistradosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDoctores();
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
                    listaDoctores.setText(""); // Limpia cuando se cambia de la pestaña
                }
            }
        });
    }



    private void registrarDoctor() {
        String nombre = nombreField.getText().trim();
        String ci = cedulaField.getText().trim();
        String correo = correoField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String especialidadSeleccionada = (String) especialidadCombo.getSelectedItem();
        String jornada = (String) jornadaCombo.getSelectedItem();

        //validaciones
        if (nombre.isEmpty() || ci.isEmpty() || correo.isEmpty() || telefono.isEmpty()
                || especialidadSeleccionada == null || jornada == null) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
            return;
        }

        if (!Validador.validarCedula(ci)) {
            JOptionPane.showMessageDialog(this, "La cédula debe tener exactamente 10 dígitos.");
            return;
        }
        if (!Validador.validarCorreo(correo)) {
            JOptionPane.showMessageDialog(this, "Ingrese un correo electrónico válido.");
            return;
        }
        if (!Validador.validarTelefono(telefono)) {
            JOptionPane.showMessageDialog(this, "El teléfono debe tener exactamente 10 dígitos.");
            return;
        }
//Coneccion

        try (Connection conn = ConexionBaseDatos.conectar()) {
            // Obtener el id_especialidad correspondiente al nombre
            String obtenerIdSql = "SELECT id_especialidad FROM especialidad WHERE nombre = ?";
            PreparedStatement obtenerIdStmt = conn.prepareStatement(obtenerIdSql);
            obtenerIdStmt.setString(1, especialidadSeleccionada);
            ResultSet rs = obtenerIdStmt.executeQuery();

            if (rs.next()) {
                int idEspecialidad = rs.getInt("id_especialidad");

                // Insertar el doctor
                String insertarSql = "INSERT INTO doctor (nombre, ci, id_especialidad, correo, telefono, jornada) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertarStmt = conn.prepareStatement(insertarSql);
                insertarStmt.setString(1, nombre);
                insertarStmt.setString(2, ci);
                insertarStmt.setInt(3, idEspecialidad);
                insertarStmt.setString(4, correo);
                insertarStmt.setString(5, telefono);
                insertarStmt.setString(6, jornada);

                int filasAfectadas = insertarStmt.executeUpdate();

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "Doctor registrado exitosamente.");
                    limpiarCamposRegistro();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar el doctor.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró la especialidad seleccionada.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al registrar el doctor: " + e.getMessage());
        }
    }

    private void limpiarCamposRegistro() {
        nombreField.setText("");
        cedulaField.setText("");
        correoField.setText("");
        telefonoField.setText("");
        especialidadCombo.setSelectedIndex(-1);
        jornadaCombo.setSelectedIndex(-1);
    }

    private void cargarEspecialidades() {
        try (Connection conn = ConexionBaseDatos.conectar()) {
            String sql = "SELECT nombre FROM especialidad";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Limpiar los ComboBox primero
            especialidadCombo.removeAllItems();
            especialidadActCombo.removeAllItems();

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                especialidadCombo.addItem(nombre);
                especialidadActCombo.addItem(nombre);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar especialidades: " + e.getMessage());
        }
    }

    private void buscarCargarDoctor() {
        String ci = cedulaBuscarActField.getText().trim();

        if (ci.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula del doctor a buscar.");
            return;
        }

        try (Connection conn = ConexionBaseDatos.conectar()) {
            String sql = "SELECT d.nombre, d.correo, d.telefono, d.jornada, e.nombre AS especialidad " +
                    "FROM doctor d JOIN especialidad e ON d.id_especialidad = e.id_especialidad " +
                    "WHERE d.ci = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, ci);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Cargar los campos en el formulario
                nombreActField.setText(rs.getString("nombre"));
                correoActField.setText(rs.getString("correo"));
                telefonoActField.setText(rs.getString("telefono"));
                jornadaActCombo.setSelectedItem(rs.getString("jornada"));
                especialidadActCombo.setSelectedItem(rs.getString("especialidad"));

                JOptionPane.showMessageDialog(this, "Doctor encontrado y datos cargados.");
            } else {
                JOptionPane.showMessageDialog(this, "Doctor no encontrado.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar doctor: " + ex.getMessage());
        }
    }




    private void actualizarDoctor() {
        String ci = cedulaBuscarActField.getText().trim();  // CI es el identificador
        String nuevoNombre = nombreActField.getText().trim();
        String nuevoCorreo = correoActField.getText().trim();
        String nuevoTelefono = telefonoActField.getText().trim();
        String nuevaEspecialidad = (String) especialidadActCombo.getSelectedItem();
        String nuevaJornada = (String) jornadaActCombo.getSelectedItem();

        //validaciones
        if (ci.isEmpty() || nuevoNombre.isEmpty() || nuevoCorreo.isEmpty() || nuevoTelefono.isEmpty()
                || nuevaEspecialidad == null || nuevaJornada == null) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
            return;
        }
        if (!Validador.validarCedula(ci)) {
            JOptionPane.showMessageDialog(this, "La cédula debe tener exactamente 10 dígitos.");
            return;
        }
        if (!Validador.validarCorreo(nuevoCorreo)) {
            JOptionPane.showMessageDialog(this, "Ingrese un correo electrónico válido.");
            return;
        }
        if (!Validador.validarTelefono(nuevoTelefono)) {
            JOptionPane.showMessageDialog(this, "El teléfono debe tener exactamente 10 dígitos.");
            return;
        }

        try (Connection conn = ConexionBaseDatos.conectar()) {
            // Obtener el ID de la especialidad
            String sqlEspecialidad = "SELECT id_especialidad FROM especialidad WHERE nombre = ?";
            PreparedStatement stmtEspecialidad = conn.prepareStatement(sqlEspecialidad);
            stmtEspecialidad.setString(1, nuevaEspecialidad);
            ResultSet rs = stmtEspecialidad.executeQuery();

            if (rs.next()) {
                int idEspecialidad = rs.getInt("id_especialidad");

                String sqlActualizar = "UPDATE doctor SET nombre = ?, correo = ?, telefono = ?, id_especialidad = ?, jornada = ? " +
                        "WHERE ci = ?";
                PreparedStatement stmtActualizar = conn.prepareStatement(sqlActualizar);
                stmtActualizar.setString(1, nuevoNombre);
                stmtActualizar.setString(2, nuevoCorreo);
                stmtActualizar.setString(3, nuevoTelefono);
                stmtActualizar.setInt(4, idEspecialidad);
                stmtActualizar.setString(5, nuevaJornada);
                stmtActualizar.setString(6, ci);

                int filas = stmtActualizar.executeUpdate();
                if (filas > 0) {
                    JOptionPane.showMessageDialog(this, "Doctor actualizado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró un doctor con esa cédula.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Especialidad no encontrada.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar doctor: " + ex.getMessage());
        }
    }





    private void eliminarDoctor() {
        String correo = cedulaBuscarActField.getText();

        try (Connection con = ConexionBaseDatos.conectar()) {
            String sql = "DELETE FROM doctor WHERE correo = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, correo);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Doctor eliminado correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Doctor no encontrado.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar doctor: " + ex.getMessage());
        }
    }



    private void mostrarDoctores() {
        try (Connection conn = ConexionBaseDatos.conectar()) {
            String sql = "SELECT d.nombre, d.ci, e.nombre AS especialidad, d.correo, d.telefono, d.jornada " +
                    "FROM doctor d INNER JOIN especialidad e ON d.id_especialidad = e.id_especialidad";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            listaDoctores.setText(""); // Limpia el área antes de mostrar

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String ci = rs.getString("ci");
                String especialidad = rs.getString("especialidad");
                String correo = rs.getString("correo");
                String telefono = rs.getString("telefono");
                String jornada = rs.getString("jornada");

                listaDoctores.append("Nombre: " + nombre + "\n");
                listaDoctores.append("Cédula: " + ci + "\n");
                listaDoctores.append("Especialidad: " + especialidad + "\n");
                listaDoctores.append("Correo: " + correo + "\n");
                listaDoctores.append("Teléfono: " + telefono + "\n");
                listaDoctores.append("Jornada: " + jornada + "\n");
                listaDoctores.append("-----------------------------\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar doctores: " + e.getMessage());
        }
    }



}
