package com.refengSGL.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.refengSGL.entity.File;
import com.refengSGL.entity.User;
import com.refengSGL.entity.request.PageBo;
import com.refengSGL.entity.response.FilePageDto;
import com.refengSGL.entity.response.FileVo;
import com.refengSGL.entity.response.PageVo;
import com.refengSGL.exception.CodeException;
import com.refengSGL.mapper.FileMapper;
import com.refengSGL.mapper.UserMapper;
import com.refengSGL.service.ApiService;
import com.refengSGL.utils.ExceptionUtils;
import com.refengSGL.utils.ProtectiveAction;
import com.refengSGL.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.refengSGL.constant.FileConstant.*;
import static com.refengSGL.constant.UserConstant.IS_LOGIN;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiServiceImpl extends ServiceImpl<FileMapper, File> implements ApiService {

    private final UserMapper userMapper;
    /**
     * 获取当前在线人数
     */
    @Override
    public Integer countLoginNum() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsLogin, IS_LOGIN);
        return userMapper.selectCount(queryWrapper);
    }

    /**
     * 统计当日用户注册数
     */
    @Override
    public Integer countRegisterNum(String day) {
        return baseMapper.selectRegisterNumByDay(day);
    }

    /**
     * 统计当日文件上传数
     */
    @Override
    public Integer countFileUploadNum(String day) {
        return baseMapper.selectUploadFileNumByDay(day);
    }

    /**
     * 统计当日文件保护数
     */
    @Override
    public Integer countFileProtectNum(String day) {
        return baseMapper.selectProtectFileNumByDay(day);
    }

    /**
     * 查找文件（用户id）
     */
    @Override
    public List<FileVo> getFileInfoByUserId() {
        log.info("Getting a list of all files for the current user");
        // 根据用户id查询文件列表
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getUserId, TokenUtils.getCurrentUser()).orderByDesc(File::getCreateTime);
        List<File> originalFileList = baseMapper.selectList(queryWrapper);
        if (!Objects.isNull(originalFileList)) {
            // 数据脱敏
            List<FileVo> safetyFileList = new ArrayList<>();
            log.info("Data desensitization is underway");
            for (File file : originalFileList) {
                FileVo safetyFile = getSafetyFile(file);
                safetyFileList.add(safetyFile);
            }
            return safetyFileList;
        }
        throw new CodeException("当前用户文件列表为空");
    }

    /**
     * 查找文件（文件id）
     */
    @Override
    public FileVo getFileInfoByFileId(Long fileId) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getId, fileId);
        Integer isExistFile = baseMapper.selectCount(queryWrapper);
        if (isExistFile < FILE_EXIST) {
            log.info("The target file does not exist");
            throw new CodeException("目标文件不存在");
        }
        File originalFile = baseMapper.selectOne(queryWrapper);
        // 数据脱敏
        return getSafetyFile(originalFile);
    }

    /**
     * 上传文件
     */
    @Override
    public FileVo uploadFile(MultipartFile file, String uploadPath) {
        if (Objects.isNull(file)) {
            throw new CodeException("未找到上传文件");
        }
        // 获取上传路径的目录对象
        java.io.File curFile = new java.io .File(uploadPath);
        if (!curFile.exists()) {
            // 目录对象不存在，创建目录结构
            boolean mkdirResult = curFile.mkdir();
            if (!mkdirResult) {
                log.info("Failed to create the directory structure");
                throw new CodeException("创建保存目录结构失败");
            }
            log.info("The directory structure is created successfully");
        }
        // 获取原始文件的后缀名
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isNotEmpty(originalFilename)) {
            log.info("Original file name exists");
            // 动态获取原始文件的后缀名
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 利用UUID随机生成文件名，防止文件名称重复，造成文件覆盖
            String fileName = UUID.randomUUID() + suffix;
            java.io.File dest = new java.io.File(uploadPath + fileName);
            // 文件转存
            try {
                log.info("The file is being transferred");
                file.transferTo(dest);

                // 封装返回类型
                File target = new File();
                // 设置文件名（包含后缀名）
                target.setName(fileName);
                // 设置文件类型（取出后缀名）
                target.setType(suffix.substring(1));
                // 设置文件大小（以字节为单位）
                target.setMemory(String.valueOf(file.getSize()));
                // 设置上传文件的用户ID
                target.setUserId(TokenUtils.getCurrentUser().getId());
                // 设置文件在服务器上存储的路径
                target.setStore(uploadPath + fileName);
                // 设置文件原始文件名
                target.setOriginalFileName(originalFilename);
                // 将上传文件信息存入数据库中
                baseMapper.insert(target);

                return getSafetyFile(target);
            } catch (IOException e) {
                log.error(ExceptionUtils.getMessage(e));
                throw new CodeException("文件存储失败");
            }
        }
        log.info("Original file name does not exists");
        throw new CodeException("文件名为空");
    }

    /**
     * 调用开源工具进行扫描和分析：这里使用Clang Static Analyzer
     */
    private boolean runAnalysis(String filePath) {
        ProcessBuilder pb = new ProcessBuilder(SCAN_BUILD, CLANG, "-c", filePath);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            // 获取输出结果文件
            String scanResultFilePath = readOutput(p);
            if (scanResultFilePath.contains(OUTPUT_CONTAIN_WARNING)) {
                log.info("The output contains {}", scanResultFilePath.toString());
                return false;
            }
        } catch (IOException e) {
            throw new CodeException("文件读写异常");
        }
        return true;
    }

    /**
     * 读取Clang Static Analyzer的输出
     */
    private String readOutput(Process p) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = p.getInputStream().read()) != END_OF_FILE) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    /**
     * 文件信息脱敏
     */
    private FileVo getSafetyFile(File originalFile) {
        FileVo safetyFile = new FileVo();
        safetyFile.setName(originalFile.getName());
        safetyFile.setType(originalFile.getType());
        safetyFile.setMemory(originalFile.getMemory());
        safetyFile.setOriginalFileName(originalFile.getOriginalFileName());
        safetyFile.setCreateTime(originalFile.getUpdateTime());
        safetyFile.setOriginalFileId(originalFile.getOriginalFile());
        return safetyFile;
    }

    /**
     * 下载文件
     */
    @Override
    public void downloadFile(String fileName, String uploadUrl, HttpServletResponse response) {
        String storeUrl = uploadUrl + fileName;
        // 获取文件对象
        java.io.File file = new java.io.File(storeUrl);
        if (!file.exists()) {
            log.info("The target file does not exists");
            throw new CodeException("目标文件不存在");
        }
        // 清空response
        response.reset();
        // 设置response的Header
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Content-Length", "" + file.length());
        ServletOutputStream outputStream;
        byte[] array;
        try {
            outputStream = response.getOutputStream();
            array = FileUtils.readFileToByteArray(file);
            outputStream.write(array);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new CodeException("文件下载失败");
        }
    }

    /**
     * tigress 保护方式
     */
    @Override
    public FileVo performProtect(String fileName,ArrayList<String> paramList){
        // 获取 tigress 类型
        String protectionWord = paramList.get(0);
        ProtectiveAction.performShellScript(fileName,paramList);

        // 文件名 :  fileName + protectionType
        String protectionType = ProtectiveAction.getProtectType(Integer.parseInt(protectionWord));
        String protectFileName = fileName + protectionType;

        // store: uploadPath + protectedFileName
        String storePath = FILE_UPLOAD_PATH + protectFileName;
        // 获取文件保护后的后缀
        String FileSuffix = protectFileName.substring(protectFileName.lastIndexOf("."));
        // 生成新的文件名
        String FileName = UUID.randomUUID() + FileSuffix;
        String lastFileName = FileName.replace(C_SUFFIX,EMPTY_STRING);
        // memory
        String fileMemory = ProtectiveAction.getFileMemory(protectFileName);

        // 项目中 有名为 File 的类 防止混淆
        java.io.File resultFile = ProtectiveAction.getResultFileObject(protectFileName,lastFileName);
        if (Objects.isNull(resultFile)){
            log.info("修改保护后文件名失败");
            throw new CodeException("修改保护后文件名失败");
        }

        Integer originalFileId = getFileIdByFileName(fileName);
        if (Objects.isNull(originalFileId)){
            log.info("原始文件不存在");
            throw new CodeException("原始文件不存在");
        }

        String originalFileNameByFileId = getOriginalFileNameByFileId(originalFileId);
        File resultStoreFile = new File();
        resultStoreFile.setName(lastFileName);
        resultStoreFile.setType(TIGRESS_ELF);
        resultStoreFile.setMemory(fileMemory);
        resultStoreFile.setUserId(TokenUtils.getCurrentUser().getId());
        resultStoreFile.setStore(storePath);
        resultStoreFile.setOriginalFileName(originalFileNameByFileId);
        resultStoreFile.setOriginalFile(originalFileId);

        baseMapper.insert(resultStoreFile);

        return getSafetyFile(resultStoreFile);
    }

    /**
     * 获取原文件名（文件id）
     */
    private String getOriginalFileNameByFileId(Integer fileId) {
        File originalFile = getFileByIdOrName(fileId.toString(), GET_BY_ID);
        return originalFile.getOriginalFileName();
    }

    /**
     * 获取文件信息（文件名）
     */
    @Override
    public FileVo getFileInfoByFileName(String fileName) {
        File originalFile = getFileByIdOrName(fileName, GET_BY_NAME);
        return getSafetyFile(originalFile);
    }

    /**
     * 获取文件（文件名或者文件id）
     */
    private File getFileByIdOrName(String identifier, String identifierType) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        if (GET_BY_ID.equals(identifierType)) {
            queryWrapper.eq(File::getId, identifier);
        } else if (GET_BY_NAME.equals(identifierType)) {
            queryWrapper.eq(File::getName, identifier);
        }
        Integer selectCount = baseMapper.selectCount(queryWrapper);
        if (selectCount < FILE_EXIST) {
            log.info("Target file does not exist");
            throw new CodeException("文件不存在");
        }
        File file = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(file)) {
            log.info("Target file does not exist");
            throw new CodeException("文件不存在");
        }
        return file;
    }

    /**
     * 文件分页查询
     */
    @Override
    public Page<PageVo> getFileInfo(PageBo pageBo) {
        Integer page = pageBo.getPage();
        Integer limit = pageBo.getLimit();
        if (Objects.isNull(page) || Objects.isNull(limit)) {
            log.info("分页查询两个必要参数为空");
            throw new CodeException("分页查询参数缺失");
        }
        String fileName = pageBo.getFileName();
        Page<File> pageInfo = new Page<>(page, limit);

        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(File::getCreateTime);
        Integer userId = TokenUtils.getCurrentUser().getId();
        log.info("userId:{}",userId);
        // 如果未传入 fileName 则 根据userId 查询
        queryWrapper.eq(File::getUserId,userId);

        if (StringUtils.isNotBlank(fileName)) {
            // 传入 fileName 则 根据 id 和 fileName 同时查询
            queryWrapper.eq(File::getUserId,userId)
                    .likeRight(File::getOriginalFileName, fileName);
        }
        baseMapper.selectPage(pageInfo, queryWrapper);

        List<File> records = pageInfo.getRecords();
        log.info("records:{}",records);
        List<PageVo> filePageDto = records
                .stream()
                .map(this::getSafetyPage)
                .collect(Collectors.toList());
        Page<PageVo> resPageInfo = new Page<>(page, limit, pageInfo.getTotal());
        resPageInfo.setRecords(filePageDto);
        return resPageInfo;
    }

    private FilePageDto getTargetFilePage(File file) {
        FilePageDto filePageDto = new FilePageDto();
        filePageDto.setId(file.getId());
        filePageDto.setFileName(file.getOriginalFileName());
        filePageDto.setFileSize(file.getMemory());
        filePageDto.setFileLocation(file.getStore());
        filePageDto.setType(file.getType());
        filePageDto.setCreateTime(file.getCreateTime());
        return filePageDto;
    }

    /**
     * 分页查询
     */
    @Override
    public Page<FilePageDto> getPageVoInfo(PageBo pageBo) {
        Integer page = pageBo.getPage();
        Integer limit = pageBo.getLimit();
        String fileName = pageBo.getFileName();
        Page<File> pageInfo = new Page<>(page, limit);

        // 筛选出当前用户的保护后的文件
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(File::getUpdateTime);
        if (StringUtils.isNotBlank(fileName)) {
            queryWrapper.likeRight(File::getOriginalFileName, fileName);
        }
        baseMapper.selectPage(pageInfo, queryWrapper);

        // 封装页面数据
        List<File> pageInfoRecords = pageInfo.getRecords();
        List<FilePageDto> safetyPageList = pageInfoRecords
                .stream()
                .map(this::getTargetFilePage)
                .collect(Collectors.toList());
        Page<FilePageDto> resultPageInfo = new Page<>(page, limit, pageInfo.getTotal());
        resultPageInfo.setRecords(safetyPageList);
        return resultPageInfo;
    }


    /**
     * 数据脱敏（页面数据）
     */
    private PageVo getSafetyPage(File originalFile) {
        PageVo safetyPage = new PageVo();
        Integer originalFileId = originalFile.getOriginalFile();
        log.info("originalFileId:{}",originalFileId);
        File file = baseMapper.selectById(originalFileId);
        safetyPage.setQueryFileId(originalFile.getId());
        safetyPage.setOriginalFileName(originalFile.getOriginalFileName());
        if (Objects.isNull(file)) {
            log.info("Original file does not exist");
            safetyPage.setOriginalFileName("文件尚未混淆");
        }
        safetyPage.setResultFileName(originalFile.getName());
        safetyPage.setUpdateTime(originalFile.getCreateTime());
        return safetyPage;
    }

    /**
     * 删除文件（文件名）
     */
    @Override
    public boolean deleteFileByFileName(String fileName) {
        Integer fileIdByFileName = getFileIdByFileName(fileName);
        if (Objects.isNull(fileIdByFileName)) {
            return false;
        }
        return deleteFileByFileId(Long.valueOf(fileIdByFileName));
    }

    /**
     * 【核心代码】执行脚本文件
     */

    @Override
    // 传入三个参数 文件名 保护类型 参数列表
    public FileVo executeShell(String fileName, int pType, List<String> arguments) {
        // 文件路径  filePath  = /home/project/upload/ + 文件名(hello.c)
        log.info("pType:{}",pType);
        String filePath = FILE_UPLOAD_PATH + fileName;
        // 检查文件是否存在
        log.info("待保护文件为：{}", filePath);
        // 创建一个 File 对象 参数为 文件地址
        java.io.File file = new java.io.File(filePath);
        // 调用 判断文件存在的方法 如果不存在 就抛出异常
        if (!file.exists()) {
            log.info("Target file does not exist");
            throw new CodeException("目标文件不存在");
        }
        // 根据pType选择脚本文件
        String script;String suffix;
        // 根据 方法传入的参数 pType 判断需要进行的保护类型
        switch (pType) {
            case 1:
                script = OURS_OBFUSCATE;
                int lastIndex = fileName.lastIndexOf('.');
                log.info("lastIndex:{}",lastIndex);
                // 将 文件地址 重新加载 改为 去除扩展名后的 新文件地址名
                String tFileName = fileName.substring(0, lastIndex);
                String oursType = arguments.get(OURS_STOP);
                int oType = Integer.parseInt(oursType);
                log.info("Specific method of obfuscation: {}", oType);
                switch (oType) {
                    case 1:
                        suffix = OURS_STOP_I;
                        break;
                    case 2:
                        suffix = OURS_STOP_II;
                        break;
                    case 3:
                        suffix = OURS_STOP_BOTH;
                        break;
                    default:
                        log.info("Invalid tigress obfuscate type");
                        throw new CodeException("无效的混淆类型");
                }
                // 后缀为_obf_I / _II / _both
                suffix = OBF + suffix;
                // 拼接调用的脚本命令: ./ours_stop.sh /home/project/upload/hello （（测试！！
                // 说明: ours的脚本文件中 已经包含 文件路径了，所以不需要再加路径了
                String[] oursCommand = {RUN_SHELL + script, tFileName};
                log.info("Init shell is : {}", Arrays.toString(oursCommand));
                // 添加 1 1 / 2 1 / 3 1 参数
                oursCommand = Stream.concat(Arrays.stream(oursCommand), arguments.stream()).toArray(String[]::new);
                log.info("After shell is : {}", Arrays.toString(oursCommand));
                // 调用脚本并等待执行完成
                ProcessBuilder pb = new ProcessBuilder(oursCommand);
                // 指定工作目录
                pb.directory(new java.io.File(WORK_DIRECTORY));
                // 将标准错误和标准输出合并
                pb.redirectErrorStream(true);
                Process process_ours = null;
                try {
                    process_ours = pb.start();
                    process_ours.waitFor();
                } catch (IOException | InterruptedException e) {
                    log.info(ExceptionUtils.getMessage(e));
                    throw new CodeException("保护失败");
                }
                // 现在 文件格式 hello.c
                int lastIndexOur = fileName.lastIndexOf(".");
                String temporaryFileName = fileName.substring(0,lastIndexOur);
                String lastFileName = temporaryFileName + suffix;
                log.info("After file name is :{}",lastFileName);
                // 下载路径 + lastFileName
                java.io.File targetAfterFile_ours = new java.io.File(FILE_UPLOAD_PATH+lastFileName);
                if (!targetAfterFile_ours.exists()){
                    log.info("Protected file does not exist");
                    throw new CodeException("保护失败");
                }
                String newFileNameByUUID = UUID.randomUUID().toString();
                String newFileName = newFileNameByUUID + suffix;
                String lastFilePath = FILE_UPLOAD_PATH + newFileName;
                java.io.File targetFile_ours = new java.io.File(lastFilePath);
                if (!targetAfterFile_ours.renameTo(targetFile_ours)){
                    log.info("Failed to rename protected file");
                    throw new CodeException("保护失败");
                }
                // 可以简写为一个封装函数
                File responseFile_ours = new File();
                String targetFileName_ours = targetFile_ours.getName();
                long fileLength = targetFile_ours.length();
                responseFile_ours.setName(targetFileName_ours);
                responseFile_ours.setMemory(String.valueOf(fileLength));
                responseFile_ours.setUserId(TokenUtils.getCurrentUser().getId());
                responseFile_ours.setType(OURS_AFTER_FILE_TYPE);
                responseFile_ours.setStore(lastFilePath);
                responseFile_ours.setOriginalFile(getFileIdByFileName(fileName));
                responseFile_ours.setOriginalFileName(getFileNameByFileId(getFileIdByFileName(fileName)));
                baseMapper.insert(responseFile_ours);
                return getSafetyFile(responseFile_ours);

            case 2:
                script = TIGRESS_OBFUSCATE;
                // 文件地址  filePath  = /home/project/upload/ + 文件名(hello.c)
                // 去除filePath末尾的扩展名 相当于 去除 “.c”
                // 返回去除 扩展名后的 文件长度
                int lastDotIndex = filePath.lastIndexOf('.');
                // 将 文件地址 重新加载 改为 去除扩展名后的 新文件地址名
                filePath = filePath.substring(0, lastDotIndex);
                log.info("arguments: {}", arguments);
                // 根据arguments的第二个参数获取具体的保护方式
                String tigressType = arguments.get(TIGRESS_TYPE);
                int tType = Integer.parseInt(tigressType);
                log.info("Specific method of obfuscation: {}", tType);
                switch (tType) {
                    case 1:
                        suffix = TIGRESS_FLATTEN;
                        break;
                    case 2:
                        suffix = TIGRESS_ENCODE_ARITHMETIC;
                        break;
                    case 3:
                        suffix = TIGRESS_ADD_OPAQUE;
                        break;
                    default:
                        log.info("Invalid tigress obfuscate type");
                        throw new CodeException("无效的加固类型");
                }
                // 拼接调用的脚本命令: ./tigress_obfuscate.sh /home/project/upload/hello
                String[] tigressCmd = {RUN_SHELL + script, filePath};
                log.info("Init shell is : {}", Arrays.toString(tigressCmd));
                // 添加 main 1 参数
                tigressCmd = Stream.concat(Arrays.stream(tigressCmd), arguments.stream()).toArray(String[]::new);
                log.info("After shell is : {}", Arrays.toString(tigressCmd));
                // 调用脚本并等待执行完成
                ProcessBuilder processBuilder = new ProcessBuilder(tigressCmd);
                // 指定工作目录
                processBuilder.directory(new java.io.File(WORK_DIRECTORY));
                // 将标准错误和标准输出合并
                processBuilder.redirectErrorStream(true);
                Process process = null;
                try {
                    process = processBuilder.start();
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    log.info(ExceptionUtils.getMessage(e));
                    throw new CodeException("保护失败");
                }
                // 获取生成的保护文件并返回文件对象
                // 去除fileName末尾的扩展名
                int lastIndexOf = fileName.lastIndexOf('.');
                String tmpFileName = fileName.substring(0, lastIndexOf);
                String afterFileName = tmpFileName + suffix + C_SUFFIX;// .c
                log.info("After file name is : {}", afterFileName);
                // 下载路径 + tmpFileName（截断后的文件名） + suffix + C_SUFFIX
                java.io.File targetAfterFile = new java.io.File(FILE_UPLOAD_PATH + afterFileName);
                if (!targetAfterFile.exists()) {
                    log.info("Protected file does not exist");
                    throw new CodeException("保护失败");
                }
                // 生成UUID作为新的文件名
                String uuidFileName = UUID.randomUUID().toString();
                // 将新的文件名和后缀名拼接成新保护文件名
                String newAfterFileName = uuidFileName + suffix + C_SUFFIX;
                // 将新保护文件名拼接到文件上传路径
                String afterFilePath = FILE_UPLOAD_PATH + newAfterFileName;
                java.io.File targetFile = new java.io.File(afterFilePath);
                if (!targetAfterFile.renameTo(targetFile)) {
                    log.info("Failed to rename protected file");
                    throw new CodeException("保护失败");
                }
                File responseFile = new File();
                String targetFileName = targetFile.getName();
                long length = targetFile.length();
                responseFile.setName(targetFileName);
                responseFile.setMemory(String.valueOf(length));
                responseFile.setUserId(TokenUtils.getCurrentUser().getId());
                responseFile.setType(TIGRESS_AFTER_FILE_TYPE);
                responseFile.setStore(afterFilePath);
                responseFile.setOriginalFile(getFileIdByFileName(fileName));
                responseFile.setOriginalFileName(getOriginalFileNameByFileId(getFileIdByFileName(fileName)));
                baseMapper.insert(responseFile);
                return getSafetyFile(responseFile);
        }
        return null;
    }

    /**
     * 删除文件（文件Id）
     */
    @Override
    public boolean deleteFileByFileId(Long fileId) {
        return deleteFileById(fileId);
    }

    @Override
    public boolean removeRowsByIds(List<String> idList) {
        if (Objects.isNull(idList)) {
            log.info("The file id list is empty");
            throw new CodeException("文件id列表为空");
        }
        int count = baseMapper.deleteBatchIds(idList);
        return count > 0;
    }


    /**
     * 获取文件id（文件名）
     */
    private Integer getFileIdByFileName(String fileName) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getName, fileName);
        File file = baseMapper.selectOne(queryWrapper);
        if (!Objects.isNull(file)) {
            return file.getId();
        }
        throw new CodeException("找不到该文件");
    }

    /**
     * 获取文件名
     */
    private String getFileNameByFileId(Integer fileId) {
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper.eq(File::getId, fileId);
        File file = baseMapper.selectOne(fileLambdaQueryWrapper);
        if (Objects.isNull(file)) {
            return "文件已删除";
        }
        return file.getName();
    }

    /**
     * 封装：删除文件（文件Id）
     */
    private boolean deleteFileById(Long fileId) {
        File file = baseMapper.selectById(fileId);
        if (Objects.isNull(file)) {
            log.info("Target file does not exist");
            throw new CodeException("目标文件不存在");
        }
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getId, fileId);
        int count = baseMapper.delete(wrapper);
        return count > 0;
    }
    
}
