package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.CrowlookCategory;
import com.ruoyi.system.domain.CrowlookComment;
import com.ruoyi.system.domain.CrowlookPage;
import com.ruoyi.system.domain.CrowlookPost;

/** Crowlook 三端内容服务。 */
public interface ICrowlookContentService
{
    List<CrowlookCategory> selectCategoryList(CrowlookCategory category);
    CrowlookCategory selectCategoryById(Long categoryId);
    int insertCategory(CrowlookCategory category);
    int updateCategory(CrowlookCategory category);
    int deleteCategoryByIds(Long[] categoryIds);

    List<CrowlookPost> selectPostList(CrowlookPost post);
    CrowlookPost selectPostById(Long postId);
    int insertPost(CrowlookPost post);
    int updatePost(CrowlookPost post);
    int deletePostByIds(Long[] postIds);

    List<CrowlookPage> selectPageList(CrowlookPage page);
    CrowlookPage selectPageById(Long pageId);
    int insertPage(CrowlookPage page);
    int updatePage(CrowlookPage page);
    int deletePageByIds(Long[] pageIds);

    List<CrowlookComment> selectCommentList(CrowlookComment comment);
    CrowlookComment selectCommentById(Long commentId);
    int insertComment(CrowlookComment comment);
    int updateComment(CrowlookComment comment);
    int deleteCommentByIds(Long[] commentIds);

    Map<String, Object> selectOverview();
    Map<String, Integer> importSnapshot(Map<String, Object> snapshot, String username);
    Map<String, Object> getPublicCategories();
    Map<String, Object> getPublicPage(String pageKey, Long pageId);
    Map<String, Object> getPublicPosts(Long categoryId, String keyword, Integer page, Integer pageSize);
    Map<String, Object> getPublicPost(Long postId);
}
