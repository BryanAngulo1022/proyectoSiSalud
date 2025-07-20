import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/* Formulario de inicio de sesión para el sistema SISALUD.
 */
public class InicioSesionForm extends JFrame {
    private JTextField usuarioField;
    private JPasswordField claveField;
    private JComboBox<String> rolComboBox;
    private JButton accederButton;
    private JPanel loginPanel;

    public InicioSesionForm() {
        setTitle("Inicio de Sesión - SISALUD");
        setContentPane(loginPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);

        accederButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSesion();
            }
        });
    }

    /* Verifica si el usuario, contraseña y rol coinciden con los de la base de datos.
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
                JOptionPane.showMessageDialog(this, "Acceso concedido como " + rolSeleccionado);
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

    public static void main(String[] args) {
        new InicioSesionForm();
    }
}
