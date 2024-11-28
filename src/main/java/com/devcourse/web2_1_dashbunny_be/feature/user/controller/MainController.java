package com.devcourse.web2_1_dashbunny_be.feature.user.controller;

import com.devcourse.web2_1_dashbunny_be.domain.user.SocialUser;
import com.devcourse.web2_1_dashbunny_be.domain.user.User;
import com.devcourse.web2_1_dashbunny_be.feature.user.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping ("/login")
    public String login(Model model) {

        return "login";
    }

    @GetMapping("/moveAdditionalInfo")
    public String showRegistrationForm() {
        return "AdditionalInfo";
    }

    // 회원가입 추가정보 세션 저장

    /*    "phone": "01094861669",
          "name" : "김김김",
          "email" : "kim@kim.com",
          "birthday" : "990909-2",
          "password" : "z123456"    */
    @PostMapping("/api/auth/signUp/AdditionalInfo")
    public String processRegistrationForm(@ModelAttribute UserDTO userDTO, HttpSession session) {
        System.out.println("processRegistrationForm : " + userDTO);
        // 추가 정보를 세션에 저장
        session.setAttribute("AdditionalInfoUser", userDTO);
        // OAuth2 로그인 시작 (카카오 로그인 URL로 리디렉션)
        return "redirect:/oauth2/authorization/kakao";
    }

    @GetMapping("/main")
    public String main(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                model.addAttribute("user", user);
                log.info("user model : " + model.getAttribute("user"));
            }// OAuth2 카카오 로그인 사용자 처리
            else if (principal instanceof SocialUser) {
                SocialUser socialUser = (SocialUser) principal;
                model.addAttribute("user", socialUser);
                log.info("socialUser model : " + model.getAttribute("user"));
            }
        }
        return "main"; // main.html로 이동
    }

}