package com.knockfish.service.impl;

import com.knockfish.repository.ArticleRepository;
import com.knockfish.repository.CodeSnippetRepository;
import com.knockfish.repository.NoteRepository;
import com.knockfish.repository.SiteRepository;
import com.knockfish.repository.TagRepository;
import com.knockfish.security.CustomUserDetails;
import com.knockfish.service.DashboardService;
import com.knockfish.vo.dashboard.ActivityVO;
import com.knockfish.vo.dashboard.ArticleLatestVO;
import com.knockfish.vo.dashboard.ArticleTrendVO;
import com.knockfish.vo.dashboard.DashboardOverviewVO;
import com.knockfish.vo.tag.TagWithArticleCountVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final SiteRepository siteRepository;
    private final NoteRepository noteRepository;
    private final CodeSnippetRepository codeSnippetRepository;

    @Override
    public DashboardOverviewVO getOverview() {
        DashboardOverviewVO overview = new DashboardOverviewVO();

        Long userId = getCurrentUserId();

        overview.setTotalArticles(articleRepository.selectArticleCount());
        overview.setTotalTags(tagRepository.selectTagCount());
        overview.setTotalSites(siteRepository.selectSiteCount());
        overview.setTotalNotes(noteRepository.selectNoteCount());
        overview.setTotalCodeSnippets(codeSnippetRepository.selectCodeSnippetCount());
        overview.setDraftCount(articleRepository.selectCount(userId));
        overview.setTotalVisits(0L);

        overview.setArticleChange("+10%");
        overview.setTagChange("-12%");
        overview.setSiteChange("+30%");
        overview.setVisitChange("+20%");

        return overview;
    }

    @Override
    public ArticleTrendVO getArticleTrend(String period, Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        if (period == null) {
            period = "monthly";
        }

        ArticleTrendVO trend = new ArticleTrendVO();
        trend.setPeriod(period);
        trend.setYear(year);

        List<Map<String, Object>> result;
        if ("monthly".equals(period)) {
            result = articleRepository.selectMonthlyArticleCount(year);
            trend.setLabels(buildMonthLabels(year));
        } else if ("weekly".equals(period)) {
            result = articleRepository.selectWeeklyArticleCount(year);
            trend.setLabels(buildWeekLabels(year));
        } else {
            result = articleRepository.selectYearlyArticleCount();
            trend.setLabels(buildYearLabels());
        }

        List<Integer> values = new ArrayList<>();
        for (Map<String, Object> map : result) {
            values.add(((Number) map.get("count")).intValue());
        }
        trend.setValues(values);

        return trend;
    }

    @Override
    public List<ArticleLatestVO> getLatestArticles(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return articleRepository.selectLatestArticles(limit);
    }

    @Override
    public List<TagWithArticleCountVO> getTagCloud() {
        return tagRepository.selectTagWithArticleCount();
    }

    @Override
    public List<TagWithArticleCountVO> getArticleCategoryStats() {
        return tagRepository.selectTagWithArticleCount();
    }

    @Override
    public List<ActivityVO> getActivities(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        List<ActivityVO> activities = new ArrayList<>();

        List<ArticleLatestVO> latestArticles = articleRepository.selectLatestArticles(limit);
        for (ArticleLatestVO article : latestArticles) {
            ActivityVO activity = new ActivityVO();
            activity.setId(article.getArticleId());
            activity.setType("article_publish");
            activity.setTitle("发布了文章");
            activity.setContent(article.getTitle());
            activity.setTime(article.getPublishTime());
            activity.setLink("/article/" + article.getArticleId());
            activities.add(activity);
        }

        return activities;
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        return 0L;
    }

    private List<String> buildMonthLabels(int year) {
        List<String> labels = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            labels.add(year + "-" + String.format("%02d", i));
        }
        return labels;
    }

    private List<String> buildWeekLabels(int year) {
        List<String> labels = new ArrayList<>();
        LocalDate date = LocalDate.of(year, 1, 1);
        int week = 1;
        while (date.getYear() == year) {
            labels.add("第" + week + "周");
            date = date.plusDays(7);
            week++;
        }
        return labels;
    }

    private List<String> buildYearLabels() {
        List<String> labels = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 4; i <= currentYear; i++) {
            labels.add(String.valueOf(i));
        }
        return labels;
    }
}