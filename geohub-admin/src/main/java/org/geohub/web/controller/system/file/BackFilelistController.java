package org.geohub.web.controller.system.file;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.geohub.common.annotation.Log;
import org.geohub.common.core.controller.BaseController;
import org.geohub.common.core.domain.AjaxResult;
import org.geohub.common.core.page.TableDataInfo;
import org.geohub.common.enums.BusinessType;
import org.geohub.common.utils.poi.ExcelUtil;
import org.geohub.system.domain.BackFilelist;
import org.geohub.system.service.file.IBackFilelistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 已上传文件列表Controller
 */
@Api("已上传文件列表")
@RestController
@RequestMapping("/system/filelist")
public class BackFilelistController extends BaseController
{
    @Autowired
    private IBackFilelistService backFilelistService;

    /**
     * 查询已上传文件列表列表
     */
    @ApiOperation("查询已上传文件列表列表")
    @PreAuthorize("@ss.hasPermi('system:filelist:query')")
    @GetMapping("/list")
    public TableDataInfo list(BackFilelist backFilelist)
    {
        startPage();
        List<BackFilelist> list = backFilelistService.selectBackFilelistList(backFilelist);
        return getDataTable(list);
    }

    /**
     * 导出已上传文件列表列表
     */
    @ApiOperation("导出已上传文件列表列表")
    @PreAuthorize("@ss.hasPermi('system:filelist:export')")
    @Log(title = "已上传文件列表", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(BackFilelist backFilelist)
    {
        List<BackFilelist> list = backFilelistService.selectBackFilelistList(backFilelist);
        ExcelUtil<BackFilelist> util = new ExcelUtil<BackFilelist>(BackFilelist.class);
        return util.exportExcel(list, "filelist");
    }

    /**
     * 获取已上传文件列表详细信息
     */
    @ApiOperation("获取已上传文件列表详细信息")
    @PreAuthorize("@ss.hasPermi('system:filelist:detail')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(backFilelistService.selectBackFilelistById(id));
    }

    /**
     * 新增已上传文件列表
     */
    @ApiOperation("新增已上传文件列表")
    @PreAuthorize("@ss.hasPermi('system:filelist:add')")
    @Log(title = "已上传文件列表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BackFilelist backFilelist)
    {
        return toAjax(backFilelistService.insertBackFilelist(backFilelist));
    }

    /**
     * 修改已上传文件列表
     */
    @ApiOperation("修改已上传文件列表")
    @PreAuthorize("@ss.hasPermi('system:filelist:edit')")
    @Log(title = "已上传文件列表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BackFilelist backFilelist)
    {
        return toAjax(backFilelistService.updateBackFilelist(backFilelist));
    }

    /**
     * 删除已上传文件列表
     */
    @ApiOperation("删除已上传文件列表")
    @PreAuthorize("@ss.hasPermi('system:filelist:remove')")
    @Log(title = "已上传文件列表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(backFilelistService.deleteBackFilelistByIds(ids));
    }

    @ApiOperation("文件列表页下载文件")
    //@PreAuthorize("@ss.hasPermi('system:filelist:download')")
    @Log(title = "下载文件", businessType = BusinessType.DELETE)
    @PostMapping("/download")
    public void downloadFile(HttpServletResponse response, BackFilelist file)
    {
        try {
            response.getOutputStream().write(FileCopyUtils.copyToByteArray(new File(file.getLocation())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
