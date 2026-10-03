package com.kaoyu.laiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.kaoyu.laiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

public class ResourceDownloadTool {


    @Tool(description = "download a resource file from a URL")
    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url,
                                   @ToolParam(description = "Name of the downloaded file") String fileName) {
        String fileDir = FileConstant.FILE_SAVE_DIR + File.separator + "download";
        String filePath = fileDir + File.separator + fileName;
        try {
            //创建文件目录
            FileUtil.mkdir(fileDir);
            HttpUtil.downloadFile(url, new File(filePath));
            return "resource download successfully to " + filePath;
        } catch (Exception e) {
            return "Error download resource " + e.getMessage();
        }

    }


}