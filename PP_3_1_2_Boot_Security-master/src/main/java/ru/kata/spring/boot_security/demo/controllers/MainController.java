package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RegistrationService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.*;

@Controller
public class MainController {
    private final UserService userService;
    private final RegistrationService registrationService;
    private final PasswordEncoder passwordEncoder;

    public MainController(UserService userService, RegistrationService registrationService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping(value = "/user")
    public String getHome(@AuthenticationPrincipal User activeUser, Model model) {
        model.addAttribute("roles", activeUser.getRoles());
        model.addAttribute("user", activeUser);
        return "home";
    }

    @GetMapping(value = "/admin")
    public String getAllUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin";
    }

    @PostMapping(value = "/createUser")
    public String newUser(@ModelAttribute("user") @Valid User user) {
        registrationService.register(user);
        return "redirect:/admin";
    }

    @GetMapping(value = "/admin/new")
    public String create(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "new";
    }

    @GetMapping(value = "/admin/edit/{id}")
    public String editUser(@PathVariable("id") int id, Model model) {
        User user = userService.findById(id);
        Set<Role> roles = user.getRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "edit";
    }

    @PostMapping(value = "/admin/edit")
    public String update(@ModelAttribute("user") @Valid User updatedUser, @ModelAttribute("roles") Set<Role> roles,
                         @RequestParam(value = "userId", required = false) Integer userId) {
        User user = userService.findById(userId);
        user.setName(updatedUser.getName());
        user.setLastName(updatedUser.getLastName());
        user.setAge(updatedUser.getAge());
        user.setEmail(updatedUser.getEmail());
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        userService.update(userId,user);
        return "redirect:/admin";
    }

    @PostMapping(value = "/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        userService.delete(id);
        return "redirect:/admin";
    }
    }
















