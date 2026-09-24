package com.ruoyi.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.CrowlookCategory;
import com.ruoyi.system.domain.CrowlookComment;
import com.ruoyi.system.domain.CrowlookPage;
import com.ruoyi.system.domain.CrowlookPost;
import com.ruoyi.system.mapper.CrowlookContentMapper;
import com.ruoyi.system.service.ICrowlookContentService;

/** Crowlook 内容中心服务实现。 */
@Service
public class CrowlookContentServiceImpl implements ICrowlookContentService
{
    @Autowired
    private CrowlookContentMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<CrowlookCategory> selectCategoryList(CrowlookCategory category)
    {
        return mapper.selectCategoryList(category);
    }

    @Override
    public CrowlookCategory selectCategoryById(Long categoryId)
    {
        return mapper.selectCategoryById(categoryId);
    }

    @Override
    public int insertCategory(CrowlookCategory category)
    {
        applyCategoryDefaults(category);
        validateCategoryParent(category);
        return mapper.insertCategory(category);
    }

    @Override
    public int updateCategory(CrowlookCategory category)
    {
        applyCategoryDefaults(category);
        validateCategoryParent(category);
        return mapper.updateCategory(category);
    }

    @Override
    @Transactional
    public int deleteCategoryByIds(Long[] categoryIds)
    {
        for (Long categoryId : categoryIds)
        {
            if (mapper.countCategoryChildren(categoryId) > 0)
            {
                throw new ServiceException("请先删除当前分类的子分类");
            }
            if (mapper.countCategoryPosts(categoryId) > 0)
            {
                throw new ServiceException("分类下仍有内容，不能删除");
            }
        }
        return mapper.deleteCategoryByIds(categoryIds);
    }

    @Override
    public List<CrowlookPost> selectPostList(CrowlookPost post)
    {
        return mapper.selectPostList(post);
    }

    @Override
    public CrowlookPost selectPostById(Long postId)
    {
        return mapper.selectPostById(postId);
    }

    @Override
    public int insertPost(CrowlookPost post)
    {
        applyPostDefaults(post);
        post.setFormatContent(normalizeJson(post.getFormatContent(), "{}", "视频配置"));
        return mapper.insertPost(post);
    }

    @Override
    public int updatePost(CrowlookPost post)
    {
        applyPostDefaults(post);
        post.setFormatContent(normalizeJson(post.getFormatContent(), "{}", "视频配置"));
        return mapper.updatePost(post);
    }

    @Override
    @Transactional
    public int deletePostByIds(Long[] postIds)
    {
        mapper.deleteCommentsByPostIds(postIds);
        mapper.deleteFavoritesByPostIds(postIds);
        return mapper.deletePostByIds(postIds);
    }

    @Override
    public List<CrowlookPage> selectPageList(CrowlookPage page)
    {
        return mapper.selectPageList(page);
    }

    @Override
    public CrowlookPage selectPageById(Long pageId)
    {
        return mapper.selectPageById(pageId);
    }

    @Override
    public int insertPage(CrowlookPage page)
    {
        normalizePage(page);
        return mapper.insertPage(page);
    }

    @Override
    public int updatePage(CrowlookPage page)
    {
        normalizePage(page);
        return mapper.updatePage(page);
    }

    @Override
    public int deletePageByIds(Long[] pageIds)
    {
        return mapper.deletePageByIds(pageIds);
    }

    @Override
    public List<CrowlookComment> selectCommentList(CrowlookComment comment)
    {
        return mapper.selectCommentList(comment);
    }

    @Override
    public CrowlookComment selectCommentById(Long commentId)
    {
        return mapper.selectCommentById(commentId);
    }

    @Override
    public int insertComment(CrowlookComment comment)
    {
        if (blank(comment.getStatus())) comment.setStatus("0");
        if (blank(comment.getNickname())) comment.setNickname("微信用户");
        if (comment.getPostId() != null && comment.getParentId() != null && comment.getParentId() > 0)
        {
            CrowlookComment parent = mapper.selectCommentById(comment.getParentId());
            if (parent == null || !comment.getPostId().equals(parent.getPostId())) comment.setParentId(0L);
        }
        return mapper.insertComment(comment);
    }

    @Override
    public int updateComment(CrowlookComment comment)
    {
        if (blank(comment.getStatus())) comment.setStatus("0");
        return mapper.updateComment(comment);
    }

