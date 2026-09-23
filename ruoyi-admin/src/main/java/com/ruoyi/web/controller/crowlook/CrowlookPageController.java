package com.ruoyi.web.controller.crowlook;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.system.domain.CrowlookPage;
import com.ruoyi.system.service.ICrowlookContentService;

/** Crowlook 页面编排与快照迁移。 */
@RestController
@RequestMapping("/crowlook/page")
public class CrowlookPageController extends BaseController
{
    @Autowired
    private ICrowlookContentService contentService;

    @PreAuthorize("@ss.hasPermi('crowlook:page:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrowlookPage page)
    {
        startPage();
        List<CrowlookPage> list = contentService.selectPageList(page);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crowlook:page:query')")
    @GetMapping("/{pageId}")
    public AjaxResult getInfo(@PathVariable Long pageId)
    {
        return success(contentService.selectPageById(pageId));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:page:add')")
    @Log(title = "Crowlook 页面", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody CrowlookPage page)
    {
        page.setCreateBy(getUsername());
        return toAjax(contentService.insertPage(page));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:page:edit')")
    @Log(title = "Crowlook 页面", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody CrowlookPage page)
    {
        page.setUpdateBy(getUsername());
        return toAjax(contentService.updatePage(page));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:page:remove')")
    @Log(title = "Crowlook 页面", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pageIds}")
    public AjaxResult remove(@PathVariable Long[] pageIds)
    {
        return toAjax(contentService.deletePageByIds(pageIds));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:page:import')")
    @Log(title = "Crowlook 快照导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importSnapshot(@RequestBody Map<String, Object> snapshot)
    {
        return success(contentService.importSnapshot(snapshot, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('crowlook:page:list')")
    @GetMapping("/overview")
    public AjaxResult overview()
    {
        return success(contentService.selectOverview());
    }
}
