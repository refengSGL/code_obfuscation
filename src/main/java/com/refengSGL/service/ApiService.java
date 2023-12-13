package com.refengSGL.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.refengSGL.entity.File;
import com.refengSGL.entity.request.PageBo;
import com.refengSGL.entity.response.FilePageDto;
import com.refengSGL.entity.response.FileVo;
import com.refengSGL.entity.response.PageVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface ApiService extends IService<File> {

    /**
     * 统计当前在线用户数
     */
    Integer countLoginNum();

    /**
     * 统计当日用户注册数
     */
    Integer countRegisterNum(String day);

    /**
     * 统计当日文件上传数
     */
    Integer countFileUploadNum(String day);

    /**
     * 统计当日保护文件数
     */
    Integer countFileProtectNum(String day);

    /**
     * 根据用户ID获取文件列表
     */
    List<FileVo> getFileInfoByUserId();

    /**
     * 根据文件id获取文件信息
     */
    FileVo getFileInfoByFileId(Long fileId);


    /**
     * 文件删除更换
     */
    FileVo uploadFile(MultipartFile file, String uploadPath) throws IOException;

    /**
     * 文件下载
     */
    void downloadFile(String fileName, String uploadUrl, HttpServletResponse response);

    /**
     * tigress
     */
    FileVo performProtect(String fileName, ArrayList<String> paramList);

    /**
     * 根据文件名获取文件信息
     */
    FileVo getFileInfoByFileName(String fileName);


    Page<FilePageDto> getPageVoInfo(PageBo pageBo);

    /**
     * 根据文件名删除文件
     */
    boolean deleteFileByFileName(String fileName);

    /**
     * 核心代码 #
     */
    FileVo executeShell(String fileName, int pType, List<String> arguments);

    /**
     * 删除文件（文件Id）
     */
    boolean deleteFileByFileId(Long fileId);

    /**
     * 批量删除文件（文件id列表）
     */
    boolean removeRowsByIds(List<String> idList);

    /**
     * 获取封装好的页面信息
     */
    Page<PageVo> getFileInfo(PageBo pageBo);
}
