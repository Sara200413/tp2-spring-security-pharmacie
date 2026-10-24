package com.ensa.pharma.service;

import com.ensa.pharma.model.Medicament;
import com.ensa.pharma.model.Vente;
import com.ensa.pharma.repository.MedicamentRepository;
import com.ensa.pharma.repository.VenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class VenteService {

    private final MedicamentRepository medicamentRepository;
    private final VenteRepository venteRepository;

    public VenteService(MedicamentRepository medicamentRepository, VenteRepository venteRepository) {
        this.medicamentRepository = medicamentRepository;
        this.venteRepository = venteRepository;
    }

    // Enregistre une vente ET diminue le stock (dans la même transaction)
    @Transactional
    public Vente enregistrer(Long medicamentId, int quantite, String vendeur) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0.");
        }
        Medicament m = medicamentRepository.findById(medicamentId)
                .orElseThrow(() -> new IllegalArgumentException("Médicament introuvable."));
        if (m.getQuantiteStock() < quantite) {
            throw new IllegalArgumentException("Stock insuffisant : il reste " + m.getQuantiteStock()
                    + " unité(s) de " + m.getNom() + ".");
        }

        m.setQuantiteStock(m.getQuantiteStock() - quantite);
        medicamentRepository.save(m);

        Vente v = new Vente();
        v.setMedicament(m);
        v.setQuantite(quantite);
        v.setPrixTotal(m.getPrix().multiply(BigDecimal.valueOf(quantite)));
        v.setDateVente(LocalDateTime.now());
        v.setVendeur(vendeur);
        return venteRepository.save(v);
    }
}
