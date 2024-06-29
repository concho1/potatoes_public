package com.goott.potatoes.esh.boardModel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Page {
    private int page;
    private int rowsize;
    private int totalRecord;
    private int startNo;
    private int endNo;
    private int startBlock;
    private int endBlock;
    private int allPage;
    private int block=3;

    private String field;
    private String keyword;


    public Page(int page, int rowsize, int totalRecord) {
        this.page = page;
        this.rowsize = rowsize;
        this.totalRecord = totalRecord;

        this.startNo = (this.page * this.rowsize) - (this.rowsize -1);
        this.endNo = (this.page * this.rowsize);
        this.startBlock = (((this.page -1)/this.block) * this.block)+1;
        this.endBlock = (((this.page -1)/this.block) * this.block)+this.block;
        this.allPage = (int)Math.ceil(this.totalRecord/(double)this.rowsize);

        if(this.endBlock > this.allPage) {
            this.endBlock = this.allPage;
        }

    }
        public Page(int page, int rowsize, int totalRecord, String field, String keyword) {
         this(page, rowsize, totalRecord);
         this.field = field;
         this.keyword = keyword;
        }

}
