package Roles;

import Conexion.ConexionBaseDatos;
import imagenes.FondoPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Formulario de inicio de sesión para el sistema SISALUD.
 * Permite autenticar usuarios según su rol (Administrador o Recepcionista)
 * y redirige al panel correspondiente tras una validación exitosa.
 *
 * <p>Este formulario incluye:</p>
 * <ul>
 *     <li>Campos para usuario y contraseña.</li>
 *     <li>Selección del rol de acceso.</li>
 *     <li>Botón para iniciar sesión.</li>
 * </ul>
 *
 * @author Bryan
 * @version 1.0
 */
public class InicioSesionForm extends JFrame {
    private JTextField usuarioField;
    private JPasswordField claveField;
    private JComboBox<String> rolComboBox;
    private JButton accederButton;
    private JPanel loginPanel;


    /**
     * Constructor que inicializa el formulario de inicio de sesión.
     * Configura título, tamaño y eventos de los botones.
     */
    public InicioSesionForm() {
        setTitle("Inicio de Sesión - SISALUD");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);

        // Creamos el panel de fondo
        FondoPanel fondo = new FondoPanel();
        fondo.setImagen("/imagenes/login.jpg");

        // Hacemos transparente el panel diseñado
        loginPanel.setOpaque(false);

        // Añadimos el loginPanel (con los botones creados en el diseñador) al panel de fondo
        fondo.add(loginPanel);

        // Establecemos el fondo como contentPane
        setContentPane(fondo);
        setResizable(false);
        setVisible(true);



        accederButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSesion();
            }
        });
    }


    /**
     * Verifica las credenciales (usuario, contraseña y rol) en la base de datos.
     * Si son correctas, abre el panel correspondiente al rol seleccionado.
     *
     * <p>Roles disponibles:</p>
     * <ul>
     *     <li>Administrador → {@link Administrador}</li>
     *     <li>Recepcionista → {@link Recepcionista}</li>
     * </ul>
     *
     * <p>En caso de error, muestra un mensaje en pantalla.</p>
     */
    private void iniciarSesion() {
        String usuario = usuarioField.getText().trim(); // Usuario ingresado
        String contrasena = String.valueOf(claveField.getPassword()); // Contraseña
        String rolSeleccionado = (String) rolComboBox.getSelectedItem(); // Rol elegido

        // Validación simple de campos vacíos
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }

        try (Connection conexion = ConexionBaseDatos.conectar()) {
            // Consulta SQL: busca un usuario con usuario, clave y rol específicos
            // conexion se cierra automaticamente

            // Consulta SQL para verificar los datos
            // consulta preparada, que busca un usuario
            String sql = "SELECT * FROM usuario WHERE usuario = ? AND contrasena = ? AND rol = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            // se establece los valores en la consulta
            //Reemplaza los ? en la consulta con los valores ingresados
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ps.setString(3, rolSeleccionado);
            //Ejecutar la consulta  y guarda los resultados en rs
            ResultSet rs = ps.executeQuery();
            //rs.next() evalua si hay un siguiente registro en el resultado"
            if (rs.next()) {
                // Si se encuentra el usuario con los datos correctos
                dispose(); // Cierra el formulario actual

                String nombreUsuario = rs.getString("nombre");

                if (rolSeleccionado.equalsIgnoreCase("Administrador")) {
                    new Administrador(nombreUsuario);
                } else {
                    new Recepcionista(nombreUsuario );
                }
            } else {

                JOptionPane.showMessageDialog(this, "Datos incorrectos. Verifica usuario, contraseña y rol.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en base de datos: " + e.getMessage());
        }
    }



}


