package co.edu.unicordoba.registrovisitantes.controlador;

import java.net.InetAddress;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicordoba.registrovisitantes.modelo.Visitante;
import co.edu.unicordoba.registrovisitantes.servicio.VisitanteService;
import co.edu.unicordoba.registrovisitantes.util.TextoUtil;

@RestController
@RequestMapping("/api/visitantes")
public class VisitanteController {

    // Sello de esta ejecución de la JVM: se fija una vez al cargar la clase.
    private static final Instant ARRANQUE = Instant.now();

    private final VisitanteService servicio;

    // Inyección por constructor: forma recomendada para este taller.
    public VisitanteController(VisitanteService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public Visitante registrar(@RequestParam String nombre,
                               @RequestParam int edad) {
        return servicio.registrar(nombre, edad);
    }

    @GetMapping
    public List<Visitante> listar() {
        return servicio.listar();
    }

    @GetMapping("/conteos")
    public Map<String, Object> conteos() {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("registradosEnElServicio", servicio.contarRegistrados());
        resultado.put("creadosEnLaClase", servicio.contarCreadosEnLaClase());
        resultado.put("edadMinima", Visitante.EDAD_MINIMA);
        return resultado;
    }

    @GetMapping("/instancia")
    public Map<String, Object> instancia() throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("host", InetAddress.getLocalHost().getHostName());
        resultado.put("arranqueJvm", ARRANQUE.toString());
        resultado.put("creados", Visitante.getTotalCreados());
        resultado.put("registrados", servicio.contarRegistrados());
        return resultado;
    }

    @GetMapping("/normalizar")
    public Map<String, String> normalizar(@RequestParam String texto) {
        return Map.of("normalizado", TextoUtil.normalizarNombre(texto));
    }
}
