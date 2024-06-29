package com.goott.potatoes.police.controller;

import com.goott.potatoes.police.model.PoliceMapper;
import com.goott.potatoes.police.model.PoliceService;
import com.goott.potatoes.police.model.Section;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/police")
public class PoliceController {

    @Autowired
    private PoliceService service;

    @Autowired
    private PoliceMapper mapper;

    private final int rowsize = 10;
    private int totalRecord = 0;

    @GetMapping("/main")
    public ModelAndView mainPage() {

        ModelAndView mav = new ModelAndView("police/police_main");
        return mav;
    }

    @PostMapping("section")
    public void inputSec(@RequestParam("file")MultipartFile file, Model model) throws IOException {
        List<Section> secList = new ArrayList<>();

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if(!extension.equals("xlsx") && !extension.equals("xls")) {
            throw new IOException("엑셀 파일만 업로드 해주세요.");
        }

        Workbook workbook = null;

        if(extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        }else if(extension.equals("xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        }

        Sheet worksheet = workbook.getSheetAt(0);

        for(int i=1; i<worksheet.getPhysicalNumberOfRows(); i++) {
            Row row = worksheet.getRow(i);
            Section dto = new Section();

            dto.setSecNum((int)row.getCell(0).getNumericCellValue());
            dto.setMain(row.getCell(1).getStringCellValue());
            dto.setCenter(row.getCell(2).getStringCellValue());
            dto.setName(row.getCell(3).getStringCellValue());
            dto.setSep(row.getCell(4).getStringCellValue());
            dto.setTel(row.getCell(5).getStringCellValue());
            dto.setAddr(row.getCell(6).getStringCellValue());

            secList.add(dto);

        }

        this.mapper.add_sec(secList);
    }

    @GetMapping("search_main")
    public ModelAndView search_main(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("police/search_main");

        return mav;
    }

}
