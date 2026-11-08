package com.elibrary.web;

import com.elibrary.service.UserRegistrationService;
import com.elibrary.web.form.RegistrationForm;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final UserRegistrationService registrationService;

    public AuthController(UserRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String login() { return "auth/login"; }

    @GetMapping("/register")
    public String registrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult bindingResult) {
        if (form.getPassword() != null && form.getConfirmPassword() != null
                && !Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "validation.password.mismatch");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            registrationService.register(form.getUsername(), form.getEmail(), form.getDisplayName(), form.getPassword());
        } catch (UserRegistrationService.UsernameAlreadyTakenException ex) {
            bindingResult.reject("auth.identifierTaken");
            return "auth/register";
        } catch (DataIntegrityViolationException ex) {
            bindingResult.reject("auth.identifierTaken");
            return "auth/register";
        }
        return "redirect:/login?registered";
    }
}
