package com.ruoyi.web.controller.crowlook;

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
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.CrowlookCategory;
import com.ruoyi.system.service.ICrowlookContentService;

/** Crowlook 分类管理。 */
@RestController
@RequestMapping("/crowlook/category")
public class CrowlookCategoryController extends BaseController
{
    @Autowired
    private ICrowlookContentService contentService;

    @PreAuthorize("@ss.hasPermi('crowlook:category:list')")
    @GetMapping("/list")
    public AjaxResult list(CrowlookCategory category)
    {
        return success(contentService.selectCategoryList(category));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:category:query')")
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId)
    {
        return success(contentService.selectCategoryById(categoryId));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:category:add')")
    @Log(title = "Crowlook 分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CrowlookCategory category)
    {
        category.setCreateBy(getUsername());
        return toAjax(contentService.insertCategory(category));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:category:edit')")
    @Log(title = "Crowlook 分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CrowlookCategory category)
    {
        category.setUpdateBy(getUsername());
        return toAjax(contentService.updateCategory(category));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:category:remove')")
    @Log(title = "Crowlook 分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return toAjax(contentService.deleteCategoryByIds(categoryIds));
    }
}
