package com.knockfish.utils;

import com.github.pagehelper.PageInfo;
import com.knockfish.vo.PageResultVO;

public class PageConvertUtil {

    public static <T> PageResultVO<T> convert(PageInfo<T> pageInfo) {
        PageResultVO<T> result = new PageResultVO<>();
        result.setList(pageInfo.getList());
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setSize(pageInfo.getSize());
        result.setStartRow(pageInfo.getStartRow());
        result.setEndRow(pageInfo.getEndRow());
        result.setPages(pageInfo.getPages());
        result.setPrePage(pageInfo.getPrePage());
        result.setNextPage(pageInfo.getNextPage());
        result.setFirstPage(pageInfo.isIsFirstPage());
        result.setLastPage(pageInfo.isIsLastPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setNavigatePages(pageInfo.getNavigatePages());
        result.setNavigatepageNums(pageInfo.getNavigatepageNums());
        result.setNavigateFirstPage(pageInfo.getNavigateFirstPage());
        result.setNavigateLastPage(pageInfo.getNavigateLastPage());
        return result;
    }
}