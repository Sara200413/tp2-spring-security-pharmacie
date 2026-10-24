package com.ensa.pharma.controller;

import com.ensa.pharma.model.Role;
import com.ensa.pharma.model.User;
import com.ensa.pharma.repository.RoleRepository;
import com.ensa.pharma.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;

// Toutes les routes /admin/** sont réservées au rôle ADMIN (voir SecurityConfig)
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin";
    }

    @PostMapping("/users/creer")
    public String creer(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam Long roleId,
                        RedirectAttributes ra) {
        if (username.isBlank() || password.length() < 6) {
            ra.addFlashAttribute("erreur", "Nom d'utilisateur obligatoire et mot de passe de 6 caractères minimum.");
            return "redirect:/admin";
        }
        if (userRepository.existsByUsername(username)) {
            ra.addFlashAttribute("erreur", "Le nom d'utilisateur « " + username + " » existe déjà.");
            return "redirect:/admin";
        }
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            ra.addFlashAttribute("erreur", "Rôle introuvable.");
            return "redirect:/admin";
        }

        User u = new User();
        u.setUsername(username.trim());
        u.setPassword(passwordEncoder.encode(password));   // on crypte avant d'enregistrer
        u.setEnabled(true);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        u.setRoles(roles);
        userRepository.save(u);

        ra.addFlashAttribute("succes", "Utilisateur « " + username + " » créé avec le rôle " + role.getNom() + ".");
        return "redirect:/admin";
    }

    @PostMapping("/users/supprimer/{id}")
    public String supprimer(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        userRepository.findById(id).ifPresent(u -> {
            if (u.getUsername().equals(auth.getName())) {
                ra.addFlashAttribute("erreur", "Tu ne peux pas supprimer ton propre compte.");
            } else {
                userRepository.delete(u);
                ra.addFlashAttribute("succes", "Utilisateur « " + u.getUsername() + " » supprimé.");
            }
        });
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/activer")
    public String activerDesactiver(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        userRepository.findById(id).ifPresent(u -> {
            if (u.getUsername().equals(auth.getName())) {
                ra.addFlashAttribute("erreur", "Tu ne peux pas désactiver ton propre compte.");
            } else {
                u.setEnabled(!u.isEnabled());
                userRepository.save(u);
                ra.addFlashAttribute("succes", "Compte « " + u.getUsername() + " » "
                        + (u.isEnabled() ? "activé." : "désactivé."));
            }
        });
        return "redirect:/admin";
    }
}
