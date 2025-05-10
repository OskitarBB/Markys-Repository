package com.markys.markys.repository;

import com.markys.markys.model.Platillo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatilloRepository extends JpaRepository<Platillo, Long> {
}
