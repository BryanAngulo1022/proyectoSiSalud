import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDatos {
    //URL = "jdbc:postgresql://<HOST>:<PUERTO>/<DATABASE>";
    private static final String URL = "jdbc:postgresql://by2irxhy7lmdxk94s7sw-postgresql.services.clever-cloud.com:" +
            "50013/by2irxhy7lmdxk94s7sw";
    //USER
    private static final String USUARIO =  "uv4zudeif8k9yxmy34bd";
    //PASSWORD
    private static final String CONTRASENA = "izN9PokXbBV2iOfFWed9MWabLWFBUy";

    public static Connection conectar() {
        Connection conexion = null;
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println(" Conexión exitosa a PostgreSQL");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(" Error al conectar: " + e.getMessage());
        }
        return conexion;
    }
}
