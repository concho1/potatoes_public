package com.goott.potatoes.manager.controller;

import com.goott.potatoes.Hamster.model.missing;
import com.goott.potatoes.Hamster.model.pickup;
import com.goott.potatoes.common.service.ImageService;
import com.goott.potatoes.esh.boardModel.Board;
import com.goott.potatoes.esh.boardModel.BoardReply;
import com.goott.potatoes.manager.model.ManagerMapper;
import com.goott.potatoes.manager.model.SessionListener;
import com.goott.potatoes.manager.model.UserCnt;
import com.goott.potatoes.qna.model.FAQ;
import com.goott.potatoes.qna.model.QNA;
import com.goott.potatoes.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("manager")
public class ManagerController {

    @Autowired
    private ManagerMapper mapper;

    @Autowired
    private ImageService imageService;

    @GetMapping("/main")
    public ModelAndView manager_main(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ModelAndView mav = new ModelAndView("manager/manager_test");

        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        Optional<HttpSession> session = Optional.ofNullable(request.getSession(false));
        if(session.isEmpty()) {
            out.println("""
                            <script>
                            alert('관리자만 사용이 가능한 페이지입니다.')
                            location.href='/login'
                            </script>
                        """);
        }else {
            HttpSession ses1 = session.get();

            if(ses1.getAttribute("userRank") == null || !ses1.getAttribute("userRank").equals("manager")) {
                out.println("""
                                <script>
                                alert('관리자만 사용이 가능한 페이지입니다.')
                                location.href='/login'
                                </script>
                            """);
            }
        }


        // For Chart & sales, revenue, customers card
        int totalUser = 0;
        int totalGuest = 0;
        int totalJoin = 0;
        int period = 14;

        LocalDateTime base = LocalDateTime.now();
        Month mon = base.getMonth();

        LocalDateTime ldt = LocalDateTime.of(base.getYear(), mon.getValue(), base.getDayOfMonth(), 0, 0, 0);

        // 1일 가입자, 접속자를 구하기 위한 로직
        String date = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        UserCnt user = this.mapper.getUserCnt(date);

        String ytdDate = ldt.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        UserCnt ytdDto = this.mapper.getUserCnt(ytdDate);

        // 2주 누적 가입자, 접속자를 구하기 위한 로직
        for(int i=0; i<period; i++){
            String date2 = ldt.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            UserCnt dto = this.mapper.getUserCnt(date2);
            if(dto != null) {
                totalUser += dto.getUserCount();
                totalGuest += dto.getGuestCount();
                totalJoin += dto.getJoinCount();
            }
        }

        // 분실물 게시판 글 수를 구하기 위한 로직
        int totalMissing = this.mapper.countMissing();


        // For Q&A
        List<QNA> qList = this.mapper.getQNAList();


        // For 회원관리
        int size = 6;
        List<User> uList = this.mapper.getUserListForMain(size);

        // For F.A.Q
        List<FAQ> fList = this.mapper.getFAQListForMain();

        mav.addObject("todayUser", user);
        mav.addObject("ytdUser", ytdDto);
        mav.addObject("TotalUser", totalUser);
        mav.addObject("TotalGuest", totalGuest);
        mav.addObject("TotalJoin", totalJoin);
        mav.addObject("UList", uList);
        mav.addObject("QList", qList);
        mav.addObject("FList", fList);
        mav.addObject("totalMissing", totalMissing);
        mav.addObject("side_manager", true);


        return mav;
    }

    @PostMapping("/count")
    @ResponseBody
    public ModelAndView getCount(HttpServletResponse response, @RequestParam("count") int count,
                                 @RequestParam("title") String title, @RequestParam("type") String type) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        LocalDateTime base = LocalDateTime.now();
        Month mon = base.getMonth();

        LocalDateTime ldt = LocalDateTime.of(base.getYear(), mon.getValue(), base.getDayOfMonth(), 0, 0, 0);

        String date = "";
        UserCnt user = null;

        int totalUser = 0;
        int totalGuest = 0;
        int totalJoin = 0;

