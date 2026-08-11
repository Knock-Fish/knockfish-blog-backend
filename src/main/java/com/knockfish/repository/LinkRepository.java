package com.knockfish.repository;

import com.knockfish.dto.link.LinkQueryDTO;
import com.knockfish.entity.Link;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LinkRepository {
    List<Link> selectAll(LinkQueryDTO query);

    List<Link> selectAllForFront();

    Long insert(Link link);

    void updateById(Link link);

    void deleteById(Long id);

    /**
     * Agent: 统计友链总数
     */
    Long selectLinkCount();
}
