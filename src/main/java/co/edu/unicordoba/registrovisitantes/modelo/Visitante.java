package co.edu.unicordoba.registrovisitantes.modelo;

import co.edu.unicordoba.registrovisitantes.util.TextoUtil;

public class Visitante {

    // Miembros de instancia: cada objeto tiene sus propios valores.
    private final int id;
    private final String nombre;
    private final int edad;

    // Miembro de clase: una sola copia compartida por todos los objetos.
    private static int totalCreados;

    // Constante de clase.
    public static final int EDAD_MINIMA = 18;

    // Bloque static: se ejecuta una sola vez cuando se carga la clase.
    static {
        totalCreados = 0;
    }

    public Visitante(String nombre, int edad) {
        // No usamos this porque totalCreados pertenece a la clase.
        totalCreados++;

        // Usamos this porque id, nombre y edad pertenecen a esta instancia.
        this.id = totalCreados;
        this.nombre = TextoUtil.normalizarNombre(nombre);
        this.edad = edad;
    }

    public boolean esMayorDeEdad() {
        return this.edad >= EDAD_MINIMA;
    }

    public static int getTotalCreados() {
        return totalCreados;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }
}
