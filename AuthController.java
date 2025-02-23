package bigprojects.controller;

import bigprojects.dto.LoginRequest;
import bigprojects.model.User;
import bigprojects.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // Show Signup Page
    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup"; // Renders signup.html
    }

    // Handle Signup
    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, Model model) {
        if (userService.isEmailRegistered(user.getEmail())) {
            model.addAttribute("error", "Email is already registered.");
            return "signup";
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("error", "Password and Confirm Password do not match.");
            return "signup";
        }
        userService.signup(user);
        model.addAttribute("message", "Signup successful! Please log in.");
        return "redirect:/auth/login";
    }

    // Show Login Page
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login"; // Renders login.html
    }

    // Handle Login (Modified for Thymeleaf)
    @PostMapping("/login")
    public String login(@ModelAttribute("loginRequest") LoginRequest loginRequest, Model model, HttpSession session) {
        String loginMessage = userService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (loginMessage.equals("Login successful")) {
            session.setAttribute("loggedInUser", loginRequest.getEmail()); // Store user session
            return "redirect:/auth/home"; // Redirect to home page after login
        } else {
            model.addAttribute("error", "Invalid email or password.");
            return "login"; // Stay on login page with error message
        }
    }

    // Show Home Page (Only If User is Logged In)
    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/auth/login"; // Redirect to login if not logged in
        }

        model.addAttribute("userEmail", loggedInUser);
        return "home"; // Renders home.html
    }

    // Logout User
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clear user session
        return "redirect:/auth/login"; // Redirect to login page
    }
}
