package com.goott.potatoes.esh.boardController;

import com.goott.potatoes.common.model.Alarm;
import com.goott.potatoes.common.model.Image;
import com.goott.potatoes.common.service.ImageService;
import com.goott.potatoes.esh.boardModel.Board;
import com.goott.potatoes.esh.boardModel.BoardReply;
import com.goott.potatoes.esh.service.BoardService;
import com.goott.potatoes.esh.service.ReplyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
public class BoardControllerDeco {

    /*int page;*/

    /*private final int rowsize =3;

    private int totalRecord = 0;*/

    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardService service;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ReplyService replyService;

    @GetMapping("/deco_board_list")
    public String list(Model model, HttpServletRequest request) {
        Alarm alarm = new Alarm(model);
        int page = 0;

        if(request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }else {
            page = 1;
        }

        List<Board> list = this.service.decoGetBoardList();
        for(Board board : list) {
            board.setImgKey(
             imageService.findImageByKey(board.getImgKey()).get().getUrl());
            int commentCount = replyService.countReply(board.getBoardNum());
            board.setCommentCount(commentCount);
        }

        model.addAttribute("List", list).addAttribute("side_chk",true).addAttribute("page", page);

        return "boardTemplate/deco_board_list";
    }

    @GetMapping("/deco_board_insert")
    public ModelAndView insert(HttpSession session, Model model) {
        Alarm alarm = new Alarm(model);
        String id =(String)session.getAttribute("userId");
        if(id == null) {
           alarm.setMessageAndRedirect("로그인 후에 이용 가능합니다", "deco_board_list");
           return new ModelAndView(alarm.getMessagePage());
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("boardTemplate/deco_board_insert");
        return modelAndView;
    }

    @PostMapping("/deco_board_insert_ok")
    public String insertOk(
            @RequestPart("file") MultipartFile file,
            @RequestParam("userNickname") String userNickname,
            @RequestParam("title") String title,
            @RequestParam("cont") String cont,
            HttpServletRequest request,
            Model model) {

        Alarm alarm = new Alarm(model);
        Board board = new Board();

        HttpSession session = request.getSession();
        String userId =(String) session.getAttribute("userNickname");
        if(userId == null || userId.isEmpty()) {
            alarm.setMessageAndRedirect("로그인이 필요한 서비스입니다.", "deco_board_insert");
            return alarm.getMessagePage();
        }

        board.setUserId(userId);
        board.setUserNickname(userNickname);
        board.setTitle(title);
        board.setCont(cont);

        Optional<Image> imageOptional = imageService.insertFileAndGetImage(file);
        String baseKey = "potatoes/16ffe646-2fac-4e58-8833-8998c0f10b56";
        if(imageOptional.isEmpty()){
            board.setImgKey(baseKey);
        }else{
            Image image = imageOptional.get();
            board.setImgKey(image.getImgKey());
        }
        // 보드 저장
        int result = boardService.insertBoard(board);

        if (result > 0) {
            alarm.setMessageAndRedirect("게시글이 등록 되었습니다.", "deco_board_list");
        }
        else {
            alarm.setMessageAndRedirect("게시글 등록에 실패했습니다.", "deco_board_insert");
        }
        return alarm.getMessagePage();

    }

    @GetMapping("/deco_board_content")
    public String content(
            Model model,
            @RequestParam("no") int no,
            HttpServletRequest request, @RequestParam("page") int page)  {
        String userIdSession = String.valueOf(request.getSession().getAttribute("userNickname"));
        System.out.println("===========================");
        System.out.println(userIdSession);

        this.service.viewCount(no);
        Board board = this.service.boardContent(no);
        if(board != null) {
            board.setImgKey(
                    imageService.findImageByKey(board.getImgKey()).get().getUrl()
            );
        }
        List<BoardReply> replyList = this.replyService.getBoardReplies(no);

        model.addAttribute("Cont", board);
        model.addAttribute("userIdSession", userIdSession);
        model.addAttribute("replyList", replyList);
        model.addAttribute("page", page);
        model.addAttribute("side_chk",true);
        return "boardTemplate/deco_board_content";
    }

    @GetMapping("/deco_board_update")
    public String update(Model model, @RequestParam("no") int no, HttpServletRequest request) {
        Board board = this.service.boardContent(no);
        if(request.getSession().getAttribute("userNickname") == null
                        || !request.getSession().getAttribute("userNickname").equals(board.getUserNickname())) {
            return "redirect:/deco_board_list";
        }

        String baseImgKey = "potatoes/16ffe646-2fac-4e58-8833-8998c0f10b56";
        Optional<Image> imageOp = imageService.findImageByKey(baseImgKey);

        model.addAttribute("Upt", board);
        // Checking if imageService.findImageByKey(board.getImgKey()) returns a value
        Optional<Image> boardImageOp = imageService.findImageByKey(board.getImgKey());
        if (boardImageOp.isPresent()) {
            model.addAttribute("imgUrl", boardImageOp.get().getUrl());
        } else {
            model.addAttribute("imgUrl", "defaultImageUrl"); // Set a default image URL if no image is found
        }

        if (imageOp.isPresent()) {
            model.addAttribute("baseUrl", imageOp.get().getUrl());
        } else {
            model.addAttribute("baseUrl", "defaultBaseUrl"); // Set a default base URL if no image is found
        }
        return "boardTemplate/deco_board_update";
    }

    @PostMapping("/deco_board_update_ok")
    public String updateOk(@RequestParam("boardNum") int no,
                           @RequestParam("imageKey") String imgkey,
                           @RequestParam("userNickname") String userNickname,
                           @RequestParam("userId") String id,
                           @RequestParam("title") String title,
                           @RequestParam("cont") String cont,
                           @RequestParam("removeImage") Boolean removeImage,
                           @RequestPart("file") MultipartFile file, Model model) {

        Alarm alarm = new Alarm(model);
        Board board = new Board();
        board.setBoardNum(no);
        board.setUserNickname(userNickname);
        board.setTitle(title);
        board.setCont(cont);
        board.setUserId(id);

        //imageService.updateImageByKeyAndFile(imgkey, file);
        String baseKey = "potatoes/16ffe646-2fac-4e58-8833-8998c0f10b56";
        Optional<Image> imageOptional = Optional.empty(); //Optional<Image> imageOptional = Optional.empty();
        if(removeImage){
            imageOptional = imageService.findImageByKey(baseKey);
        }else{
            // 수정된 부분 시작
            if (file != null && !file.isEmpty()) {
                if (imgkey.equals(baseKey)) {
                    imageOptional = imageService.insertFileAndGetImage(file);
                } else {
                    imageOptional = imageService.updateImageByKeyAndFile(imgkey, file);
                }
            } else if (!imgkey.isEmpty()) {
                imageOptional = imageService.findImageByKey(imgkey);
            }
        }


        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            board.setImgKey(image.getImgKey());
        } else {
            board.setImgKey(null); // 또는 기본 이미지 키 설정
        }
        // 수정된 부분 끝

        // 보드 저장
        int result = boardService.updateBoard(board);

        if (result > 0) {
            alarm.setMessageAndRedirect("게시글이 수정되었습니다.", "deco_board_list");
        }
        else {
            alarm.setMessageAndRedirect("게시글 수정에 실패했습니다.", "deco_board_list");
        }
        return alarm.getMessagePage();
    }

