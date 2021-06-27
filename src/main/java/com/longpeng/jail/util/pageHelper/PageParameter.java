package com.longpeng.jail.util.pageHelper;

import com.github.pagehelper.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "page",description = "分页参数")
public class PageParameter {
    @ApiModelProperty(value = "当前页")
    private int pageNum;
    @ApiModelProperty(value = "每页大小")
    private int pageSize;
    @ApiModelProperty(value = "总条数")
    private int total;
    @ApiModelProperty(value = "总页数")
    private int pages;

    public static PageParameter setPageParameter(Page page){
        PageParameter pageParameter=new PageParameter();
        pageParameter.setPageNum(page.getPageNum());
        pageParameter.setPageSize(page.getPageSize());
        pageParameter.setTotal((int)page.getTotal());
        pageParameter.setPages(page.getPages());
        return pageParameter;
    }

    public PageParameter(int pageNum, int pageSize, int total, int pages) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = pages;
    }

    public PageParameter() {
    }
}
