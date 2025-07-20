import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Administrador extends JFrame {
    private JPanel AdministradorPanel;
    private JTabbedPane tabbedPane1;
    private JButton cerrarSesionButton;
    private JButton regitrarUsuarioButton;
    private JButton eliminarUsuarioButton;
    private JButton verUsuariosButton;
    private JButton registrarPacientesButton;
    private JButton eliminarPacientesButton;
    private JButton verPacientesButton;
    private JButton registrarDoctoresButton;
    private JButton eliminarDoctoresButton;
    private JButton verDoctoresButton;
    private JButton registrarEspecilidadButton;
    private JButton eliminarEscpecialidadButton;
    private JButton verEspecialidadButton;
    private JButton asignarDoctoresButton;
    private JButton verReporteButton;
    private JComboBox comboBox1;
    private JLabel nombreAdmCargar;

    public Administrador(String nombreAdmin) {
        setTitle("Panel del Administrador- SISALUD");
        setContentPane(AdministradorPanel);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        //Panel de usuarios

        //Panel de pacientes

        //Panel de Doctores

        //Panel de Especialidad

        //Panel de Reportes
    }



}