        if(count == 0) {
            date = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            user = this.mapper.getUserCnt(date);
            totalUser = user.getUserCount();
            totalGuest = user.getGuestCount();
            totalJoin = user.getJoinCount();
        }else {
            for(int i=0; i<count; i++) {
                date = ldt.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                user = this.mapper.getUserCnt(date);
                if(user == null) {
                    break;
                }
                totalUser += user.getUserCount();
                totalGuest += user.getGuestCount();
                totalJoin += user.getJoinCount();
            }

        }

        if(title.equals("접속")) {
            if(type.equals("회원")) {
                out.println(Integer.toString(totalUser));
            }else {
                out.println(Integer.toString(totalGuest));
            }
        }else {
            out.println(Integer.toString(totalJoin));
        }

        return null;
    }

    @PostMapping("/info")
    @ResponseBody
    public ModelAndView getInfo(@RequestParam("userPeriod") String userPeriod, @RequestParam("guestPeriod") String guestPeriod,
                                @RequestParam("joinPeriod") String joinPeriod, HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        LocalDateTime base = LocalDateTime.now();
        Month mon = base.getMonth();

        LocalDateTime ldt = LocalDateTime.of(base.getYear(), mon.getValue(), base.getDayOfMonth(), 0, 0, 0);

        int userCount = 0;
        int guestCount = 0;
        int joinCount = 0;

        String date = "";
        String user = "";
        String guest = "";
        String join = "";



        if(userPeriod.equals("1일")) {
            userCount = 1;
            for(int i=userCount; i<userCount*2; i++) {
                date = ldt.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                UserCnt dto = this.mapper.getUserCnt(date);
                user = Integer.toString(dto.getUserCount());
            }
        }else{
            int result = 0;

            if(userPeriod.equals("1주")) {
                userCount = 7;
            }else {
                userCount = 30;
            }

            for(int i=userCount; i<userCount*2; i++) {
                date = ldt.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                UserCnt dto = this.mapper.getUserCnt(date);
                if(dto == null) {
                    break;
                }
                result += dto.getUserCount();
            }

            user = Integer.toString(result);
        }

        if(guestPeriod.equals("1일")) {
            guestCount = 1;
            for(int i=guestCount; i<guestCount*2; i++) {
                date = ldt.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                UserCnt gdto = this.mapper.getUserCnt(date);
                guest = Integer.toString(gdto.getGuestCount());
            }
        }else{
            int result = 0;

            if(guestPeriod.equals("1주")) {
                guestCount = 7;
            }else {
                guestCount = 30;
            }

            for(int i=guestCount; i<guestCount*2; i++) {
                date = ldt.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                UserCnt gdto = this.mapper.getUserCnt(date);
                if(gdto == null) {
                    break;
                }
                result += gdto.getGuestCount();
            }

            guest = Integer.toString(result);
        }

        if(joinPeriod.equals("1일")) {
            joinCount = 1;
            for(int i=joinCount; i<joinCount*2; i++) {
                date = ldt.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                UserCnt jdto = this.mapper.getUserCnt(date);
                join = Integer.toString(jdto.getJoinCount());
            }
        }else{
            int result = 0;

            if(joinPeriod.equals("1주")) {
                joinCount = 7;
            }else {
                joinCount = 30;
            }

            for(int i=joinCount; i<joinCount*2; i++) {
                date = ldt.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                UserCnt jdto = this.mapper.getUserCnt(date);
                if(jdto == null) {
                    break;
                }
                result += jdto.getJoinCount();
            }

            join = Integer.toString(result);
        }

        out.println(user+":"+guest+":"+join);

        return null;
    }

    @GetMapping("/delete_user")
    public void deleteUser(@RequestParam("id") String id, HttpServletResponse response, HttpServletRequest request) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int check = this.mapper.deleteUser(id);

        if(check > 0) {

            HttpSession session = SessionListener.getSessionByUserId(id);
            if(session != null) {
                session.invalidate();
            }else {
                out.println("""
                                <script>
                                alert('세션 삭제에 실패하였습니다.')
                                history.back()
                                </script>
                            """);
            }

            out.println("""
                            <script>
                            alert('회원이 강퇴되었습니다')
                            location.href='main'
                            </script>
                        """);
        }else {
            out.println("""
                            <script>
                            alert('회원 강퇴에 실패했습니다.')
                            history.back()
                            </script>
                        """);
        }
    }

    @PostMapping("board_main")
    @ResponseBody
    public ModelAndView boardMain(@RequestParam("boardType") String boardType, HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int size = 5;
        StringBuilder json = new StringBuilder("[");

        if(boardType.equals("분실")) {
            List<missing> mList = null;

            mList = this.mapper.getMissingListForMain(size);

            if(mList != null) {
                for (missing mdto : mList) {
                    if (mdto != null) {
                        json.append("{");

                        json.append("\"num\" : ");
                        json.append("\"").append(Integer.toString(mdto.getMisNum())).append("\"");
                        json.append(", ");

                        json.append("\"nickname\" : ");
                        json.append("\"").append(mdto.getUserId()).append("\"");
                        json.append(", ");

                        json.append("\"title\" : ");
                        json.append("\"").append(mdto.getTitle()).append("\"");
                        json.append(", ");

                        json.append("\"view_cnt\" : ");
                        json.append("\"").append(Integer.toString(mdto.getViewCnt())).append("\"");
                        json.append(", ");

                        json.append("\"created_at\" : ");
                        json.append("\"").append(mdto.getCreatedAt().toString().substring(5, 10)).append("\"");
                        json.append("}, ");
                    }
                }
            }

        }else if(boardType.equals("자유")) {
            List<Board> bList = null;

            bList = this.mapper.getBoardListForMain(size);

            if(bList != null) {
                for (Board bdto : bList) {
                    if (bdto != null) {
                        json.append("{");

                        json.append("\"num\" : ");
                        json.append("\"").append(Integer.toString(bdto.getBoardNum())).append("\"");
                        json.append(", ");

                        json.append("\"nickname\" : ");
                        json.append("\"").append(bdto.getUserNickname()).append("\"");
                        json.append(", ");

                        json.append("\"title\" : ");
                        json.append("\"").append(bdto.getTitle()).append("\"");
                        json.append(", ");

                        json.append("\"view_cnt\" : ");
                        json.append("\"").append(Integer.toString(bdto.getViewCnt())).append("\"");
                        json.append(", ");

                        json.append("\"created_at\" : ");
                        json.append("\"").append(bdto.getCreatedAt().toString().substring(5, 10)).append("\"");
                        json.append("}, ");
                    }
                }
            }
        }else {
            List<pickup> pList = null;

            pList = this.mapper.getPickUpListForMain(size);

            if(pList != null) {
                for (pickup bdto : pList) {
                    if (bdto != null) {
                        json.append("{");

                        json.append("\"num\" : ");
                        json.append("\"").append(Integer.toString(bdto.getPickNum())).append("\"");
                        json.append(", ");

                        json.append("\"nickname\" : ");
                        json.append("\"").append(bdto.getUserId()).append("\"");
                        json.append(", ");

                        json.append("\"title\" : ");
                        json.append("\"").append(bdto.getTitle()).append("\"");
                        json.append(", ");

                        json.append("\"view_cnt\" : ");
                        json.append("\"").append(Integer.toString(bdto.getViewCnt())).append("\"");
                        json.append(", ");

                        json.append("\"created_at\" : ");
                        json.append("\"").append(bdto.getCreatedAt().toString().substring(5, 10)).append("\"");
                        json.append("}, ");
                    }
                }
            }
        }

        if(json.length() > 1 && json.charAt(json.length() - 2) == ',') {
            json.setLength(json.length() - 2);
        }

        json.append("]");

        out.println(json.toString());

        out.close();

        return null;
    }

    @GetMapping("/answer_qna")
    public ModelAndView answer(@RequestParam("no") int no, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("manager/answer_qna");

        int page = 0;

        if(request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        QNA cont = this.mapper.getQNA(no);

        mav.addObject("Cont", cont);
        mav.addObject("page", page);
        mav.addObject("side_manager", true);
        return mav;
    }

    @PostMapping("answer_ok")
    public void answerOk(HttpServletRequest request, HttpServletResponse response,
                         @RequestParam("qnaGroup") int group, @RequestParam("cont") String cont,
                         @RequestParam("num") int num, @RequestParam("nickname") String nickname) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int page = Integer.parseInt(request.getParameter("page"));

        System.out.println("page : " + page);

        HttpSession session = request.getSession(false);

        QNA qdto = new QNA();

        String re_title = "문의에 대한 답변입니다.";
        String re_name = "";

        if(session.getAttribute("userNickname") != null) {
            re_name = session.getAttribute("userNickname").toString() + "%" + nickname;
        }

        qdto.setUserNickname(re_name);
        qdto.setTitle(re_title);
        qdto.setCont(cont);
        qdto.setQnaGroup(group);

        int result = this.mapper.answerQNA(qdto);

        if(result > 0) {
            this.mapper.updateQNAStatus(num);

            if(page == 0) {
                out.println("""
                                <script>
                                alert('답변이 등록되었습니다.')
                                location.href='main'
                                </script>
                            """);
            }else {
                out.println("<script>");
                out.println("alert('답변이 등록되었습니다.')");
                out.println("location.href='../qna/list?page=" + page +"'");
                out.println("</script>");
            }
        }else {
            out.println("""
                            <script>
                            alert('답변 등록에 실패했습니다.')
                            history.back()
                            </script>
                        """);
        }
    }

    @GetMapping("/users")
    public ModelAndView users() {
        ModelAndView mav = new ModelAndView("manager/manager_user");

        List<User> userList = this.mapper.getAllUserList();

        for(User udto : userList) {
            udto.setImgKey(this.imageService.findImageByKey(udto.getImgKey()).get().getUrl());
        }

        mav.addObject("uList", userList);
        mav.addObject("side_manager", true);

        return mav;
    }

    @GetMapping("/board")
    public ModelAndView goBoard(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("manager/manager_board");

        int page = 0;

        if(request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }else {
            page = 1;
        }

        List<Board> bList = this.mapper.getAllBoard();

        for(Board bdto : bList) {
            bdto.setImgKey(this.imageService.findImageByKey(bdto.getImgKey()).get().getUrl());
            bdto.setCommentCount(this.mapper.getBoardReplyCount(bdto.getBoardNum()));
        }

        mav.addObject("bList", bList);
        mav.addObject("page", page);
        mav.addObject("side_manager", true);
        mav.addObject("side_manager_board", true);

        return mav;
    }

    @GetMapping("/board_cont")
    public ModelAndView boardCont(@RequestParam("no") int no, @RequestParam("page") int page,
                                  HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("manager/manager_board_cont");

        int man = 0;

        if(request.getParameter("man") == null) {
            man = 1;
        }

        Board cont = this.mapper.getBoard(no);

        cont.setImgKey(this.imageService.findImageByKey(cont.getImgKey()).get().getUrl());

        List<BoardReply> rList = this.mapper.getBoardReplyList(no);

        mav.addObject("cont", cont);
        mav.addObject("page", page);
        mav.addObject("rList", rList);
        mav.addObject("man", man);
        mav.addObject("side_manager", true);
        mav.addObject("side_manager_board", true);

        return mav;
    }

    @GetMapping("/board_mod")
    public void boardMod(@RequestParam("no") int no, @RequestParam("page") int page,
                                 HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        Board cont = this.mapper.getBoard(no);

        cont.setTitle("관리자에 의해 삭제된 게시글입니다.");
        cont.setCont("관리자에 의해 삭제된 게시글입니다.");
        cont.setImgKey("potatoes/16ffe646-2fac-4e58-8833-8998c0f10b56");

        int res = this.mapper.modBoard(cont);

        if(res > 0) {
            out.println("<script>");
            out.println("alert('게시글이 블러 처리되었습니다.')");
            out.println("location.href='board?page="+page+"'");
            out.println("</script>");
        }else {
            out.println("""
                            <script>
                            alert('게시글 블러 처리에 실패하였습니다.')
                            history.back()
                            </script>
                        """);
        }

    }

    @GetMapping("board_del")
    public void boardDel(@RequestParam("no") int no, @RequestParam("page") int page,
                                 HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int res = this.mapper.deleteBoard(no);

        if(res > 0) {
            this.mapper.updateBoardSequence(no);
            this.mapper.updateBoardReplySequence(no);
            out.println("<script>");
            out.println("alert('게시글이 삭제되었습니다.')");
            out.println("location.href='board?page="+page+"'");
            out.println("</script>");
        }else {
            out.println("""
                            <script>
                            alert('게시글 삭제에 실패하였습니다.')
                            history.back()
                            </script>
                        """);
        }
    }

    @GetMapping("rpl_del")
    public void deleteRpl(@RequestParam("no") int no, @RequestParam("bno") int bno,
                          @RequestParam("page") int page, HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int res = this.mapper.deleteBoardReply(no);

        if(res > 0) {
            out.println("<script>");
            out.println("alert('댓글이 삭제되었습니다.')");
            out.println("location.href='board_cont?no="+bno+"&page="+page+"'");
            out.println("</script>");
        }else {
            out.println("""
                            <script>
                            alert('댓글 삭제에 실패했습니다.')
                            history.back()
                            </script>
                        """);
        }
    }


    @GetMapping("/missing")
    public ModelAndView goMissing(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("manager/manager_missing");

        int page = 0;

        if(request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }else {
            page = 1;
        }

        List<missing> mList = this.mapper.getAllMissing();

        for(missing mdto : mList) {
            mdto.setImgKey(this.imageService.findImageByKey(mdto.getImgKey()).get().getUrl());
        }

        mav.addObject("mList", mList);
        mav.addObject("page", page);
        mav.addObject("side_manager", true);
        mav.addObject("side_manager_board", true);

        return mav;
    }

    @GetMapping("/pickup")
    public ModelAndView goPickUp(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("manager/manager_pickup");

        int page = 0;

        if(request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }else {
            page = 1;
        }

        List<pickup> pList = this.mapper.getAllPickUp();

        for(pickup pdto : pList) {
            pdto.setImgKey(this.imageService.findImageByKey(pdto.getImgKey()).get().getUrl());
        }

        mav.addObject("pList", pList);
        mav.addObject("page", page);
        mav.addObject("side_manager", true);
        mav.addObject("side_manager_board", true);

        return mav;
    }

    @GetMapping("/missing_cont")
    public ModelAndView missingCont(@RequestParam("no") int no, @RequestParam("page") int page,
                                    HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("manager/manager_missing_cont");

        int man = 0;

        if(request.getParameter("man") == null) {
            man = 1;
        }

        missing cont = this.mapper.getMissing(no);

        cont.setImgKey(this.imageService.findImageByKey(cont.getImgKey()).get().getUrl());

        mav.addObject("cont", cont);
        mav.addObject("page", page);
        mav.addObject("man", man);
        mav.addObject("side_manager", true);
        mav.addObject("side_manager_board", true);

        return mav;
    }

    @GetMapping("/missing_mod")
    public void missingMod(@RequestParam("no") int no, @RequestParam("page") int page,
                         HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        missing cont = this.mapper.getMissing(no);

        cont.setTitle("관리자에 의해 삭제된 게시글입니다.");
        cont.setCont("관리자에 의해 삭제된 게시글입니다.");
        cont.setImgKey("potatoes/16ffe646-2fac-4e58-8833-8998c0f10b56");

        int res = this.mapper.modMissing(cont);

        if(res > 0) {
            out.println("<script>");
            out.println("alert('게시글이 블러 처리되었습니다.')");
            out.println("location.href='missing?page="+page+"'");
            out.println("</script>");
        }else {
            out.println("""
                            <script>
                            alert('게시글 블러 처리에 실패하였습니다.')
                            history.back()
                            </script>
                        """);
        }

    }

    @GetMapping("/missing_del")
    public void missingDel(@RequestParam("no") int no, @RequestParam("page") int page,
                         HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int res = this.mapper.deleteMissing(no);

        if(res > 0) {
            this.mapper.updateMissingSequence(no);
            out.println("<script>");
            out.println("alert('게시글이 삭제되었습니다.')");
            out.println("location.href='missing?page="+page+"'");
            out.println("</script>");
        }else {
            out.println("""
                            <script>
                            alert('게시글 삭제에 실패하였습니다.')
                            history.back()
                            </script>
                        """);
        }
    }

    @GetMapping("/pickup_cont")
    public ModelAndView pickupCont(@RequestParam("no") int no, @RequestParam("page") int page,
                                   HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("manager/manager_pickup_cont");

        int man = 0;

        if(request.getParameter("man") == null) {
            man = 1;
        }

        pickup cont = this.mapper.getPickUp(no);

        cont.setImgKey(this.imageService.findImageByKey(cont.getImgKey()).get().getUrl());

        mav.addObject("cont", cont);
        mav.addObject("page", page);
        mav.addObject("man", man);
        mav.addObject("side_manager", true);
        mav.addObject("side_manager_board", true);

        return mav;
    }

    @GetMapping("/pickup_mod")
    public void pickupMod(@RequestParam("no") int no, @RequestParam("page") int page,
                           HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        pickup cont = this.mapper.getPickUp(no);

        cont.setTitle("관리자에 의해 삭제된 게시글입니다.");
        cont.setCont("관리자에 의해 삭제된 게시글입니다.");
        cont.setImgKey("potatoes/16ffe646-2fac-4e58-8833-8998c0f10b56");

        int res = this.mapper.modPickUp(cont);

        if(res > 0) {
            out.println("<script>");
            out.println("alert('게시글이 블러 처리되었습니다.')");
            out.println("location.href='pickup?page="+page+"'");
            out.println("</script>");
        }else {
            out.println("""
                            <script>
                            alert('게시글 블러 처리에 실패하였습니다.')
                            history.back()
                            </script>
                        """);
        }

    }

    @GetMapping("/pickup_del")
    public void pickupDel(@RequestParam("no") int no, @RequestParam("page") int page,
                           HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int res = this.mapper.deletePickUp(no);

        if(res > 0) {
            this.mapper.updatePickUpSequence(no);
            out.println("<script>");
            out.println("alert('게시글이 삭제되었습니다.')");
            out.println("location.href='pickup?page="+page+"'");
            out.println("</script>");
        }else {
            out.println("""
                            <script>
                            alert('게시글 삭제에 실패하였습니다.')
                            history.back()
                            </script>
                        """);
        }
    }

    @PostMapping("update_faq")
    public void updateFAQ(FAQ faq, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        int res = this.mapper.updateFAQ(faq);

        if(res > 0) {
            out.println("""
                            <script>
                            alert('수정이 완료되었습니다.')
                            location.href='main'
                            </script>
                        """);
        }else {
            out.println("""
                            <script>
                            alert('수정에 실패되었습니다.')
                            history.back()
                            </script>
                        """);
        }
    }

    @PostMapping("insert_faq")
    public void insert(@RequestParam("insertTitle") String title, @RequestParam("insertCont") String cont,
                               HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();

        FAQ dto = new FAQ();
        dto.setTitle(title);
        dto.setCont(cont);

        int res = this.mapper.insertFAQ(dto);

        if(res > 0) {
            out.println("""
                            <script>
                            alert('등록이 완료되었습니다.')
                            location.href='main'
                            </script>
                        """);
        }else {
            out.println("""
                            <script>
                            alert('등록에 실패하였습니다.')
                            history.back()
                            </script>
                        """);
        }
    }


}
