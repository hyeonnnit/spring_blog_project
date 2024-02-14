package shop.mtcoding.blog._core.config.security;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRepository;
@RequiredArgsConstructor
@Service
public class MyLoginService implements UserDetailsService {
    private final UserRepository userRepository;
    private final HttpSession session;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername: "+username);
        User user = userRepository.findByUsername(username);
        if (user==null){
            System.out.println("user null");
            return null;
        }else {
            System.out.println("user find");
            session.setAttribute("sessionUser",user);
            return new MyLoginUser(user);
        }
    }
}
