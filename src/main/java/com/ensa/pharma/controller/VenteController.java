package com.ensa.pharma.controller;

import com.ensa.pharma.repository.MedicamentRepository;
import com.ensa.pharma.repository.VenteRepository;
import com.ensa.pharma.service.VenteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventes")
public class VenteController {

    private final MedicamentRepository medicamentRepository;
    private final VenteRepository venteRepository;
    private final VenteService venteService;

    public VenteController(MedicamentRepository medicamentRepository,
                           VenteRepository venteRepository,
                           VenteService venteService) {
        this.medicamentRepository = medicamentRepository;
        this.venteRepository = venteRepository;
        this.venteService = venteService;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("medicaments", medicamentRepository.findByQuantiteStockGreaterThan(0));
        model.addAttribute("ventes", venteRepository.findTop20ByOrderByDateVenteDesc());
        return "ventes";
    }

    @PostMapping("/enregistrer")
    public String enregistrer(@RequestParam Long medicamentId,
                              @RequestParam int quantite,
                              Authentication auth,
                              RedirectAttributes ra) {
        try {
            venteService.enregistrer(medicamentId, quantite, auth.getName());
            ra.addFlashAttribute("succes", "Vente enregistrée.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/ventes";
    }
}
