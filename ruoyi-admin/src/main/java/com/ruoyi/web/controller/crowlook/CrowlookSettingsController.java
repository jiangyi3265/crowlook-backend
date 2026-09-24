package com.ruoyi.web.controller.crowlook;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.ICrowlookContentService;

/** Crowlook 品牌、客服、门店与用户端通用设置。 */
@RestController
@RequestMapping("/crowlook/settings")
public class CrowlookSettingsController extends BaseController
{
    @Autowired
    private ICrowlookContentService contentService;

    @PreAuthorize("@ss.hasPermi('crowlook:settings:list')")
    @GetMapping
    public AjaxResult getInfo()
    {
        return success(contentService.getSiteSettings());
    }

    @PreAuthorize("@ss.hasPermi('crowlook:settings:edit')")
    @Log(title = "Crowlook 用户端设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Map<String, Object> settings)
    {
        return toAjax(contentService.updateSiteSettings(settings, getUsername()));
    }
}
