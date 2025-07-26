package Roles;

import AdministradorCRUD.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Administrador extends JFrame {
    private JPanel AdministradorPanel;
    private JTabbedPane tabbedPane1;
    private JButton cerrarSesionButton;
    private JButton PacientesButton;
    private JButton registrarEspecilidadButton;
    private JButton eliminarEspecialidadButton;
    private JButton verEspecialidadButton;
    private JButton asignarDoctoresButton;
    private JButton verReporteButton;
    private JComboBox comboBox1;
    private JLabel nombreAdmCargar;
    private JButton DoctoresButton;

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






        //Panel de Especialidad

        //Panel de Reportes



    }






}
