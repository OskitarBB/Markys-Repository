package com.markys.markys.service;

import com.markys.markys.model.Estado;
import com.markys.markys.model.Platillo;
import com.markys.markys.repository.PlatilloRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ServicioPlatilloIntegrationTest {

    @Autowired
    private ServicioPlatillo servicioPlatillo;

    @Autowired
    private PlatilloRepository platilloRepository;

    @Test
    void guardarYObtenerPlatilloDesdeBaseDeDatos() {
        // Arrange
        Platillo platillo = new Platillo(
                "Chaufa de Pollo",
                "Arroz chaufa con pollo y verduras",
                new BigDecimal("22.00"),
                Estado.DISPONIBLE,
                "chaufa.jpg"
        );

        platilloRepository.save(platillo);

        // Act
        List<Platillo> platillos = servicioPlatillo.obtenerTodos();

        System.out.println("==== PLATILLOS ENCONTRADOS ====");
        platillos.forEach(p -> System.out.println(
                "ID: " + p.getId() +
                        " | Nombre: " + p.getNombre() +
                        " | Estado: " + p.getEstado() +
                        " | Precio: " + p.getPrecio()
        ));
        System.out.println("===============================");

        // Assert
        assertFalse(platillos.isEmpty());

        Platillo encontrado = platillos.stream()
                .filter(p -> "Chaufa de Pollo".equals(p.getNombre()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No se encontró el platillo 'Chaufa de Pollo'"));

        assertEquals("Chaufa de Pollo", encontrado.getNombre());
        assertEquals(Estado.DISPONIBLE, encontrado.getEstado());
    }

    @Test
    void marcarPlatilloComoAgotado_ActualizaEstadoCorrectamente() {
        // Arrange
        Platillo platillo = new Platillo(
                "Anticuchos",
                "Corazón de res con papas",
                new BigDecimal("18.50"),
                Estado.DISPONIBLE,
                "anticuchos.jpg"
        );
        Platillo guardado = platilloRepository.save(platillo);

        // Act
        boolean actualizado = servicioPlatillo.marcarComoAgotado(guardado.getId());

        // Assert
        Platillo actualizadoPlatillo = platilloRepository.findById(guardado.getId()).orElse(null);

        assertTrue(actualizado);
        assertNotNull(actualizadoPlatillo);
        assertEquals(Estado.AGOTADO, actualizadoPlatillo.getEstado());

        List<Platillo> platillos = servicioPlatillo.obtenerTodos();

        System.out.println("==== PLATILLOS DESPUÉS DE ACTUALIZAR ESTADO ====");
        platillos.forEach(p -> System.out.println(
                "ID: " + p.getId() +
                        " | Nombre: " + p.getNombre() +
                        " | Estado: " + p.getEstado()
        ));
        System.out.println("===============================================");
    }
}
