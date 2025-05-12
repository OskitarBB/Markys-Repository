package com.markys.markys.service;

import com.markys.markys.model.Platillo;
import com.markys.markys.repository.PlatilloRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicioPlatillo {

    private final PlatilloRepository repositorio;

    public ServicioPlatillo(PlatilloRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Platillo> obtenerTodos() {
        return repositorio.findAll();
    }
}
