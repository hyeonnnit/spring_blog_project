package shop.mtcoding.blog.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import shop.mtcoding.blog._core.config.security.MyLoginUser;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserRepository userRepository;
    private final HttpSession session;
    private final BCryptPasswordEncoder passwordEncoder;

    //    @PostMapping("/login")
//    public String login(UserRequest.LoginDTO requestDTO){
//        System.out.println(requestDTO);
//        if (requestDTO.getUsername().length() < 3) {
//            return "error/400";
//        }
//        User user = userRepository.findByUsernameAndPassword(requestDTO);
//        if (user == null){
//            return "error/401";
//        }else {
//            session.setAttribute("sessionUser", user);
//        }
//        return "redirect:/";
//    }
    @PostMapping("/join")
    public String join(UserRequest.JoinDTO requsetDTO){
        System.out.println(requsetDTO);
        String rawPassword = requsetDTO.getPassword();
        String encPassword = passwordEncoder.encode(rawPassword);
        requsetDTO.setPassword(encPassword);
        userRepository.save(requsetDTO);
        return "redirect:/loginForm";
    }
    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/user/updateForm")
    public String updateForm(HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {
        User user = userRepository.findByUsername(myLoginUser.getUsername());
        request.setAttribute("user",user);
        return "user/updateForm";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/";
    }
}
