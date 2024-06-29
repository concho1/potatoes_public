package com.goott.potatoes.qna.controller;

import com.goott.potatoes.qna.model.FAQ;
import com.goott.potatoes.qna.model.QNA;
import com.goott.potatoes.qna.model.QNAMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("qna")
public class QNAController {

    @Autowired
    private QNAMapper mapper;

    private final int rowsize = 10;
    private int totalRecord = 0;

    @GetMapping("/list")
    public ModelAndView QNAList(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ModelAndView mav = new ModelAndView("qna/qna_list");

        int page = 0;

        if(request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }else {
            page = 1;
        }

        List<QNA> qList = this.mapper.getList();
        String str = "";

        for(int i=0; i<qList.size(); i++) {
            if(qList.get(i).getQnaStatus() == 2) {
                String[] strs = qList.get(i).getUserNickname().split("%");
                String sr = strs[1];
                qList.get(i).setUserNickname(sr);
            }

        }

        Optional<HttpSession> session = Optional.ofNullable(request.getSession(false));

        if(session.isEmpty()) {
            mav.addObject("nickname", "");
            mav.addObject("rank", "");
        }else {

            HttpSession session1 = session.get();

            if(session1.getAttribute("userId") == null) {
                response.setContentType("text/html; charset=utf-8");
                PrintWriter out = response.getWriter();

                out.println("""
                                <script>
                                alert('회원가입 후 이용할 수 있습니다.')
                                history.back()
                                </script>
                            """);
                return null;
            }

            if(session1.getAttribute("userNickname") != null) {
                String nickname = session1.getAttribute("userNickname").toString();
                mav.addObject("nickname", nickname);
            }else {
                mav.addObject("nickname", "");
            }

            if(session1.getAttribute("userRank") != null) {
                String rank = session1.getAttribute("userRank").toString();
                mav.addObject("rank", rank);
            }else {
                mav.addObject("rank", "");
            }
        }

        mav.addObject("qList", qList);
        mav.addObject("page", page);
        mav.addObject("side", true);

        return mav;
    }


    @GetMapping("/faq")
    public ModelAndView faq() {
        ModelAndView mav = new ModelAndView("qna/qna_faq");

        List<FAQ> faqs = this.mapper.getFAQ();

        mav.addObject("FAQ", faqs);
        mav.addObject("side", true);

        return mav;
    }

    @GetMapping("/write")
    public ModelAndView write(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        Optional<HttpSession> session = Optional.ofNullable(request.getSession(false));

        if(session.isEmpty() || session.get().getAttribute("userId") == null) {
            out.println("""
                                <script>
                                alert('회원가입 후 이용할 수 있습니다.')
                                history.back()
                                </script>
                            """);
            return null;
        }

        return new ModelAndView("qna/qna_write").addObject("side", true);
    }

    @PostMapping("/write_ok")
    public void writeOk(QNA qdto, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();

        qdto.setUserNickname(session.getAttribute("userNickname").toString());

        System.out.println(qdto);

        int result = this.mapper.insertQuestion(qdto);

        if(result > 0) {
            out.println("""
                            <script>
                            alert('질문이 등록되었습니다.')
                            location.href='list'
                            </script>
                        """);
        }else {
            out.println("""
                            <script>
                            alert('질문 등록에 실패했습니다.')
                            history.back()
                            </script>
                        """);
        }


    }

    @GetMapping("cont")
    public ModelAndView cont(@RequestParam("no") int no, @RequestParam("page") int page, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("qna/qna_cont");

        HttpSession session = request.getSession(false);

        QNA cont = this.mapper.getQNA(no);
        QNA rel = new QNA();

        if(session.getAttribute("userRank") != null) {
            String rank = session.getAttribute("userRank").toString();
            mav.addObject("rank", rank);
        }else {
            mav.addObject("rank", "");
        }

        if(cont.getQnaStatus() == 1) {
            rel = this.mapper.getAnswer(cont.getQnaGroup());
        }else if(cont.getQnaStatus() == 2) {
            rel = this.mapper.getQNA(cont.getQnaGroup());

        }

        mav.addObject("rel", rel);
        mav.addObject("cont", cont);
        mav.addObject("page", page);
        mav.addObject("side", true);

        return mav;
    }

    @GetMapping("modify")
    public ModelAndView modify(@RequestParam("no") int no, @RequestParam("page") int page,
                               HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        response.setContentType("text/html; charset=utf-8");

        QNA cont = this.mapper.getQNA(no);

        if((!session.getAttribute("userNickname").equals(cont.getUserNickname()) && !session.getAttribute("userRank").equals("manager"))) {
            PrintWriter out = response.getWriter();
            out.println("""
                            <script>
                            alert('유효하지 않은 접근방식입니다.')
                            history.back()
                            </script>
                        """);
            return  null;
        }

        ModelAndView mav = new ModelAndView("qna/qna_mod");



        mav.addObject("cont", cont);
        mav.addObject("page", page);
        mav.addObject("side", true);

        return mav;
    }

    @PostMapping("mod_ok")
    public void modOk(QNA qdto, @RequestParam("page") int page,
                      HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int res = this.mapper.updateQNA(qdto);

        if(res > 0) {
            out.println("<script>");
            out.println("alert('QNA가 수정되었습니다.')");
            out.println("location.href='cont?no="+qdto.getNum()+"&page="+page+"'");
            out.println("</script>");
        }else {
            out.println("""
                            <script>
                            alert('QNA 수정에 실패했습니다.')
                            history.back()
                            </script>
                        """);
        }

    }

    @GetMapping("delete")
    public void delete(@RequestParam("no") int no, @RequestParam("page") int page,
                       HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int res = this.mapper.deleteQNA(no);

        if(res > 0) {
            this.mapper.deleteAnswer(no);
            out.println("<script>");
            out.println("alert('QNA가 삭제되었습니다.')");
            out.println("location.href='list?page="+page+"'");
            out.println("</script>");
        }else {
            out.println("""
                            <script>
                            alert('QNA 삭제에 실패했습니다.')
                            history.back()
                            </script>
                        """);
        }
    }

}
