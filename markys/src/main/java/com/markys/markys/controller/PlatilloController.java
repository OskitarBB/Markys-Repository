package com.markys.markys.controller;

import com.markys.markys.model.Platillo;
import com.markys.markys.repository.PlatilloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/platillos")
public class PlatilloController {

    @Autowired
    private PlatilloRepository platilloRepository;

    // Obtener todos los platillos
    @GetMapping
    public List<Platillo> getAllPlatillos() {
        return platilloRepository.findAll();
    }

    // Obtener un platillo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Platillo> getPlatilloById(@PathVariable Long id) {
        Optional<Platillo> platillo = platilloRepository.findById(id);
        return platillo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo platillo
    @PostMapping
    public ResponseEntity<Platillo> createPlatillo(@RequestBody Platillo platillo) {
        Platillo savedPlatillo = platilloRepository.save(platillo);
        return new ResponseEntity<>(savedPlatillo, HttpStatus.CREATED);
    }

    // Actualizar un platillo existente
    @PutMapping("/{id}")
    public ResponseEntity<Platillo> updatePlatillo(@PathVariable Long id, @RequestBody Platillo platilloDetails) {
        Optional<Platillo> existingPlatillo = platilloRepository.findById(id);
        if (existingPlatillo.isPresent()) {
            Platillo platillo = existingPlatillo.get();
            platillo.setNombre(platilloDetails.getNombre());
            platillo.setDescripcion(platilloDetails.getDescripcion());
            platillo.setPrecio(platilloDetails.getPrecio());
            platillo.setEstado(platilloDetails.getEstado());
            platillo.setImagen(platilloDetails.getImagen());

            Platillo updatedPlatillo = platilloRepository.save(platillo);
            return ResponseEntity.ok(updatedPlatillo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un platillo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlatillo(@PathVariable Long id) {
        Optional<Platillo> platillo = platilloRepository.findById(id);
        if (platillo.isPresent()) {
            platilloRepository.delete(platillo.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
