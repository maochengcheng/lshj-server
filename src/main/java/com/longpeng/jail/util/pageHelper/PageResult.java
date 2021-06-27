package com.longpeng.jail.util.pageHelper;

import com.github.pagehelper.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "PageResult",description = "分页后的参数")
public class PageResult<T> {
    @ApiModelProperty(value = "数据列表")
    private List<T> contents;
    @ApiModelProperty(value = "分页参数")
    private PageParameter page;

    public static <T> PageResult<T> of(List<T> content){
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setContents(content);
        pageResult.setPage(PageParameter.setPageParameter((Page)content));
        return pageResult;
    }
    //jpa分页调用方法
    public static <T> PageResult<T> of(List<T> content,int pageNum, int pageSize, int total, int pages){
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setContents(content);
        pageResult.setPage(new PageParameter(pageNum,pageSize,total,pages));
        return pageResult;
    }
}
