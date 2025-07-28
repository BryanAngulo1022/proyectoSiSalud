package Validaciones;

/**
 * Clase para validar campos comunes en el sistema SISALUD.
 */
public class Validador {

    /**
     * Valida que la cédula tenga exactamente 10 dígitos numéricos.
     *
     * @param cedula Número de cédula ingresado.
     * @return true si es válida, false en caso contrario.
     */
    public static boolean validarCedula(String cedula) {
        return cedula != null && cedula.matches("\\d{10}");
    }

    /**
     * Valida que el correo tenga un formato válido y contenga '@'.
     *
     * @param correo Correo electrónico ingresado.
     * @return true si es válido, false en caso contrario.
     */
    public static boolean validarCorreo(String correo) {
        return correo != null && correo.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
        //^[\\w._%+-]Debe comenzar con al menos un carácter alfanumérico, punto, guion o guion bajo.
        //@: Debe tener un solo arroba.
        // [\\w.-]+:Después de la arroba debe haber al menos un carácter alfanumérico, punto o guion (el dominio).
        // \\.[a-zA-Z]{2,6}$ : Termina con un punto seguido de entre 2 y 6 letras (por ejemplo, .com, .ec, .online).
    }

    /**
     * Valida que el teléfono tenga exactamente 10 dígitos numéricos.
     *
     * @param telefono Número telefónico ingresado.
     * @return true si es válido, false en caso contrario.
     */
    public static boolean validarTelefono(String telefono) {
        return telefono != null && telefono.matches("\\d{10}");
    }
}
