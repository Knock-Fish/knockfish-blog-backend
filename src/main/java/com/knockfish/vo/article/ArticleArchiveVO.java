    package com.knockfish.vo.article;

    import com.fasterxml.jackson.annotation.JsonFormat;
    import io.swagger.v3.oas.annotations.media.Schema;
    import lombok.Getter;
    import lombok.Setter;

    import java.time.LocalDateTime;

    @Getter
    @Setter
    @Schema(description = "文章归档VO")
    public class ArticleArchiveVO {
        @Schema(description = "文章ID", example = "1")
        private Long articleId;
        @Schema(description = "文章标题", example = "Vue3 Composition API 深入浅出")
        private String title;
        @Schema(description = "文章描述", example = "详解Vue3组合式API的使用方法")
        private String description;
        @Schema(description = "发布时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime publishTime;
        @Schema(description = "标签名称列表", example = "前端,JavaScript")
        private String tagNames;
        @Schema(description = "浏览量", example = "1256")
        private Integer views;
    }