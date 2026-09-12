package co.edu.unicordoba.registrovisitantes.controlador;

import java.net.InetAddress;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicordoba.registrovisitantes.modelo.Visitante;
import co.edu.unicordoba.registrovisitantes.servicio.VisitanteService;
import co.edu.unicordoba.registrovisitantes.util.TextoUtil;

@RestController
public class VisitanteController {

    private static final Instant ARRANQUE = Instant.now();

    private final VisitanteService servicio;

    public VisitanteController(VisitanteService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/")
    public Map<String, String> inicio() {
        return Map.of(
            "mensaje", "API Registro de Visitantes funcionando",
            "estado", "OK"
        );
    }

@PostMapping
    public Visitante registrar(@RequestBody Map<String, Object> body) {
        String nombre = (String) body.get("nombre");
        int edad = Integer.parseInt(body.get("edad").toString());
        return servicio.registrar(nombre, edad);
    }

    @GetMapping("/api/visitantes")
    public List<Visitante> listar() {
        return servicio.listar();
    }

    @GetMapping("/api/visitantes/conteos")
    public Map<String, Object> conteos() {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("registradosEnElServicio", servicio.contarRegistrados());
        resultado.put("creadosEnLaClase", servicio.contarCreadosEnLaClase());
        resultado.put("edadMinima", Visitante.EDAD_MINIMA);
        return resultado;
    }

    @GetMapping("/api/visitantes/instancia")
    public Map<String, Object> instancia() throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("host", InetAddress.getLocalHost().getHostName());
        resultado.put("arranqueJvm", ARRANQUE.toString());
        resultado.put("creados", Visitante.getTotalCreados());
        resultado.put("registrados", servicio.contarRegistrados());
        return resultado;
    }

    @GetMapping("/api/visitantes/normalizar")
    public Map<String, String> normalizar(@RequestParam String texto) {
        return Map.of("normalizado", TextoUtil.normalizarNombre(texto));
    }
}
