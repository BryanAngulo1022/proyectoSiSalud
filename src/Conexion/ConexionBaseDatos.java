package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDatos {
    //URL = "jdbc:postgresql://<HOST>:<PUERTO>/<DATABASE>";
    // Postgre
    //private static final String URL = "jdbc:postgresql://by2irxhy7lmdxk94s7sw-postgresql.services.clever-cloud.com:50013/by2irxhy7lmdxk94s7sw";
    //MySQL
    private static final String URL = "jdbc:mysql://urgrlb4euxxqbtvv:uAKQDrSyz22PM3h8PRTe@bqhe7g8ll3hq1mumndau-mysql.services.clever-cloud.com:3306/bqhe7g8ll3hq1mumndau";
    //USER
    private static final String USUARIO =  "uurgrlb4euxxqbtvv";
    //PASSWORD
    private static final String CONTRASENA = "uAKQDrSyz22PM3h8PRTe";

    public static Connection conectar() {
        Connection conexion = null;
        try {
            //Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
           System.out.println(" Conexión exitosa a PostgreSQL");
            //catch (ClassNotFoundException | SQLException e) {
        } catch (SQLException e) {
           System.out.println(" Error al conectar: " + e.getMessage());
        }
        return conexion;
    }
}
