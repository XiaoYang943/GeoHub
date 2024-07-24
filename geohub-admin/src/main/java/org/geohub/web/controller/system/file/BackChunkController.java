package org.geohub.web.controller.system.file;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.geohub.common.annotation.Log;
import org.geohub.common.core.controller.BaseController;
import org.geohub.common.core.domain.AjaxResult;
import org.geohub.common.core.page.TableDataInfo;
import org.geohub.common.enums.BusinessType;
import org.geohub.common.utils.poi.ExcelUtil;
import org.geohub.system.domain.BackChunk;
import org.geohub.system.service.file.IBackChunkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件分片管理Controller
 */
@Api("文件分片管理")
@RestController
@RequestMapping("/system/chunk")
public class BackChunkController extends BaseController
{
    @Autowired
    private IBackChunkService backChunkService;

    /**
     * 查询文件分片管理列表
     */
    @ApiOperation("查询文件分片管理列表")
    @PreAuthorize("@ss.hasPermi('file:chunk:list')")
    @GetMapping("/list")
    public TableDataInfo list(BackChunk backChunk)
    {
        startPage();
        List<BackChunk> list = backChunkService.selectBackChunkList(backChunk);
        return getDataTable(list);
    }

    /**
     * 导出文件分片管理列表
     */
    @ApiOperation("导出文件分片管理列表")
    @PreAuthorize("@ss.hasPermi('system:filelist:export')")
    @Log(title = "文件分片管理", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(BackChunk backChunk)
    {
        List<BackChunk> list = backChunkService.selectBackChunkList(backChunk);
        ExcelUtil<BackChunk> util = new ExcelUtil<BackChunk>(BackChunk.class);
        return util.exportExcel(list, "chunk");
    }

    /**
     * 获取文件分片管理详细信息
     */
    @ApiOperation("获取文件分片管理详细信息")
    @PreAuthorize("@ss.hasPermi('file:chunk:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(backChunkService.selectBackChunkById(id));
    }

    /**
     * 新增文件分片管理
     */
    @ApiOperation("新增文件分片管理")
    @PreAuthorize("@ss.hasPermi('file:chunk:add')")
    @Log(title = "文件分片管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BackChunk backChunk)
    {
        return toAjax(backChunkService.insertBackChunk(backChunk));
    }

    /**
     * 修改文件分片管理
     */
    @ApiOperation("修改文件分片管理")
    @PreAuthorize("@ss.hasPermi('file:chunk:edit')")
    @Log(title = "文件分片管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BackChunk backChunk)
    {
        return toAjax(backChunkService.updateBackChunk(backChunk));
    }

    /**
     * 删除文件分片管理
     */
    @ApiOperation("删除文件分片管理")
    @PreAuthorize("@ss.hasPermi('file:chunk:remove')")
    @Log(title = "文件分片管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(backChunkService.deleteBackChunkByIds(ids));
    }
}
