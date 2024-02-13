package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import shop.mtcoding.blog._core.config.security.MyLoginUser;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final HttpSession session;
    private final BoardRepository boardRepository;
    @PostMapping("/board/{id}/update")
    public String update (@PathVariable int id, BoardRequest.UpdateDTO requestDTO, @AuthenticationPrincipal MyLoginUser myLoginUser){
//        // 1. 인증 체크
//        User sessionUser = (User) session.getAttribute("sessionUser");
//        if (sessionUser == null) {
//            return "redirect:/updateForm";
//        }
        // 2. 권한 체크
//        if (board == null){
//            return "error/400";
//        }
        Board board = boardRepository.findById(id);
        if (board.getUserId() != myLoginUser.getUser().getId()){
            return "error/403";
        }
        // 3. 핵심 로직
        boardRepository.update(requestDTO, id);
        return "redirect:/board/"+id;
    }
    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser){
//        User sessionUser = (User) session.getAttribute("sessionUser");
//        if (sessionUser == null) {
//            return "redirect:/loginForm";
//        }
        Board board = boardRepository.findById(id);
        if (board.getUserId() != myLoginUser.getUser().getId()){
            return "error/403";
        }
        request.setAttribute("board",board);
        return "board/updateForm";
    }
    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {
        // 1. 인증 체크
//    User sessionUser = (User) session.getAttribute("sessionUser");
//    if (sessionUser == null) {
//        return "redirect:/loginForm";
//    }

        // 2. 바디 데이터 확인 및 유효성 검사
        System.out.println(requestDTO);

        if (requestDTO.getTitle().length() > 30) {
            request.setAttribute("status", 400);
            request.setAttribute("msg", "title의 길이가 30자를 초과해서는 안되요");
            return "error/40x"; // BadRequest
        }

        // 3. 모델 위임
        // insert into board_tb(title, content, user_id, created_at) values(?,?,?, now());
        boardRepository.save(requestDTO, myLoginUser.getUser().getId());

        return "redirect:/";
    }


    @GetMapping("/")
    public String index(HttpServletRequest request) {
        List<Board> boardList = boardRepository.findAll();
        request.setAttribute("boardList", boardList);

        return "index";
    }

    //   /board/saveForm 요청(Get)이 온다
    @GetMapping("/board/saveForm")
    public String saveForm() {
        //   session 영역에 sessionUser 키값에 user 객체 있는지 체크
//        User sessionUser = (User) session.getAttribute("sessionUser");

        //   값이 null 이면 로그인 페이지로 리다이렉션
        //   값이 null 이 아니면, /board/saveForm 으로 이동
//        if (sessionUser == null) {
//            return "redirect:/loginForm";
//        }
        return "board/saveForm";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {
//        System.out.println("id : " + id);

        // 바디 데이터가 없으면 유효성 검사가 필요없지 ㅎ
        BoardResponse.DetailDTO responseDTO = boardRepository.findByIdUser(id);
//        User sessionUser = (User) session.getAttribute("sessionUser");
        boolean pageOwner;
        if(myLoginUser == null){
            pageOwner = false;
        }else{
            int 게시글작성자번호 = responseDTO.getUserId();
            int 로그인한사람의번호 = myLoginUser.getUser().getId();
            pageOwner = 게시글작성자번호 == 로그인한사람의번호;
        }
        request.setAttribute("pageOwner", pageOwner);
        request.setAttribute("board", responseDTO);
        return "board/detail";
    }
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser){
//        User sessionUser = (User) session.getAttribute("sessionUser");
//        if (sessionUser==null){
//            return "redirect:/loginForm";
//        }
        Board board = boardRepository.findById(id);
        if (board.getUserId() != myLoginUser.getUser().getId()){
            request.setAttribute("status",403);
            request.setAttribute("msg", "게시글을 삭제할 권한이 없습니다.");
            return "error/40x";
        }
        boardRepository.deleteById(id);
        return "redirect:/";
    }
}
