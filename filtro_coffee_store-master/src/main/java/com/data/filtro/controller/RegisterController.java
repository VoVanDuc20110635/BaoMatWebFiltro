package com.data.filtro.controller;

import com.data.filtro.exception.AccountNameExistException;
import com.data.filtro.exception.InputNotInvalidException;
import com.data.filtro.exception.PasswordDoNotMatchException;
import com.data.filtro.repository.UserRepository;
import com.data.filtro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;
import java.util.UUID;

import static com.data.filtro.service.InputService.*;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    UserService userService;

    private String csrfToken;

    @GetMapping
    public String showRegister(Model model) {
        String _csrfToken = generateRandomString();
        csrfToken = _csrfToken;
        System.out.println("csrfToken:" + _csrfToken);
        model.addAttribute("_csrfToken", _csrfToken);
        return "register";
    }

    @PostMapping
    public String registerUser(@RequestParam("userName") String userName,
                               @RequestParam("accountName") String accountName,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("repeatPassword") String repeatPassword,
                               @RequestParam("_csrfToken") String _csrfToken,
                               Model model) {
        if(!containsUTF8(userName) || !containsAllowedCharacters(accountName)
                || !containsAllowedCharacters(email) || !isStringLengthLessThan50(userName)
                || !isStringLengthLessThan50(accountName) || !isStringLengthLessThan50(password)){
            String message = "Tên người dùng, tên tài khoản, email chỉ được chứa các ký tự thường và dấu (), @ và " +
                    "độ dài dưới 50 ký tự";
            System.out.println(message);
            model.addAttribute("errorMessage", message);
            model.addAttribute("_csrfToken", csrfToken);
            return "register";
        }

        System.out.println("Sau khi nhan nut dang ky thi csrf token la: " + csrfToken);
        if (!_csrfToken.equals(csrfToken)) {
            System.out.println("csrfToken form: " + _csrfToken);
            String message = "Mã token không đúng";
            System.out.println(message);
            model.addAttribute("errorMessage", message);
            model.addAttribute("_csrfToken", csrfToken);
            return "register";
        }
        try {
            userService.registerUser(userName, accountName, email, password, repeatPassword);
        } catch (AccountNameExistException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("_csrfToken", csrfToken);
            return "register";
        } catch (PasswordDoNotMatchException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("_csrfToken", csrfToken);
            return "register";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String message = "Đăng ký thành công! Đăng nhập ngay!";
        model.addAttribute("message", message);
        model.addAttribute("_csrfToken", csrfToken);

        return "register";
    }
    public String generateRandomString() {
        return UUID.randomUUID().toString();
    }
}
