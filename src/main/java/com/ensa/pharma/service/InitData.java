package com.ensa.pharma.service;

import com.ensa.pharma.model.Medicament;
import com.ensa.pharma.model.Role;
import com.ensa.pharma.model.User;
import com.ensa.pharma.repository.MedicamentRepository;
import com.ensa.pharma.repository.RoleRepository;
import com.ensa.pharma.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

// Exécuté au démarrage : crée les rôles, les utilisateurs de test et quelques médicaments.
// On peut relancer l'application : rien n'est dupliqué.
@Component
public class InitData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MedicamentRepository medicamentRepository;
    private final PasswordEncoder passwordEncoder;

    public InitData(UserRepository userRepository, RoleRepository roleRepository,
                    MedicamentRepository medicamentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.medicamentRepository = medicamentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role admin = creerRole("ADMIN");
        Role pharmacien = creerRole("PHARMACIEN");
        Role caissier = creerRole("CAISSIER");
        Role client = creerRole("CLIENT");

        creerUser("admin", "admin123", admin);
        creerUser("pharma", "pharma123", pharmacien);
        creerUser("caisse", "caisse123", caissier);
        creerUser("client", "client123", client);   // en plus du sujet, pour tester le rôle CLIENT

        if (medicamentRepository.count() == 0) {
            creerMedicament("Amoxicilline 500mg", "Antibiotique à large spectre.", "45.00", 120, "2027-03-10");
            creerMedicament("Doliprane 500mg", "Antalgique et antipyrétique.", "25.50", 200, "2027-11-01");
            creerMedicament("Vitamine C 1000mg", "Complément pour renforcer les défenses.", "30.00", 80, "2027-06-15");
            creerMedicament("Bétadine Solution 10%", "Désinfectant pour usage externe.", "18.00", 5, "2027-01-30");
            creerMedicament("Nurofen 400mg", "Soulage la douleur et la fièvre.", "42.00", 0, "2027-02-20");
        }
    }

    private Role creerRole(String nom) {
        return roleRepository.findByNom(nom).orElseGet(() -> roleRepository.save(new Role(nom)));
    }

    private void creerUser(String username, String motDePasse, Role role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(motDePasse));   // BCrypt
        u.setEnabled(true);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        u.setRoles(roles);
        userRepository.save(u);
    }

    private void creerMedicament(String nom, String description, String prix, int stock, String expiration) {
        Medicament m = new Medicament();
        m.setNom(nom);
        m.setDescription(description);
        m.setPrix(new BigDecimal(prix));
        m.setQuantiteStock(stock);
        m.setDateExpiration(LocalDate.parse(expiration));
        medicamentRepository.save(m);
    }
}
