package com.ipiecoles.java.java320.controller;

import com.ipiecoles.java.java320.model.Commercial;
import com.ipiecoles.java.java320.model.Employe;
import com.ipiecoles.java.java320.model.Manager;
import com.ipiecoles.java.java320.model.Technicien;
import com.ipiecoles.java.java320.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityNotFoundException;
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
        if(employeOptional.isEmpty()){
            throw new EntityNotFoundException("L'employé d'identifiant " + id + " n'a pas été trouvé !");
        }
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
                               @RequestParam(defaultValue = "0") Integer page,
                               @RequestParam(defaultValue = "10") Integer size,
                               @RequestParam(defaultValue = "ASC") String sortDirection,
                               @RequestParam(defaultValue = "matricule") String sortProperty) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortProperty);
        Page<Employe> pageEmploye = employeRepository.findAll(pageRequest);
        model.put("employes", pageEmploye);
        model.put("pageNumber", page + 1);
        model.put("previousPage", page - 1);
        model.put("nextPage", page + 1);
        model.put("start", page * size + 1);
        model.put ("end", (page) * size + pageEmploye.getNumberOfElements());
        return "listeEmployes";
    }


    // Un Request Mapping pour chaque type d'employé pour gérer la sauvegarde
    @RequestMapping(method= RequestMethod.GET, value = "/new/{typeEmploye}")
    public String newEmploye(@PathVariable String typeEmploye, final ModelMap model){
        switch (typeEmploye.toLowerCase()){
            case "technicien": model.put("employe", new Technicien()); break;
            case "commercial": model.put("employe", new Commercial()); break;
            case "manager": model.put("employe", new Manager()); break;
        }
        return "detail";
    }

    // 5. Création et modification d'un Commercial (employes/commercial)
    @RequestMapping(method = RequestMethod.POST, value = "/commercial", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView createOrSaveCommercial(Commercial employe){
        return saveEmploye(employe);
    }

    // 8.a Création et modification d'un Technicien (employes/technicien)
    @RequestMapping(method = RequestMethod.POST, value = "/technicien", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView createOrSaveTechnicien(Technicien employe){
        return saveEmploye(employe);
    }

    // 9.a Création et modification d'un Manager (employes/manager)
    @RequestMapping(method = RequestMethod.POST, value = "/manager", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView createOrSaveManager(Manager employe){
        return saveEmploye(employe);
    }

    // Redirection vers l'employé venant d'être crée
    private RedirectView saveEmploye(Employe employe){
        employe = employeRepository.save(employe);
        return new RedirectView("/employes/" + employe.getId());
    }

    // 7. Suppression => Rediriger vers la liste des employés
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/delete")
    public RedirectView deleteEmploye(@PathVariable Long id){
        if(!employeRepository.existsById(id)){
            throw new EntityNotFoundException("L'employé d'identifiant " + id + " n'a pas été trouvé !");
        }
        employeRepository.deleteById(id);
        return new RedirectView("/employes?page=0&size=10&sortProperty=matricule&sortDirection=ASC");
    }
}
