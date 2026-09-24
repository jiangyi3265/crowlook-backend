package com.ruoyi.web.controller.crowlook;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.system.domain.CrowlookComment;
import com.ruoyi.system.domain.CrowlookPost;
import com.ruoyi.system.service.ICrowlookContentService;

/** H5 与微信小程序共用的匿名内容 API。 */
@RestController
@RequestMapping("/api")
public class CrowlookPublicController
{
    @Autowired
    private ICrowlookContentService contentService;

    @GetMapping("/category/list.json")
    public Map<String, Object> categories()
    {
        return contentService.getPublicCategories();
    }

    @GetMapping("/module/page.json")
    public Map<String, Object> page(@RequestParam("key") String key,
            @RequestParam(value = "id", required = false) Long id)
    {
        return contentService.getPublicPage(key, id);
    }

    @GetMapping("/post/list.json")
    public Map<String, Object> posts(@RequestParam(value = "category_id", required = false) Long categoryId,
            @RequestParam(value = "s", required = false) String keyword,
            @RequestParam(value = "paged", required = false) Integer page,
            @RequestParam(value = "page_size", required = false) Integer pageSize)
    {
        return contentService.getPublicPosts(categoryId, keyword, page, pageSize);
    }

    @GetMapping("/post/get.json")
    public Map<String, Object> post(@RequestParam("id") Long id)
    {
        return contentService.getPublicPost(id);
    }

    @PostMapping("/post/comment.json")
    public Map<String, Object> comment(@RequestBody CrowlookComment comment)
    {
        String content = comment.getContent() == null ? "" : comment.getContent().replaceAll("<[^>]*>", "").trim();
        if (content.isEmpty() || content.length() > 1000 || comment.getPostId() == null)
        {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("errcode", 400);
            error.put("message", "评论内容无效");
            return error;
        }
        CrowlookPost post = contentService.selectPostById(comment.getPostId());
        if (post == null || !"0".equals(post.getStatus()))
        {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("errcode", 404);
            error.put("message", "作品不存在或尚未发布");
            return error;
        }
        comment.setContent(content);
        comment.setStatus("0");
        comment.setCreateBy("public");
        contentService.insertComment(comment);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("errcode", 0);
        response.put("message", "评论已提交，审核通过后显示");
        return response;
    }

    @PostMapping("/post/favorite.json")
    public Map<String, Object> favorite(@RequestBody Map<String, Object> body)
    {
        Long postId = body.get("postId") instanceof Number ? ((Number) body.get("postId")).longValue() : null;
        String deviceKey = body.get("deviceKey") == null ? "" : String.valueOf(body.get("deviceKey")).trim();
        boolean active = Boolean.TRUE.equals(body.get("active")) || "true".equalsIgnoreCase(String.valueOf(body.get("active")));
        if (postId == null || !deviceKey.matches("[A-Za-z0-9_-]{8,80}"))
        {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("errcode", 400);
            error.put("message", "点赞请求无效");
            return error;
        }
        return contentService.setPublicFavorite(postId, deviceKey, active);
    }
}
