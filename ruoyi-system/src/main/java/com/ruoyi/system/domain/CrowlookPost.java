package com.ruoyi.system.domain;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/** Crowlook 作品、文章或视频内容。 */
public class CrowlookPost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long postId;
    private String postTitle;
    private String slug;
    private String excerpt;
    private String thumbnail;
    private String format;
    private String formatContent;
    private Long categoryId;
    private String categoryIds;
    private String categoryName;
    private String content;
    private String status;
    private Boolean featured;
    private Integer sortOrder;
    private Long views;
    private Long favoriteCount;
    private Long commentCount;
    private Integer mode;
    private Integer style;
    private Date publishTime;

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    @NotBlank(message = "内容标题不能为空")
    @Size(max = 160, message = "内容标题不能超过160个字符")
    public String getPostTitle() { return postTitle; }
    public void setPostTitle(String postTitle) { this.postTitle = postTitle; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }
    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getFormatContent() { return formatContent; }
    public void setFormatContent(String formatContent) { this.formatContent = formatContent; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryIds() { return categoryIds; }
    public void setCategoryIds(String categoryIds) { this.categoryIds = categoryIds; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Long getViews() { return views; }
    public void setViews(Long views) { this.views = views; }
    public Long getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Long favoriteCount) { this.favoriteCount = favoriteCount; }
    public Long getCommentCount() { return commentCount; }
    public void setCommentCount(Long commentCount) { this.commentCount = commentCount; }
    public Integer getMode() { return mode; }
    public void setMode(Integer mode) { this.mode = mode; }
    public Integer getStyle() { return style; }
    public void setStyle(Integer style) { this.style = style; }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getPublishTime() { return publishTime; }
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }
}
