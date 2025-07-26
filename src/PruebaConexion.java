import Conexion.ConexionBaseDatos;

import java.sql.Connection;

public class PruebaConexion {
    public static void main(String[] args) {
        ConexionBaseDatos.conectar();
    }
}