    @Override
    public int deleteCommentByIds(Long[] commentIds)
    {
        return mapper.deleteCommentByIds(commentIds);
    }

    @Override
    public Map<String, Object> selectOverview()
    {
        Map<String, Object> overview = mapper.selectOverview();
        return overview == null ? new LinkedHashMap<>() : overview;
    }

    @Override
    public Map<String, Object> getSiteSettings()
    {
        CrowlookPage page = mapper.selectPageByKey("discovery");
        if (page == null) throw new ServiceException("作品页尚未初始化，无法读取用户端设置");
        Map<String, Object> config = asMap(parseJson(page.getConfigJson(), new LinkedHashMap<>()));
        Map<String, Object> settings = asMap(config.get("ilank"));
        return new LinkedHashMap<>(settings);
    }

    @Override
    @Transactional
    public int updateSiteSettings(Map<String, Object> settings, String username)
    {
        CrowlookPage page = mapper.selectPageByKey("discovery");
        if (page == null) throw new ServiceException("作品页尚未初始化，无法保存用户端设置");
        Map<String, Object> config = asMap(parseJson(page.getConfigJson(), new LinkedHashMap<>()));
        Map<String, Object> current = new LinkedHashMap<>(asMap(config.get("ilank")));
        String[] allowed = { "site_name", "site_slogan", "site_wx", "site_form", "site_add",
                "site_comment", "site_kf", "site_wxkf", "site_tab", "copyright_text", "share_base",
                "profile_name", "home_image", "home_caption", "home_statement_cn", "home_statement_en" };
        for (String key : allowed)
        {
            if (settings.containsKey(key)) current.put(key, settings.get(key));
        }
        config.put("ilank", current);
        return mapper.updatePageConfig(page.getPageId(), json(config, "{}"), username);
    }

