package org.geohub.web.controller.system.file;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.geohub.common.core.controller.BaseController;
import org.geohub.common.core.domain.AjaxResult;
import org.geohub.system.domain.BackChunk;
import org.geohub.system.domain.BackFilelist;
import org.geohub.system.domain.vo.CheckChunkVO;
import org.geohub.system.service.file.IBackFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件管理Controller
 */
@Api("文件管理")
@RestController
@RequestMapping("/system/file")
public class BackFileController extends BaseController
{
    @Autowired
    private IBackFileService backFileService;

    /**
     * 上传文件
     */
    @ApiOperation("上传文件")
    @PreAuthorize("@ss.hasPermi('system:file:upload')")
    @PostMapping("/upload")
    public AjaxResult postFileUpload(@ModelAttribute BackChunk chunk, HttpServletResponse response)
    {
        int i = backFileService.postFileUpload(chunk, response);
        AjaxResult aR = toAjax(i);
        if(aR.isSuccess()) {
            aR.put("needMerge", true);
            aR.put("result", true);
        }
        return aR;
    }

    /**
     * 检查文件上传状态
     */
    @ApiOperation("检查文件上传状态")
    @PreAuthorize("@ss.hasPermi('system:file:check')")
    @GetMapping("/upload")
    public CheckChunkVO getFileUpload(@ModelAttribute BackChunk chunk, HttpServletResponse response)
    {
        //查询根据md5查询文件是否存在
        CheckChunkVO fileUpload = backFileService.getFileUpload(chunk, response);
        return fileUpload;
    }

    /**
     *
     */
//    @ApiOperation("删除文件")
//    @PreAuthorize("@ss.hasPermi('system:file:remove')")
//    @Log(title = "用户删除文件", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{id}")
//    public AjaxResult remove(@PathVariable("id") Long id)
//    {
//        return toAjax(backFileService.deleteBackFileByIds(id));
//    }

    /**
     * 检查文件上传状态
     */
    @ApiOperation("合并文件")
    //@PreAuthorize("@ss.hasPermi('system:file:merge')")
    @PostMapping("/merge")
    public AjaxResult merge(BackFilelist fileInfo)
    {
        String path = backFileService.mergeFile(fileInfo);
        return AjaxResult.success("操作成功", path);
    }
}
