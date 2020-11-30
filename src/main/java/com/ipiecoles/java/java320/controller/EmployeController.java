package com.ipiecoles.java.java320.controller;

import com.ipiecoles.java.java320.model.Employe;
import com.ipiecoles.java.java320.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("employes")
public class EmployeController {
    @Autowired
    private EmployeRepository employeRepository;

    // 2 - Afficher un employé
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getEmployeById(@PathVariable Long id, final ModelMap model) {
        Optional<Employe> employeOptional = employeRepository.findById(id);
        // Gérer erreur 404
        model.put("employe", employeOptional.get());
        return "detail";
    }

    // 3 - Recherche par matricule
    @RequestMapping(value = "", params = "matricule", method = RequestMethod.GET)
    public String searchEmployeByMatricule(@RequestParam String matricule, final ModelMap model) {
        Employe employe = employeRepository.findByMatricule(matricule);
        // Gérer erreur 404
        model.put("employe", employe);
        return "detail";
    }

    // 4 - Liste des employés
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String listEmployes(final ModelMap model,
                               @RequestParam Integer page,
                               @RequestParam Integer size,
                               @RequestParam String sortDirection,
                               @RequestParam String sortProperty) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortProperty);
        Page<Employe> pageEmploye = employeRepository.findAll(pageRequest);
        model.put("employes", pageEmploye);
        model.put("pageNumber", page + 1);
        model.put("previousPage", page - 1);
        model.put("nextPage", page + 1);
        model.put("start", page * size + 1);
        model.put ("end", (page + 1) * size);
        return "listeEmployes";
    }
}
