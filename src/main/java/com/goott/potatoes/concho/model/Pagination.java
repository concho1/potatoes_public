package com.goott.potatoes.concho.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {
    private Integer nowPageNum;
    private Integer offset;
    private Integer limit;
    private Integer endPageNum;
    private ArrayList<Integer> pageBlocks;
}
