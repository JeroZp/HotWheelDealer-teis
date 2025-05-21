package com.hotwheels.dealer.controller.web;

import com.hotwheels.dealer.entity.Cliente;
import com.hotwheels.dealer.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class AuthController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginForm() {
        return "web/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        if (SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
            !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            return "redirect:/";
        }

        try {
            Optional<Cliente> clienteOpt = clienteService.findByEmail(email);

            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();

                if (clienteService.validarCredenciales(email, password)) {
                    // Guardar ID del cliente en la sesión
                    session.setAttribute("clienteId", cliente.getId());
                    session.setAttribute("clienteNombre", cliente.getNombre());

                    System.out.println("Cliente autenticado: " + cliente.getId() + " - " + cliente.getNombre());

                    redirectAttributes.addFlashAttribute("success", "¡Inicio de sesión exitoso!");
                    return "redirect:/";
                }
            }

            redirectAttributes.addFlashAttribute("error", "Credenciales inválidas. Intenta nuevamente.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al iniciar sesión: " + e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/registro")
    public String showRegistroForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "web/registro";
    }

    @PostMapping("/registro")
    public String processRegistration(@ModelAttribute("cliente") Cliente cliente,
                                      @RequestParam String confirmPassword,
                                      RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) {

        // Verificar que las contraseñas coincidan
        if (!cliente.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/registro";
        }

        try {
            // Verificar si el email ya existe
            if (clienteService.existeEmail(cliente.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "El email ya está registrado");
                return "redirect:/registro";
            }

            // Configurar el cliente
            cliente.setUsername(cliente.getEmail()); // Usar email como username
            cliente.setPassword(passwordEncoder.encode(cliente.getPassword())); // Encriptar contraseña
            cliente.setActivo(true);
            cliente.addRole("USER"); // Rol predeterminado

            // Guardar el cliente
            Cliente nuevoCliente = clienteService.save(cliente);

            // Auto-login después del registro exitoso
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    nuevoCliente, null, nuevoCliente.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);

            // Guardar ID del cliente en la sesión para el carrito
            HttpSession session = request.getSession();
            session.setAttribute("clienteId", nuevoCliente.getId());
            session.setAttribute("clienteNombre", nuevoCliente.getNombre());

            redirectAttributes.addFlashAttribute("success", "¡Bienvenido a HotWheels, " + nuevoCliente.getNombre() + "!");
            return "redirect:/";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/registro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Has cerrado sesión correctamente");
        return "redirect:/";
    }

    // Para pruebas
    @GetMapping("/test-login")
    public String testLogin(HttpSession session, RedirectAttributes redirectAttributes) {
        // Establece un ID real de cliente de tu base de datos
        Long clienteId = 1L;
        session.setAttribute("clienteId", clienteId);
        session.setAttribute("clienteNombre", "Usuario de Prueba");

        System.out.println("Sesión de prueba iniciada. ClienteId: " + clienteId);

        redirectAttributes.addFlashAttribute("success", "Sesión de prueba iniciada como Usuario de Prueba (ID: " + clienteId + ")");
        return "redirect:/";
    }
}