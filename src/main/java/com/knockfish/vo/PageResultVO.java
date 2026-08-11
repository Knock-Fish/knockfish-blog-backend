package com.knockfish.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分页结果VO")
public class PageResultVO<T> {

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "总记录数", example = "100")
    private long total;

    @Schema(description = "当前页码", example = "1")
    private int pageNum;

    @Schema(description = "每页数量", example = "10")
    private int pageSize;

    @Schema(description = "当前页记录数", example = "10")
    private int size;

    @Schema(description = "起始行", example = "0")
    private long startRow;

    @Schema(description = "结束行", example = "9")
    private long endRow;

    @Schema(description = "总页数", example = "10")
    private int pages;

    @Schema(description = "上一页", example = "0")
    private int prePage;

    @Schema(description = "下一页", example = "2")
    private int nextPage;

    @Schema(description = "是否第一页", example = "true")
    private boolean isFirstPage;

    @Schema(description = "是否最后一页", example = "false")
    private boolean isLastPage;

    @Schema(description = "是否有上一页", example = "false")
    private boolean hasPreviousPage;

    @Schema(description = "是否有下一页", example = "true")
    private boolean hasNextPage;

    @Schema(description = "导航页码数", example = "8")
    private int navigatePages;

    @Schema(description = "导航页码列表")
    private int[] navigatepageNums;

    @Schema(description = "导航第一页", example = "1")
    private int navigateFirstPage;

    @Schema(description = "导航最后一页", example = "10")
    private int navigateLastPage;
}