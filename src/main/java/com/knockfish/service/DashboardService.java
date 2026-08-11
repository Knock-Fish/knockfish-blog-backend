package com.knockfish.service;

import com.knockfish.vo.dashboard.ActivityVO;
import com.knockfish.vo.dashboard.ArticleLatestVO;
import com.knockfish.vo.dashboard.ArticleTrendVO;
import com.knockfish.vo.dashboard.DashboardOverviewVO;
import com.knockfish.vo.tag.TagWithArticleCountVO;

import java.util.List;

public interface DashboardService {

    DashboardOverviewVO getOverview();

    ArticleTrendVO getArticleTrend(String period, Integer year);

    List<ArticleLatestVO> getLatestArticles(Integer limit);

    List<TagWithArticleCountVO> getTagCloud();

    List<TagWithArticleCountVO> getArticleCategoryStats();

    List<ActivityVO> getActivities(Integer limit);
}