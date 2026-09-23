package com.ruoyi.web.controller.crowlook;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.CrowlookPost;
import com.ruoyi.system.service.ICrowlookContentService;

/** Crowlook 作品内容管理。 */
@RestController
@RequestMapping("/crowlook/post")
public class CrowlookPostController extends BaseController
{
    @Autowired
    private ICrowlookContentService contentService;

    @PreAuthorize("@ss.hasPermi('crowlook:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrowlookPost post)
    {
        startPage();
        List<CrowlookPost> list = contentService.selectPostList(post);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crowlook:post:query')")
    @GetMapping("/{postId}")
    public AjaxResult getInfo(@PathVariable Long postId)
    {
        return success(contentService.selectPostById(postId));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:post:add')")
    @Log(title = "Crowlook 内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CrowlookPost post)
    {
        post.setCreateBy(getUsername());
        return toAjax(contentService.insertPost(post));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:post:edit')")
    @Log(title = "Crowlook 内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CrowlookPost post)
    {
        post.setUpdateBy(getUsername());
        return toAjax(contentService.updatePost(post));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:post:remove')")
    @Log(title = "Crowlook 内容", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds)
    {
        return toAjax(contentService.deletePostByIds(postIds));
    }
}
