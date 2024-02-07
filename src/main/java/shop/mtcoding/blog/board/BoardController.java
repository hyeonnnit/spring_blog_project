package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import shop.mtcoding.blog.user.User;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {
    private final HttpSession session;
    private final BoardRepository boardRepository;

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable int id, BoardRequest.UpdateDTO requestDTO){
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser==null){
            return "redirect:/updateForm";
        }
        Board board = boardRepository.findById(id);
        if (board==null){
            return "error/400";
        }
        if (board.getUserId() != sessionUser.getId()){
            return "error/403";
        }
        boardRepository.update(requestDTO, id);
        return "redirect:/board/"+id;
    }
    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request){
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null){
            return "redirect:/loginForm";
        }
        Board board = boardRepository.findById(id);
        if (board.getUserId() != sessionUser.getId()){
            return "error/403";
        }
        request.setAttribute("board",board);
        return "board/updateForm";
    }
    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request){
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null){
            return "redirect:/loginForm";
        }
        System.out.println(requestDTO);
        if (requestDTO.getTitle().length()>30){
            request.setAttribute("status",400);
            request.setAttribute("msg","title의 길이가 30자를 초과해서는 안됩니다.");
            return "error/40x";
        }
        boardRepository.save(requestDTO,sessionUser.getId());
        return "redirect:/";
    }
    @GetMapping({ "/", "/board" })
    public String index(HttpServletRequest request) {
        List<Board> boardList = boardRepository.findAll();
        request.setAttribute("boardList",boardList);
        return "index";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser==null){
            return "redirect:/loginForm";
        }
        return "board/saveForm";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, HttpServletRequest request) {
//        System.out.println("id: "+id);
        BoardResponse.DetailDTO responseDTO = boardRepository.findByIdUser(id);
        User sessionUser = (User) session.getAttribute("sessionUser");
        int 게시글작성자번호 = responseDTO.getUserId();
        int 로그인한사람의번호 = sessionUser.getId();
        boolean pageOwner = 게시글작성자번호 == 로그인한사람의번호;
        request.setAttribute("board", responseDTO);
        request.setAttribute("pageOwner", pageOwner);
        return "board/detail";
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id, HttpServletRequest request){
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser==null){
            return "redirect:/loginForm";
        }
        Board board = boardRepository.findById(id);
        if (board.getUserId() != sessionUser.getId()){
            request.setAttribute("status",403);
            request.setAttribute("msg", "게시글을 삭제할 권한이 없습니다.");
            return "error/40x";
        }
        boardRepository.deleteById(id);
        return "redirect:/";
    }
}
