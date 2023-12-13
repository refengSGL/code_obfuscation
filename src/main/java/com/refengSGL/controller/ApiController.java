package com.refengSGL.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.refengSGL.entity.File;
import com.refengSGL.entity.request.PageBo;
import com.refengSGL.entity.response.FilePageDto;
import com.refengSGL.entity.response.FileVo;
import com.refengSGL.entity.response.PageVo;
import com.refengSGL.exception.CodeException;
import com.refengSGL.service.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.refengSGL.constant.ResultCode.*;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;

    private static final String[] C_MIME_TYPES = {"text/x-csrc", "text/plain", "text/x-c++src", "application/x-csrc", "application/octet-stream"};

    /**
     * 获取配置文件中的文件存储路径，将文件存储到本地/服务器
     */
    @Value("${file.uploadUrl}")
    private String uploadPath;

    /**
     * 查询当前登录用户所有的文件
     */
    @GetMapping("/file/list")
    public SaResult getFileList() {
        List<FileVo> files = apiService.getFileInfoByUserId();
        if (!Objects.isNull(files)) {
            return SaResult.ok("获取成功").setData(files).setCode(SUCCESS);
        }

        return SaResult.error("获取失败").setCode(ERROR);
    }

    /**
     * 根据文件ID查询文件信息
     */
    @GetMapping("/file/id/{fileId}")
    public SaResult getFileInfo(@PathVariable Long fileId) {
        if (Objects.isNull(fileId)) {
            log.info("File id is null");
            throw new CodeException("文件id为空");
        }
        FileVo safetyFile = apiService.getFileInfoByFileId(fileId);
        if (!Objects.isNull(safetyFile)) {
            return SaResult.ok("获取成功").setData(safetyFile).setCode(SUCCESS);
        }
        return SaResult.error("获取失败").setCode(ERROR);
    }


    /**
     * 文件上传
     */
    @PostMapping("/file/upload")
    public SaResult fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            log.info("Upload file is empty");
            throw new CodeException("上传文件为空");
        }
        // 对上传的文件名进行验证和过滤
        // 只允许字母、数字、下划线和短横线
        String originalFilename = file.getOriginalFilename();
        if (Objects.isNull(originalFilename)) {
            throw new CodeException("上传文件名为空");
        }
        String pattern = "^[a-zA-Z0-9_-]+\\.[a-zA-Z0-9]+$";
        if (!originalFilename.matches(pattern)) {
            log.info("The upload file name contains invalid characters");
            throw new CodeException("上传文件名包含非法字符");
        }

        // 获取文件名部分
        String safeFilename = FilenameUtils.getName(originalFilename);
        if (!originalFilename.equals(safeFilename)) {
            log.info("The upload file name contains path separators");
            throw new CodeException("上传文件名包含路径分隔符");
        }
        // 替换双引号
        StringUtils.replaceChars(originalFilename, "\"", "_");
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());
        log.info("The MIME type of the current file is : {}", mimeType);
        boolean isValid = false;
        for (String cType : C_MIME_TYPES) {
            if (cType.equals(mimeType)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            log.info("The upload file is not a C source file");
            throw new CodeException("上传文件类型不符");
        }

        FileVo safetyFile = apiService.uploadFile(file, uploadPath);
        if (!Objects.isNull(safetyFile)) {
            return SaResult.ok("文件上传成功").setData(safetyFile).setCode(SUCCESS);
        }
        return SaResult.error("文件上传失败").setCode(ERROR);
    }

    /**
     * 文件下载
     */
    @GetMapping("/file/download/{fileName}")
    public SaResult fileDownload(@PathVariable(value = "fileName") String fileName, HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            log.info("File name is blank");
            throw new CodeException("文件名为空");
        }
        apiService.downloadFile(fileName, uploadPath, response);
        return SaResult.ok("文件下载成功").setCode(SUCCESS);
    }

    /**
     * tigress 保护操作
     */
    @PostMapping("/file/protect/ollvm/{fileName}")
    public SaResult performShellScript(@PathVariable(value = "fileName") String fileName, @RequestBody ArrayList<String> paramList) {
        if (StringUtils.isBlank(fileName)) {
            log.info("File name is blank");
            throw new CodeException("文件名为空");
        }
        if (Objects.isNull(paramList)) {
            log.info("Param list is null");
            throw new CodeException("参数列表为空");
        }
        log.info("参数列表paramList为：{}", paramList);
        FileVo safetyFile = apiService.performProtect(fileName, paramList);
        if (!Objects.isNull(safetyFile)) {
            return SaResult.ok("文件保护成功").setData(safetyFile).setCode(SUCCESS);
        }
        return SaResult.error("文件保护失败").setCode(ERROR);
    }

    /**
     * 根据文件名查询文件信息
     */
    @GetMapping("/file/fileName/{fileName}")
    public SaResult getFileInfoByFileName(@PathVariable String fileName) {
        return getResultByFileName(fileName, (name) -> {
            FileVo safetyFile = apiService.getFileInfoByFileName(name);
            if (!Objects.isNull(safetyFile)) {
                return SaResult.ok("获取成功").setData(safetyFile).setCode(SUCCESS);
            }
            return SaResult.error("获取失败").setCode(ERROR);
        });
    }

    /**
     * 获取执行保护操作日志信息
     */
    @GetMapping("/file/page")
    public SaResult getPageVoInfo(PageBo pageBo) {
        if (Objects.isNull(pageBo)) {
            log.info("PageBo is null");
            throw new CodeException("分页参数接收对象为空");
        }
        Page<FilePageDto> pageInfo = apiService.getPageVoInfo(pageBo);
        if (!Objects.isNull(pageInfo)) {
            return SaResult.ok("获取成功").setData(pageInfo).setCode(SUCCESS);
        }
        return SaResult.error("获取失败").setCode(ERROR);
    }

    /**
     * 测试原始文件信息内容
     */
    @GetMapping("/file/test")
    public SaResult getPageInfo(PageBo pageBo) {
        Integer page = pageBo.getPage();
        Integer limit = pageBo.getLimit();
        Page<File> pageInfo = new Page<>(page, limit);
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper.orderByDesc(File::getUpdateTime);
        apiService.page(pageInfo, fileLambdaQueryWrapper);
        return SaResult.ok("获取成功").setData(pageInfo).setCode(SUCCESS);
    }

    /**
     * 删除文件（文件名）
     */
    @DeleteMapping("/file/remove/fileName")
    public SaResult removeByFileName(@RequestParam("fileName") String fileName) {
        return getResultByFileName(fileName, (name) -> {
            boolean isDeleted = apiService.deleteFileByFileName(name);
            if (isDeleted) {
                return SaResult.ok("删除成功").setCode(MOVE_PERM);
            }
            return SaResult.error("删除失败").setCode(NOT_MODIFIED);
        });
    }

    /**
     * 删除文件（文件Id）
     */
    @DeleteMapping("/file/remove/fileId/{fileId}")
    public SaResult removeByFileId(@PathVariable Long fileId) {
        boolean isDeleted = apiService.deleteFileByFileId(fileId);
        if (isDeleted) {
            return SaResult.ok("删除成功").setCode(MOVE_PERM);
        }
        return SaResult.error("删除失败").setCode(NOT_MODIFIED);
    }

    /**
     * 判断文件名是否为空
     */
    private SaResult getResultByFileName(String fileName, Function<String, SaResult> function) {
        if (StringUtils.isBlank(fileName)) {
            log.info("File name is blank");
            throw new CodeException("文件名为空");
        }
        return function.apply(fileName);
    }

    /**
     * 保护接口
     */
    @PostMapping("/file/protect/{pType}/{fileName}")
    public SaResult protect(@PathVariable String fileName,
                            @PathVariable int pType,
                            @RequestBody List<String> arguments) {
        FileVo safetyFile = apiService.executeShell(fileName, pType, arguments);
        return SaResult.ok("保护成功").setData(safetyFile).setCode(SUCCESS);
    }

    /**
     * 批量删除文件（文件id列表）
     */
    @DeleteMapping("/file/batch-remove")
    public SaResult removeRows(@RequestBody List<String> idList) {
        boolean result = apiService.removeRowsByIds(idList);
        if (result) {
            return SaResult.ok("删除成功").setCode(MOVE_PERM);
        }
        return SaResult.error("数据不存在").setCode(ERROR);
    }

    /**
     * 根据日期统计注册人数
     */
    @GetMapping("/count-register-num/{day}")
    public SaResult countRegisterNum(@PathVariable String day) {
        log.info("Start to count the register number of the day");
        Integer num = apiService.countRegisterNum(day);
        return SaResult.ok("获取成功").setData(num).setCode(SUCCESS);
    }

    /**
     * 获取当前登录人数
     */
    @GetMapping("/count-current-login")
    public SaResult countCurrentLoginNum() {
        Integer num = apiService.countLoginNum();
        if (!Objects.isNull(num)) {
            return SaResult.ok("获取成功").setData(num).setCode(SUCCESS);
        }
        return SaResult.ok("获取失败").setCode(ERROR);
    }

    /**
     * 根据日期统计上传文件数
     */
    @GetMapping("/count-upload-num/{day}")
    public SaResult countUploadFileNum(@PathVariable String day) {
        log.info("Start to count the upload file number of the day");
        Integer num = apiService.countFileUploadNum(day);
        return SaResult.ok("获取成功").setData(num).setCode(SUCCESS);
    }

    /**
     * 根据日期统计保护文件数
     */
    @GetMapping("/count-protect-file/{day}")
    public SaResult countProtectFileNum(@PathVariable String day) {
        log.info("Start to count the protect file number of the day");
        Integer num = apiService.countFileProtectNum(day);
        return SaResult.ok("获取成功").setData(num).setCode(SUCCESS);
    }

    @GetMapping("/file/manage")
    public SaResult getFilePage(PageBo pageBo) {
        if (Objects.isNull(pageBo)) {
            log.info("PageBo is null");
            throw new CodeException("分页参数接收对象为空");
        }
        Page<PageVo> pageInfo = apiService.getFileInfo(pageBo);
        if (!Objects.isNull(pageInfo)) {
            return SaResult.ok("获取成功").setData(pageInfo).setCode(SUCCESS);
        }
        return SaResult.error("获取失败").setCode(ERROR);
    }
}
