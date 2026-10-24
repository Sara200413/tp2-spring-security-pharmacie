package com.ensa.pharma.repository;

import com.ensa.pharma.model.Vente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenteRepository extends JpaRepository<Vente, Long> {

    List<Vente> findTop20ByOrderByDateVenteDesc();
}
