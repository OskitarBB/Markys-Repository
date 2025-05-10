package com.markys.markys.service;

import com.markys.markys.model.Estado;
import com.markys.markys.model.Platillo;
import com.markys.markys.repository.PlatilloRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioPlatilloTest {

    @Mock
    private PlatilloRepository platilloRepository;

    @InjectMocks
    private ServicioPlatillo servicioPlatillo;

    @Test
    void deberiaActualizarEstadoDelPlatilloAAgotado() {
        // Arrange
        Long platilloId = 1L;
        Platillo platillo = new Platillo(
                "Pollo a la brasa",
                "Descripción de prueba",
                new BigDecimal("25.00"),
                Estado.DISPONIBLE,
                "pollo.jpg"
        );

        when(platilloRepository.findById(platilloId)).thenReturn(Optional.of(platillo));
        when(platilloRepository.save(any(Platillo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        servicioPlatillo.marcarComoAgotado(platilloId);

        // Assert
        assertEquals(Estado.AGOTADO, platillo.getEstado(), "El estado del platillo debe ser AGOTADO");
        verify(platilloRepository).findById(platilloId);
        verify(platilloRepository).save(platillo);
    }
}
