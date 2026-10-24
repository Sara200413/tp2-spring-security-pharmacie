package com.ensa.pharma.repository;

import com.ensa.pharma.model.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    // Médicaments disponibles (stock > 0) : ce que voit le CLIENT
    List<Medicament> findByQuantiteStockGreaterThan(int quantite);
}
