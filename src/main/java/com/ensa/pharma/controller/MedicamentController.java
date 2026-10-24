package com.ensa.pharma.controller;

import com.ensa.pharma.model.Medicament;
import com.ensa.pharma.repository.MedicamentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/medicaments")
public class MedicamentController {

    private final MedicamentRepository medicamentRepository;

    public MedicamentController(MedicamentRepository medicamentRepository) {
        this.medicamentRepository = medicamentRepository;
    }

    // ADMIN, PHARMACIEN : tous les médicaments | CLIENT : seulement les médicaments disponibles
    @GetMapping
    public String liste(Authentication auth, Model model) {
        boolean gestionnaire = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_PHARMACIEN"));

        List<Medicament> medicaments = gestionnaire
                ? medicamentRepository.findAll()
                : medicamentRepository.findByQuantiteStockGreaterThan(0);

        model.addAttribute("medicaments", medicaments);
        model.addAttribute("today", LocalDate.now());
        return "medicaments";
    }

    // ADMIN : formulaire d'ajout
    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("medicament", new Medicament());
        return "medicament-form";
    }

    // ADMIN : enregistrer un nouveau médicament
    @PostMapping("/ajouter")
    public String ajouter(@ModelAttribute Medicament medicament, RedirectAttributes ra) {
        medicament.setId(null);
        medicamentRepository.save(medicament);
        ra.addFlashAttribute("succes", "Médicament « " + medicament.getNom() + " » ajouté.");
        return "redirect:/medicaments";
    }

    // ADMIN : supprimer
    @PostMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            medicamentRepository.deleteById(id);
            ra.addFlashAttribute("succes", "Médicament supprimé.");
        } catch (Exception e) {
            // ex : le médicament a déjà des ventes enregistrées
            ra.addFlashAttribute("erreur", "Suppression impossible : ce médicament est lié à des ventes.");
        }
        return "redirect:/medicaments";
    }

    // ADMIN + PHARMACIEN : modifier le stock
    @PostMapping("/stock/{id}")
    public String modifierStock(@PathVariable Long id, @RequestParam int quantite, RedirectAttributes ra) {
        if (quantite < 0) {
            ra.addFlashAttribute("erreur", "Le stock ne peut pas être négatif.");
            return "redirect:/medicaments";
        }
        medicamentRepository.findById(id).ifPresent(m -> {
            m.setQuantiteStock(quantite);
            medicamentRepository.save(m);
            ra.addFlashAttribute("succes", "Stock de « " + m.getNom() + " » mis à jour : " + quantite);
        });
        return "redirect:/medicaments";
    }
}
