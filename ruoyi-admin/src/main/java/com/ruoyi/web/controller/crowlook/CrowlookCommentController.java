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
import com.ruoyi.system.domain.CrowlookComment;
import com.ruoyi.system.service.ICrowlookContentService;

/** Crowlook 评论审核。 */
@RestController
@RequestMapping("/crowlook/comment")
public class CrowlookCommentController extends BaseController
{
    @Autowired
    private ICrowlookContentService contentService;

    @PreAuthorize("@ss.hasPermi('crowlook:comment:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrowlookComment comment)
    {
        startPage();
        List<CrowlookComment> list = contentService.selectCommentList(comment);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crowlook:comment:query')")
    @GetMapping("/{commentId}")
    public AjaxResult getInfo(@PathVariable Long commentId)
    {
        return success(contentService.selectCommentById(commentId));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:comment:add')")
    @Log(title = "Crowlook 评论", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CrowlookComment comment)
    {
        comment.setCreateBy(getUsername());
        return toAjax(contentService.insertComment(comment));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:comment:edit')")
    @Log(title = "Crowlook 评论", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CrowlookComment comment)
    {
        comment.setUpdateBy(getUsername());
        return toAjax(contentService.updateComment(comment));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:comment:remove')")
    @Log(title = "Crowlook 评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/{commentIds}")
    public AjaxResult remove(@PathVariable Long[] commentIds)
    {
        return toAjax(contentService.deleteCommentByIds(commentIds));
    }
}
