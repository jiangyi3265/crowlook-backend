package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.CrowlookCategory;
import com.ruoyi.system.domain.CrowlookComment;
import com.ruoyi.system.domain.CrowlookPage;
import com.ruoyi.system.domain.CrowlookPost;

/** Crowlook 内容中心数据访问。 */
public interface CrowlookContentMapper
{
    List<CrowlookCategory> selectCategoryList(CrowlookCategory category);
    List<CrowlookCategory> selectPublishedCategories();
    CrowlookCategory selectCategoryById(Long categoryId);
    int insertCategory(CrowlookCategory category);
    int updateCategory(CrowlookCategory category);
    int upsertCategory(CrowlookCategory category);
    int deleteCategoryByIds(Long[] categoryIds);
    int countCategoryChildren(Long categoryId);
    int countCategoryPosts(Long categoryId);

    List<CrowlookPost> selectPostList(CrowlookPost post);
    CrowlookPost selectPostById(Long postId);
    int insertPost(CrowlookPost post);
    int updatePost(CrowlookPost post);
    int upsertPost(CrowlookPost post);
    int deletePostByIds(Long[] postIds);
    int deleteCommentsByPostIds(Long[] postIds);
    int incrementPostViews(Long postId);
    List<CrowlookPost> selectPublicPosts(@Param("categoryId") Long categoryId, @Param("keyword") String keyword,
            @Param("offset") int offset, @Param("limit") int limit);
    long countPublicPosts(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);
    List<CrowlookPost> selectRelatedPosts(@Param("categoryId") Long categoryId, @Param("postId") Long postId,
            @Param("limit") int limit);

    List<CrowlookPage> selectPageList(CrowlookPage page);
    CrowlookPage selectPageById(Long pageId);
    CrowlookPage selectPageByKey(@Param("pageKey") String pageKey);
    CrowlookPage selectPublishedPage(@Param("pageKey") String pageKey, @Param("pageId") Long pageId);
    int insertPage(CrowlookPage page);
    int updatePage(CrowlookPage page);
    int upsertPage(CrowlookPage page);
    int updatePageConfig(@Param("pageId") Long pageId, @Param("configJson") String configJson,
            @Param("username") String username);
    int deletePageByIds(Long[] pageIds);

    List<CrowlookComment> selectCommentList(CrowlookComment comment);
    List<CrowlookComment> selectApprovedComments(Long postId);
    CrowlookComment selectCommentById(Long commentId);
    int insertComment(CrowlookComment comment);
    int updateComment(CrowlookComment comment);
    int upsertComment(CrowlookComment comment);
    int deleteCommentByIds(Long[] commentIds);

    int insertFavorite(@Param("postId") Long postId, @Param("deviceKey") String deviceKey);
    int deleteFavorite(@Param("postId") Long postId, @Param("deviceKey") String deviceKey);
    int changePostFavoriteCount(@Param("postId") Long postId, @Param("delta") int delta);
    int deleteFavoritesByPostIds(Long[] postIds);

    Map<String, Object> selectOverview();
}
