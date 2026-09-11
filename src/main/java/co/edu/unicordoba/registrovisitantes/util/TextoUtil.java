package co.edu.unicordoba.registrovisitantes.util;

public final class TextoUtil {

    private TextoUtil() {
        throw new UnsupportedOperationException("Clase de utilidad");
    }

    public static String normalizarNombre(String texto) {
        if (texto == null || texto.isBlank()) {
            return "SIN NOMBRE";
        }

        String[] partes = texto.trim().toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String parte : partes) {
            resultado.append(Character.toUpperCase(parte.charAt(0)))
                     .append(parte.substring(1))
                     .append(" ");
        }

        return resultado.toString().trim();
    }
}
