package com.example.demo.controller;

import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================================================
    // 결과 페이지
    // =========================================================
    @GetMapping("/result")
    public String resultPage() {
        return "user/result";
    }

    // =========================================================
    // 회원가입 페이지
    // =========================================================
    @GetMapping("/register")
    public String registerPage() {
        return "user/register";
    }

    // =========================================================
    // 회원가입 인증번호 발급
    // =========================================================
    @PostMapping("/register/auth")
    @ResponseBody
    public String registerAuth(
            @RequestParam("name") String name,
            @RequestParam("id") String id,
            @RequestParam("phone") String phone,
            @RequestParam("birth") String birth,
            @RequestParam("email") String email,
            HttpSession session
    ) {

        try {

            System.out.println("===== 회원가입 인증 요청 =====");
            System.out.println("name  = " + name);
            System.out.println("id    = " + id);
            System.out.println("phone = " + phone);
            System.out.println("birth = " + birth);
            System.out.println("email = " + email);

            // 아이디 / 전화번호 중복 확인
            userService.checkRegisterInfo(
                    id,
                    phone
            );

            // 회원가입 정보를 세션에 임시 저장
            session.setAttribute(
                    "registerName",
                    name
            );

            session.setAttribute(
                    "registerId",
                    id
            );

            session.setAttribute(
                    "registerPhone",
                    phone
            );

            session.setAttribute(
                    "registerBirth",
                    birth
            );

            session.setAttribute(
                    "registerEmail",
                    email
            );

            // 회원가입용 인증번호 발급
            String code =
                    userService.createVerificationCode(
                            phone,
                            "R"
                    );

            // 테스트용으로 인증번호 반환
            return code;

        } catch (Exception e) {

            e.printStackTrace();

            return "ERROR";
        }
    }

    // =========================================================
    // 회원가입 인증번호 확인
    // =========================================================
    @PostMapping("/register/verify")
    @ResponseBody
    public String registerVerify(
            @RequestParam("authCode") String authCode,
            HttpSession session
    ) {

        try {

            String phone =
                    (String) session.getAttribute(
                            "registerPhone"
                    );

            if (phone == null) {
                return "NO_REGISTER";
            }

            boolean result =
                    userService.verifyCode(
                            phone,
                            "R",
                            authCode
                    );

            if (!result) {
                return "FAIL";
            }

            // 세션에 임시 저장했던 회원정보 가져오기
            String name =
                    (String) session.getAttribute(
                            "registerName"
                    );

            String id =
                    (String) session.getAttribute(
                            "registerId"
                    );

            String birth =
                    (String) session.getAttribute(
                            "registerBirth"
                    );

            String email =
                    (String) session.getAttribute(
                            "registerEmail"
                    );

            // 인증 성공 후 실제 회원정보 저장
            userService.completeRegister(
                    name,
                    id,
                    phone,
                    birth,
                    email
            );

            // 임시 회원가입 정보 삭제
            session.removeAttribute("registerName");
            session.removeAttribute("registerId");
            session.removeAttribute("registerPhone");
            session.removeAttribute("registerBirth");
            session.removeAttribute("registerEmail");

            return "SUCCESS";

        } catch (Exception e) {

            e.printStackTrace();

            return "ERROR";
        }
    }

    // =========================================================
    // 로그인 페이지
    // =========================================================
    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    // =========================================================
    // 로그인 인증번호 발급
    // =========================================================
    @PostMapping("/login/auth")
    @ResponseBody
    public String loginAuth(
            @RequestParam("id") String id,
            @RequestParam("phone") String phone
    ) {

        try {

            Map<String, Object> user =
                    userService.findUserForLogin(
                            id,
                            phone
                    );

            if (user == null) {
                return "USER_NOT_FOUND";
            }

            String code =
                    userService.createVerificationCode(
                            phone,
                            "L"
                    );

            return code;

        } catch (Exception e) {

            e.printStackTrace();

            return "ERROR";
        }
    }

    // =========================================================
    // 로그인 인증번호 확인
    // =========================================================
    @PostMapping("/login/verify")
    @ResponseBody
    public String loginVerify(
            @RequestParam("id") String id,
            @RequestParam("phone") String phone,
            @RequestParam("authCode") String authCode,
            HttpSession session
    ) {

        try {

            Map<String, Object> user =
                    userService.findUserForLogin(
                            id,
                            phone
                    );

            if (user == null) {
                return "USER_NOT_FOUND";
            }

            boolean result =
                    userService.verifyCode(
                            phone,
                            "L",
                            authCode
                    );

            if (!result) {
                return "FAIL";
            }

            session.setAttribute(
                    "loginId",
                    id
            );

            session.setAttribute(
                    "userNumber",
                    user.get("SP_UI_No")
            );

            return "SUCCESS";

        } catch (Exception e) {

            e.printStackTrace();

            return "ERROR";
        }
    }

    // =========================================================
    // 로그아웃
    // =========================================================
    @GetMapping("/logout")
    public String logout(
            HttpSession session
    ) {

        session.invalidate();

        return "redirect:/";
    }
}