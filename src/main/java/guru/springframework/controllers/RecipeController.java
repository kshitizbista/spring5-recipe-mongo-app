package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@Slf4j
public class RecipeController {

    private static final String RECIPE_FORM_URL = "recipe/recipeForm";

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findById(id));
        return "recipe/show";
    }

    @GetMapping("/recipe/new")
    public String newRecipe(Model model) {
        model.addAttribute("recipe", new RecipeCommand());
        return RECIPE_FORM_URL;

    }

    @GetMapping("/recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return RECIPE_FORM_URL;
    }

    @GetMapping("/recipe/{id}/delete")
    public String deleteRecipeById(@PathVariable String id) {
        recipeService.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("recipe")
    public String saveOrUpdateRecipe(@Valid @ModelAttribute("recipe") RecipeCommand command, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));
            return RECIPE_FORM_URL;
        }
        RecipeCommand savedRecipeCommand = recipeService.saveRecipeCommand(command);
        return "redirect:/recipe/" + savedRecipeCommand.getId() + "/show";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(Exception ex) {
        log.error("Handling not found exception");
        log.error(ex.getMessage());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("404error");
        modelAndView.addObject("exception", ex);
        return modelAndView;
    }


}
