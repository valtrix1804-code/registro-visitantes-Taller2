package co.edu.unicordoba.registrovisitantes.servicio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.unicordoba.registrovisitantes.modelo.Visitante;

@Service
public class VisitanteService {

    // Estado de instancia del bean: pertenece al objeto administrado por Spring.
    private final List<Visitante> reg = new ArrayList<>();

    public Visitante registrar(String nombre, int edad) {
        Visitante visitante = new Visitante(nombre, edad);
        reg.add(visitante);
        return visitante;
    }

    public List<Visitante> listar() {
        return List.copyOf(reg);
    }

    public int contarRegistrados() {
        return reg.size();
    }

    public int contarCreadosEnLaClase() {
        return Visitante.getTotalCreados();
    }
}
