package com.knockfish.controller.open;

import com.knockfish.common.Result;
import com.knockfish.service.LinkService;
import com.knockfish.vo.link.LinkVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/front/link")
@Tag(name = "前台-友链", description = "博客前台友链接口（匿名可访问）")
public class LinkFrontController {
    private final LinkService linkService;

    @GetMapping("/list")
    @Operation(summary = "获取友链列表", description = "获取所有友链列表")
    public Result<List<LinkVO>> getLinkList() {
        return Result.success(linkService.getLinkList());
    }
}
