package com.example.simpleSite.controller;

import com.example.simpleSite.models.Role;
import com.example.simpleSite.models.User;
import com.example.simpleSite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("{user}")
    public String userSave(@ModelAttribute User user,
                           @RequestParam Map<String, String> role) {
        userService.saveUser(user, role);
        return "redirect:/user";
    }

    @GetMapping("profile")
    public String getProfile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("profile")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String email) {
        userService.updateProfile(user, username, password, email);

        return "redirect:/user/profile";
    }

    @GetMapping("subscribe/{user}")
    public String subscribe(@AuthenticationPrincipal User currentUser,
                            @PathVariable User user) {
        userService.subscribe(currentUser, user);
        return "redirect:/user-message/" + user.getId();
    }

    @GetMapping("unsubscribe/{user}")
    public String unsubscribe(@AuthenticationPrincipal User currentUser,
                            @PathVariable User user) {
        userService.unsubscribe(currentUser, user);
        return "redirect:/user-message/" + user.getId();
    }

    @GetMapping("{type}/{user}/list")
    public String userList(@PathVariable User user,
                           @PathVariable String type,
                           Model model) {
        model.addAttribute("userChannel", user);
        model.addAttribute("type", type);
        if ("subscriptions".equals(type)) {
            model.addAttribute("users", user.getSubscriptions());
        } else {
            model.addAttribute("users", user.getSubscribers());
        }

        return "subscriptions";
    }
}