    @GetMapping("deco_board_delete_ok")
    public String delete(@RequestParam("no") int no, Model model) {
        int result = boardService.deleteBoard(no);

        Alarm alarm = new Alarm(model);

        if (result > 0) {
            boardService.updateSeq(no);
            alarm.setMessageAndRedirect("성공", "deco_board_list");
        }
        else {
            alarm.setMessageAndRedirect("실패", "deco_board_insert");
        }
        return alarm.getMessagePage();
    }

    /*@GetMapping("deco_board_search")
    public String search(@RequestParam("field") String field,
                         @RequestParam("keyword") String keyword,
                         HttpServletRequest request,
                         Model model) {
        //System.out.println(field + keyword);
        int page;
        if(request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        } else {
            page = 1;
        }
        Map<String, String> map = new HashMap<String, String>();

        map.put("Field", field);
        map.put("Keyword", keyword);
        totalRecord = this.boardMapper.searchBoardCount(map);
        System.out.println("tr"+totalRecord);
        Page pdto = new Page(page, rowsize, totalRecord, field, keyword);
        //Page pdto = new Page(page, rowsize, totalRecord);

        List<Board> searchList = this.boardMapper.searchBoardList(pdto);

        for(Board board : searchList) {
            System.out.println("bn"+board.getBoardNum());
            board.setImgKey(
                    imageService.findImageByKey(board.getImgKey()).get().getUrl()
            );
        }
        System.out.println("eb"+pdto.getEndBlock());
        System.out.println("sb"+pdto.getStartBlock());
        model.addAttribute("searchPageList", searchList).addAttribute("paging", pdto);

        return "boardTemplate/deco_board_search_list";
    }*/

    @PostMapping("/deco_insert_reply")
    @ResponseBody
    public String insertReply(@RequestParam("boardNum") int boardNum, @RequestParam("id") String id,
                              @RequestParam("depth") int depth, @RequestParam("step") int step,
                              @RequestParam("cont") String cont, HttpSession session) {

        String userIdSession = (String)session.getAttribute("userId");

        /*System.out.println(userIdSession);*/
        if(userIdSession == null || userIdSession.isEmpty()) {
            return "login_required";
        }

        BoardReply reply = new BoardReply();
        reply.setBoardNum(boardNum);
        reply.setId(id);
        reply.setDepth(depth);
        reply.setStep(step);
        reply.setCont(cont);

        int result = replyService.insertReply(reply);
        if (result > 0) {
            return "success";
        } else {
            return "fail";
        }
    }

    @PostMapping("/deco_delete_reply")
    @ResponseBody
    public String deleteReply(@RequestParam("replyNum") int replyNum) {
        int result = replyService.deleteReply(replyNum);

        if (result > 0) {
            return "success";
        } else {
            return "fail";
        }

    }


}