    @Override
    @Transactional
    public Map<String, Integer> importSnapshot(Map<String, Object> snapshot, String username)
    {
        if (snapshot == null || snapshot.isEmpty())
        {
            throw new ServiceException("快照内容为空");
        }
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("categories", 0);
        result.put("pages", 0);
        result.put("posts", 0);
        result.put("comments", 0);

        Object categoryRoot = snapshot.get("categories");
        if (categoryRoot instanceof Map)
        {
            categoryRoot = asMap(categoryRoot).get("categories");
        }
        importCategoryNodes(asList(categoryRoot), 0L, username, result);

        Object pages = snapshot.get("pages");
        if (pages instanceof List)
        {
            int order = 0;
            for (Object entry : asList(pages))
            {
                Map<String, Object> wrapper = asMap(entry);
                String key = text(wrapper.get("key"));
                Object data = wrapper.containsKey("data") ? wrapper.get("data") : wrapper;
                importPage(key, asMap(data), username, order++, result);
            }
        }
        else
        {
            String[] pageKeys = { "home", "discovery", "about", "module175", "module177" };
            for (int i = 0; i < pageKeys.length; i++)
            {
                Object value = snapshot.get(pageKeys[i]);
                if (value instanceof Map)
                {
                    importPage(pageKeys[i], asMap(value), username, i, result);
                }
            }
        }

        Object posts = snapshot.get("posts");
        if (posts instanceof List)
        {
            for (Object value : asList(posts)) importPost(asMap(value), username, result);
        }
        else
        {
            Map<Long, Map<String, Object>> postSummaries = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : snapshot.entrySet())
            {
                if (!(entry.getValue() instanceof Map)) continue;
                for (Object item : asList(asMap(entry.getValue()).get("posts")))
                {
                    Map<String, Object> summary = asMap(item);
                    Long postId = number(summary.get("id"));
                    if (postId != null) postSummaries.put(postId, summary);
                }
            }
            for (Map<String, Object> summary : postSummaries.values()) importPost(summary, username, result);

            // 详情数据最后导入，以正文、评论和完整元数据覆盖列表摘要。
            for (Map.Entry<String, Object> entry : snapshot.entrySet())
            {
                if (entry.getKey().startsWith("post") && entry.getValue() instanceof Map)
                {
                    Object post = asMap(entry.getValue()).get("post");
                    if (post instanceof Map)
                    {
                        Long postId = number(asMap(post).get("id"));
                        importPost(asMap(post), username, result);
                        if (postId != null && postSummaries.containsKey(postId))
                        {
                            result.put("posts", result.get("posts") - 1);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getPublicCategories()
    {
        List<CrowlookCategory> categories = mapper.selectPublishedCategories();
        Map<Long, Map<String, Object>> items = new LinkedHashMap<>();
        List<Map<String, Object>> roots = new ArrayList<>();
        for (CrowlookCategory category : categories)
        {
            Map<String, Object> item = categoryMap(category);
            item.put("children", new ArrayList<Map<String, Object>>());
            items.put(category.getCategoryId(), item);
        }
        for (CrowlookCategory category : categories)
        {
            Map<String, Object> item = items.get(category.getCategoryId());
            if (category.getParentId() != null && category.getParentId() > 0 && items.containsKey(category.getParentId()))
            {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) items.get(category.getParentId()).get("children");
                children.add(item);
            }
            else
            {
                roots.add(item);
            }
        }
        Map<String, Object> response = ok();
        response.put("page_title", "内容分类");
        response.put("categories", roots);
        return response;
    }

    @Override
    public Map<String, Object> getPublicPage(String pageKey, Long pageId)
    {
        CrowlookPage page = mapper.selectPublishedPage(pageKey, pageId);
        if (page == null) return notFound("页面尚未发布");

        Map<String, Object> modulePage = new LinkedHashMap<>();
        modulePage.put("id", page.getPageId());
        modulePage.put("taxonomy", "module_page");
        modulePage.put("name", blank(page.getPageName()) ? page.getPageTitle() : page.getPageName());
        modulePage.put("slug", page.getSlug());
        modulePage.put("bg_color", page.getBackgroundColor());
        modulePage.put("bg_img", page.getBackgroundImage());
        modulePage.put("style", blank(page.getBackgroundColor()) ? "" : "background-color:" + page.getBackgroundColor() + ";");

        Map<String, Object> response = ok();
        response.put("page_title", page.getPageTitle());
        response.put("module_page", modulePage);
        List<Object> modules = asList(parseJson(page.getModulesJson(), new ArrayList<>()));
        hydratePostModules(modules);
        response.put("modules", modules);
        Object config = parseJson(page.getConfigJson(), new LinkedHashMap<>());
        if (config instanceof Map && !asMap(config).isEmpty()) response.put("config", config);
        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> setPublicFavorite(Long postId, String deviceKey, boolean active)
    {
        CrowlookPost post = mapper.selectPostById(postId);
        if (post == null || !"0".equals(post.getStatus())) return notFound("作品不存在或尚未发布");
        int changed = active ? mapper.insertFavorite(postId, deviceKey) : mapper.deleteFavorite(postId, deviceKey);
        if (changed > 0) mapper.changePostFavoriteCount(postId, active ? 1 : -1);
        CrowlookPost updated = mapper.selectPostById(postId);
        Map<String, Object> response = ok();
        response.put("active", active);
        response.put("favorite_count", updated == null || updated.getFavoriteCount() == null ? 0 : updated.getFavoriteCount());
        return response;
    }

    @Override
    public Map<String, Object> getPublicPosts(Long categoryId, String keyword, Integer page, Integer pageSize)
    {
        int currentPage = page == null || page < 1 ? 1 : page;
        int size = pageSize == null || pageSize < 1 ? 12 : Math.min(pageSize, 50);
        int offset = (currentPage - 1) * size;
        long total = mapper.countPublicPosts(categoryId, keyword);
        List<CrowlookPost> posts = mapper.selectPublicPosts(categoryId, keyword, offset, size);
        int totalPages = total == 0 ? 0 : (int) Math.ceil(total / (double) size);

        Map<String, Object> response = ok();
        response.put("page_title", keyword == null ? "作品列表" : "搜索结果");
        response.put("total", total);
        response.put("total_pages", totalPages);
        response.put("current_page", currentPage);
        response.put("next_cursor", 0);
        response.put("posts", posts.stream().map(this::postSummary).collect(Collectors.toList()));
        if (categoryId != null)
        {
            CrowlookCategory category = mapper.selectCategoryById(categoryId);
            if (category != null) response.put("current_category", categoryMap(category));
        }
        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> getPublicPost(Long postId)
    {
        mapper.incrementPostViews(postId);
        CrowlookPost post = mapper.selectPostById(postId);
        if (post == null || !"0".equals(post.getStatus())) return notFound("内容不存在或尚未发布");

        Map<String, Object> detail = postSummary(post);
        detail.put("content", post.getContent());
        detail.put("views", post.getViews());
        detail.put("time", formatDate(post.getPublishTime(), "yyyy-MM-dd"));
        detail.put("date", formatDate(post.getPublishTime(), "yyyy-MM-dd"));
        detail.put("timestamp", post.getPublishTime() == null ? 0 : post.getPublishTime().getTime() / 1000);
        detail.put("format_content", parseJson(post.getFormatContent(), new LinkedHashMap<>()));
        detail.put("comments", mapper.selectApprovedComments(postId).stream().map(this::commentMap).collect(Collectors.toList()));
        detail.put("related", mapper.selectRelatedPosts(post.getCategoryId(), postId, 9).stream()
                .map(this::postSummary).collect(Collectors.toList()));

        Map<String, Object> response = ok();
        response.put("page_title", post.getPostTitle());
        response.put("post", detail);
        return response;
    }

    private void applyCategoryDefaults(CrowlookCategory category)
    {
        if (category.getParentId() == null) category.setParentId(0L);
        if (category.getSortOrder() == null) category.setSortOrder(0);
        if (blank(category.getStatus())) category.setStatus("0");
        if (category.getArticleStyle() == null) category.setArticleStyle(false);
        if (category.getHidden() == null) category.setHidden(false);
    }

    private void validateCategoryParent(CrowlookCategory category)
    {
        Long parentId = category.getParentId();
        int depth = 0;
        while (parentId != null && parentId > 0)
        {
            if (category.getCategoryId() != null && category.getCategoryId().equals(parentId))
            {
                throw new ServiceException("上级分类不能是自身或自己的子分类");
            }
            CrowlookCategory parent = mapper.selectCategoryById(parentId);
            if (parent == null) throw new ServiceException("所选上级分类不存在");
            parentId = parent.getParentId();
            if (++depth > 100) throw new ServiceException("分类层级存在循环，请检查上级分类");
        }
    }

    private void applyPostDefaults(CrowlookPost post)
    {
        if (blank(post.getFormat())) post.setFormat("");
        if (blank(post.getStatus())) post.setStatus("1");
        if (post.getFeatured() == null) post.setFeatured(false);
        if (post.getSortOrder() == null) post.setSortOrder(0);
        if (post.getViews() == null) post.setViews(0L);
        if (post.getFavoriteCount() == null) post.setFavoriteCount(0L);
        if (post.getMode() == null) post.setMode(1);
        if (post.getStyle() == null) post.setStyle(1);
        if (post.getPublishTime() == null) post.setPublishTime(new Date());
        if (blank(post.getCategoryIds()) && post.getCategoryId() != null) post.setCategoryIds(String.valueOf(post.getCategoryId()));
    }

    private void normalizePage(CrowlookPage page)
    {
        if (blank(page.getStatus())) page.setStatus("1");
        if (page.getSortOrder() == null) page.setSortOrder(0);
        page.setModulesJson(normalizeJson(page.getModulesJson(), "[]", "页面模块"));
        page.setConfigJson(normalizeJson(page.getConfigJson(), "{}", "页面配置"));
    }

    private void importCategoryNodes(List<Object> nodes, Long parentId, String username, Map<String, Integer> result)
    {
        int order = 0;
        for (Object node : nodes)
        {
            Map<String, Object> source = asMap(node);
            Long id = number(source.get("id"));
            if (id == null) continue;
            CrowlookCategory category = new CrowlookCategory();
            category.setCategoryId(id);
            category.setParentId(number(source.get("parent")) == null ? parentId : number(source.get("parent")));
            category.setCategoryName(text(source.get("name")));
            category.setSlug(text(source.get("slug")));
            category.setIcon(text(source.get("icon")));
            category.setCoverUrl(text(source.get("thumbnail")));
            category.setVideoUrl(text(source.get("list_video")));
            category.setPosterUrl(text(source.get("list_poster")));
            category.setHeadingImage(text(source.get("list_hd")));
            category.setSortOrder(order++);
            category.setStatus("0");
            category.setArticleStyle(bool(source.get("pic_new")));
            category.setHidden(bool(source.get("list_hide")));
            category.setCreateBy(username);
            mapper.upsertCategory(category);
            result.put("categories", result.get("categories") + 1);
            importCategoryNodes(asList(source.get("children")), id, username, result);
        }
    }

    private void importPage(String key, Map<String, Object> source, String username, int order, Map<String, Integer> result)
    {
        if (source.isEmpty()) return;
        Map<String, Object> meta = asMap(source.get("module_page"));
        Long pageId = number(meta.get("id"));
        if (pageId == null) return;
        CrowlookPage page = new CrowlookPage();
        page.setPageId(pageId);
        page.setPageKey(key.startsWith("module") ? "module_page:" + pageId : key);
        page.setPageTitle(text(source.get("page_title")));
        page.setPageName(text(meta.get("name")));
        page.setSlug(text(meta.get("slug")));
        page.setModulesJson(json(source.get("modules"), "[]"));
        page.setConfigJson(json(source.get("config"), "{}"));
        page.setBackgroundColor(text(meta.get("bg_color")));
        page.setBackgroundImage(text(meta.get("bg_img")));
        page.setStatus("0");
        page.setSortOrder(order);
        page.setCreateBy(username);
        mapper.upsertPage(page);
        result.put("pages", result.get("pages") + 1);
    }

    private void importPost(Map<String, Object> source, String username, Map<String, Integer> result)
    {
        Long postId = number(source.get("id"));
        if (postId == null) return;
        List<Object> categories = asList(source.get("category"));
        List<String> categoryIds = new ArrayList<>();
        for (Object item : categories)
        {
            Long categoryId = number(asMap(item).get("id"));
            if (categoryId != null) categoryIds.add(String.valueOf(categoryId));
        }
        CrowlookPost post = new CrowlookPost();
        post.setPostId(postId);
        post.setPostTitle(text(source.get("title")));
        post.setSlug(text(source.get("name")));
        post.setExcerpt(text(source.get("excerpt")));
        post.setThumbnail(text(source.get("thumbnail")));
        post.setFormat(text(source.get("format")));
        post.setFormatContent(json(source.get("format_content"), "{}"));
        post.setCategoryId(categoryIds.isEmpty() ? null : Long.valueOf(categoryIds.get(0)));
        post.setCategoryIds(String.join(",", categoryIds));
        post.setContent(text(source.get("content")));
        post.setStatus("0");
        post.setFeatured(false);
        post.setSortOrder(0);
        post.setViews(defaultLong(source.get("views"), 0L));
        post.setFavoriteCount(defaultLong(source.get("fav_count"), 0L));
        post.setMode(defaultInt(source.get("mode"), 1));
        post.setStyle(defaultInt(source.get("style"), 1));
        Long timestamp = number(source.get("timestamp"));
        post.setPublishTime(timestamp == null ? new Date() : new Date(timestamp * 1000));
        post.setCreateBy(username);
        mapper.upsertPost(post);
        result.put("posts", result.get("posts") + 1);

        for (Object item : asList(source.get("comments")))
        {
            Map<String, Object> value = asMap(item);
            CrowlookComment comment = new CrowlookComment();
            comment.setCommentId(number(value.get("id")));
            comment.setPostId(postId);
            comment.setParentId(number(value.get("parent")));
            comment.setNickname(text(value.get("nickname")));
            if (blank(comment.getNickname())) comment.setNickname(text(asMap(value.get("author")).get("nickname")));
            comment.setAvatar(text(value.get("avatar")));
            if (blank(comment.getAvatar())) comment.setAvatar(text(asMap(value.get("author")).get("avatar")));
            comment.setContent(text(value.get("content")));
            comment.setStatus("1");
            Long commentTime = number(value.get("timestamp"));
            comment.setCreateTime(commentTime == null ? new Date() : new Date(commentTime * 1000));
            comment.setCreateBy(username);
            if (comment.getCommentId() == null) mapper.insertComment(comment); else mapper.upsertComment(comment);
            result.put("comments", result.get("comments") + 1);
        }
    }

    private Map<String, Object> categoryMap(CrowlookCategory category)
    {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", category.getCategoryId());
        item.put("parent", category.getParentId());
        item.put("name", category.getCategoryName());
        item.put("slug", category.getSlug());
        item.put("icon", category.getIcon());
        item.put("thumbnail", category.getCoverUrl());
        item.put("list_video", category.getVideoUrl());
        item.put("list_poster", category.getPosterUrl());
        item.put("list_hd", category.getHeadingImage());
        item.put("pic_new", Boolean.TRUE.equals(category.getArticleStyle()));
        item.put("list_hide", Boolean.TRUE.equals(category.getHidden()));
        return item;
    }

    private Map<String, Object> postSummary(CrowlookPost post)
    {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", post.getPostId());
        item.put("title", post.getPostTitle());
        item.put("excerpt", post.getExcerpt());
        item.put("thumbnail", post.getThumbnail());
        item.put("name", post.getSlug());
        item.put("format", post.getFormat());
        List<Map<String, Object>> categories = new ArrayList<>();
        if (post.getCategoryId() != null)
        {
            Map<String, Object> category = new LinkedHashMap<>();
            category.put("id", post.getCategoryId());
            category.put("name", post.getCategoryName());
            categories.add(category);
        }
        item.put("category", categories);
        item.put("comment_count", post.getCommentCount() == null ? 0 : post.getCommentCount());
        item.put("fav_count", post.getFavoriteCount() == null ? 0 : post.getFavoriteCount());
        item.put("mode", post.getMode());
        item.put("style", post.getStyle());
        return item;
    }

    private void hydratePostModules(List<Object> modules)
    {
        for (Object value : modules)
        {
            Map<String, Object> module = asMap(value);
            if (!"post".equals(text(module.get("type")))) continue;
            Map<String, Object> setting = asMap(module.get("setting"));
            Long categoryId = number(setting.get("cat"));
            int limit = Math.max(1, Math.min(defaultInt(setting.get("number"), 6), 30));
            List<CrowlookPost> posts = mapper.selectPublicPosts(categoryId, null, 0, limit);
            module.put("content", posts.stream().map(this::postSummary).collect(Collectors.toList()));
            long total = mapper.countPublicPosts(categoryId, null);
            setting.put("total_pages", total == 0 ? 0 : (int) Math.ceil(total / (double) limit));
            setting.put("next_cursor", 0);
            module.put("setting", setting);
        }
    }

    private Map<String, Object> commentMap(CrowlookComment comment)
    {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", comment.getCommentId());
        item.put("nickname", comment.getNickname());
        item.put("avatar", comment.getAvatar());
        item.put("content", comment.getContent());
        item.put("parent", comment.getParentId() == null ? 0 : comment.getParentId());
        item.put("reply_to", comment.getParentNickname());
        item.put("timestamp", comment.getCreateTime() == null ? 0 : comment.getCreateTime().getTime() / 1000);
        return item;
    }

    private Map<String, Object> ok()
    {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("errcode", 0);
        return response;
    }

    private Map<String, Object> notFound(String message)
    {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("errcode", 404);
        response.put("message", message);
        return response;
    }

    private String normalizeJson(String value, String fallback, String label)
    {
        if (blank(value)) return fallback;
        try
        {
            return objectMapper.writeValueAsString(objectMapper.readTree(value));
        }
        catch (Exception e)
        {
            throw new ServiceException(label + "不是合法 JSON");
        }
    }

    private Object parseJson(String value, Object fallback)
    {
        if (blank(value)) return fallback;
        try
        {
            return objectMapper.readValue(value, new TypeReference<Object>() { });
        }
        catch (Exception e)
        {
            return fallback;
        }
    }

    private String json(Object value, String fallback)
    {
        if (value == null) return fallback;
        try
        {
            return objectMapper.writeValueAsString(value);
        }
        catch (Exception e)
        {
            throw new ServiceException("快照中包含无法解析的数据");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value)
    {
        return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    private List<Object> asList(Object value)
    {
        return value instanceof List ? (List<Object>) value : new ArrayList<>();
    }

    private Long number(Object value)
    {
        if (value instanceof Number) return ((Number) value).longValue();
        if (value == null || blank(String.valueOf(value))) return null;
        try { return Long.valueOf(String.valueOf(value)); } catch (NumberFormatException e) { return null; }
    }

    private Long defaultLong(Object value, Long fallback)
    {
        Long parsed = number(value);
        return parsed == null ? fallback : parsed;
    }

    private Integer defaultInt(Object value, Integer fallback)
    {
        Long parsed = number(value);
        return parsed == null ? fallback : parsed.intValue();
    }

    private boolean bool(Object value)
    {
        return Boolean.TRUE.equals(value) || "1".equals(String.valueOf(value)) || "true".equalsIgnoreCase(String.valueOf(value));
    }

    private String text(Object value)
    {
        return value == null ? "" : String.valueOf(value);
    }

    private boolean blank(String value)
    {
        return value == null || value.trim().isEmpty();
    }

    private String formatDate(Date value, String pattern)
    {
        return value == null ? "" : new SimpleDateFormat(pattern).format(value);
    }
}
