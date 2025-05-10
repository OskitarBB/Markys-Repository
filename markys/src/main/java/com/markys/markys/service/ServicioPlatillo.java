package com.markys.markys.service;

import com.markys.markys.model.Estado;
import com.markys.markys.model.Platillo;
import com.markys.markys.repository.PlatilloRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioPlatillo {

    private final PlatilloRepository repositorio;

    public ServicioPlatillo(PlatilloRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Platillo> obtenerTodos() {
        return repositorio.findAll();
    }


    // codigo implementado a raiz del Test.
    // codigo refactorizado luego de la implementacion!

    public boolean marcarComoAgotado(Long id) {
        Optional<Platillo> optionalPlatillo = repositorio.findById(id);

        if (optionalPlatillo.isPresent()) {
            Platillo platillo = optionalPlatillo.get();
            platillo.setEstado(Estado.AGOTADO);
            repositorio.save(platillo);
            return true;
        }
        return false;
    }
}

