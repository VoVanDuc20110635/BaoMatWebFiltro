package com.data.filtro.controller.user;

import com.data.filtro.model.Feedback;
import com.data.filtro.model.Product;
import com.data.filtro.model.User;
import com.data.filtro.service.FeedbackService;
import com.data.filtro.service.InputService;
import com.data.filtro.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static com.data.filtro.service.InputService.containsAllowedCharacters;
import static com.data.filtro.service.InputService.isStringLengthLessThan50;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    FeedbackService feedbackService;

    @Autowired
    InputService inputService;

    private String errorMessage;
    private String csrfToken;


    @GetMapping
    public String product(Model model) {
        String _csrfToken = generateRandomString();
        csrfToken = _csrfToken;
        return "user/product";
    }

    @GetMapping("/{id}")
    public String product(@PathVariable Integer id, HttpSession session, Model model) {
        String _csrfToken = generateRandomString();
        csrfToken = _csrfToken;
        System.out.println("csrfToken:" + _csrfToken);
        model.addAttribute("_csrfToken", _csrfToken);
        int currentProductId = id;
        long maxProductId = productService.countAll();
        List<Feedback> feedbackList = feedbackService.getAllFeedBackByProductId(id);
        int t1 = 13;
        long t2 = 24;
        Product product = productService.getProductById(id);
        List<Product> productList = productService.getTop4ProductsByFlavor(product.getFlavor().getId(), currentProductId);
        model.addAttribute("product", product);
        model.addAttribute("products", productList);
        model.addAttribute("currentProductId", currentProductId);
        model.addAttribute("maxProductId", maxProductId);
        model.addAttribute("feedbackList", feedbackList);
        model.addAttribute("_csrfToken", _csrfToken);
        productList.forEach(product1 -> System.out.println(product1.getProductName()));
        if (errorMessage != null){
            model.addAttribute("errorMessage", errorMessage);
            System.out.println(errorMessage);
        }
        errorMessage = null;
        return "user/product";
    }

//    //    Controller này là hack local nhưng cso CSRF token
//    @PostMapping("/{id}/feedback")
//    public String feedback(@RequestParam String content, @PathVariable Integer id, HttpSession session, Model model) {
//        System.out.println("Noi dung binh luan: " + content);
//        String decodedString = UriUtils.decode(content, StandardCharsets.UTF_8);
//        System.out.println("Noi dung binh luan sau khi giai ma: " + decodedString);
//        Feedback feedback = new Feedback();
//        feedback.setContent(decodedString);
//        feedback.setUser(null);
//
//        User user = (User) session.getAttribute("user");
//        System.out.println("Ten cua khach hang ben ban hack: " + user.getName());
//        feedback.setUser(user);
//        feedback.setProduct(productService.getProductById(id));
//        feedbackService.createFeedback(feedback);
//        System.out.println("Da xu ly xong yeu cau tu web 1");
//        return "redirect:/product/" + id;
//    }


    //    Controller này là hack local nhưng cso CSRF token
    @PostMapping("/{id}/feedback")
    public String feedback(@RequestParam String content, @RequestParam("_csrfParameterName") String csrfTokenForm, @PathVariable Integer id, HttpSession session, Model model) {
        System.out.println("Token duoc tao: " + csrfToken);
        System.out.println("Token gui tu request: " + csrfTokenForm);
        if (!csrfTokenForm.equals(csrfToken)) {
            String message = "Mã token không đúng";
            errorMessage = message;
            model.addAttribute("errorMessage", message);
            return "redirect:/product/" + id;
        }

        System.out.println("Noi dung binh luan: " + content);
        String decodedString = UriUtils.decode(content, StandardCharsets.UTF_8);
        System.out.println("Noi dung binh luan sau khi giai ma: " + decodedString);
        Feedback feedback = new Feedback();
        feedback.setContent(decodedString);
        feedback.setUser(null);

        User user = (User) session.getAttribute("user");
        System.out.println("Ten cua khach hang ben ban hack: " + user.getName());
        feedback.setUser(user);
        feedback.setProduct(productService.getProductById(id));
        feedbackService.createFeedback(feedback);
        System.out.println("Da xu ly xong yeu cau tu web 1");
        return "redirect:/product/" + id;
    }


//    Controller này là cái gốc
//    @PostMapping("/{id}/feedback")
//    public String feedback(@ModelAttribute Feedback feedback, @PathVariable Integer id, @RequestParam("_csrfParameterName") String csrfTokenForm, HttpSession session, Model model) {
//        System.out.println("CSRF token duoc tao: " + csrfToken);
//        System.out.println("CSRF token request: " + csrfTokenForm);
//        if (!csrfTokenForm.equals(csrfToken)) {
//            String message = "Mã token không đúng";
//            model.addAttribute("errorMessage", message);
//            return "redirect:/product/" + id;
//        }
//        User user = (User) session.getAttribute("user");
//        System.out.println("Ten cua khach hang ben ban hack: " + user.getName());
//        feedback.setUser(user);
//        feedback.setProduct(productService.getProductById(id));
//        feedbackService.createFeedback(feedback);
//        System.out.println("Da xu ly xong yeu cau tu web 1");
//        return "redirect:/product/" + id;
//    }
    public String generateRandomString() {
        return UUID.randomUUID().toString();
    }
}
