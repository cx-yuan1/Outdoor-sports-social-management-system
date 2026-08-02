package com.outdoor.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.outdoor.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 公共接口控制器
 */
@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.url-prefix}")
    private String urlPrefix;

    /**
     * 文件上传
     * @param file 文件
     * @param type 文件类型：category(分类图标)、banner(轮播图)、activity(活动图片)、avatar(用户头像)
     * @return 文件URL
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "type", defaultValue = "common") String type) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        // 获取文件后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        
        // 检查文件类型
        String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        boolean allowed = false;
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(suffix)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            return Result.error("不支持的文件类型");
        }

        // 验证type参数，只允许指定的类型
        String[] validTypes = {"category", "banner", "activity", "avatar", "common"};
        boolean validType = false;
        for (String validTypeStr : validTypes) {
            if (validTypeStr.equals(type)) {
                validType = true;
                break;
            }
        }
        if (!validType) {
            type = "common"; // 默认使用common
        }

        // 生成文件名
        String fileName = IdUtil.simpleUUID() + "." + suffix;
        
        // 根据类型构建相对路径：type/yyyy/MM/dd/filename
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = type + "/" + datePath + "/" + fileName;
        
        try {
            // 统一使用项目根目录 + 配置的 uploadPath（指向 src/main/resources/static/images/）
            String projectPath = System.getProperty("user.dir");
            String imagesDirPath = projectPath + "/" + uploadPath;

            // 构建完整路径：imagesDirPath/type/yyyy/MM/dd/filename
            String fullPath = imagesDirPath + "/" + relativePath;

            // 创建目录
            File destFile = new File(fullPath);
            FileUtil.mkParentDirs(destFile);

            // 保存文件
            file.transferTo(destFile);

            // 返回访问URL：/static/images/type/yyyy/MM/dd/filename
            String url = urlPrefix + relativePath;
            return Result.success(url);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}
