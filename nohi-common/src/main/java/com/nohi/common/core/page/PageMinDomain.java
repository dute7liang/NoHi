package com.nohi.common.core.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页数据
 *
 * 只是用来生成swagger的。没撒实际用处，只是为了偷懒
 *
 * @author nohi
 */
@Data
@ApiModel("分页数据")
public class PageMinDomain {
    /**
     * 当前记录起始索引
     */
    @ApiModelProperty("当前记录起始索引")
    private Integer pageNum;

    /**
     * 每页显示记录数
     */
    @ApiModelProperty("每页显示记录数")
    private Integer pageSize;

}
