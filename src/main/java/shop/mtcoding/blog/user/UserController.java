package shop.mtcoding.blog.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
@RequiredArgsConstructor
@Controller
public class UserController {
    private final HttpSession session;
    private final UserRepository userRepository;
    @PostMapping("/join")
    public String join(UserRequest.JoinDTO requestDTO, HttpServletRequest request){
        System.out.println(requestDTO);
        if (requestDTO.getUsername().length()<3){
            request.setAttribute("status", 400);
            request.setAttribute("msg", "ID의 길이는 3자를 초과해야 합니다.");
        }
        User user = userRepository.findByUsername(requestDTO.getUsername());
        if (user==null){
            userRepository.save(requestDTO);
        }else {
            request.setAttribute("status",400);
            request.setAttribute("msg","ID가 존재합니다.");
        }

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
    public String updateForm() {
        return "user/updateForm";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }
}
