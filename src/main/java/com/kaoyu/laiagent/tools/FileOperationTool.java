package com.kaoyu.laiagent.tools;


import cn.hutool.core.io.FileUtil;
import com.kaoyu.laiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

/**
 * 文件操作
 */
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + File.separator + "file";


    /**
     * 文件读取工具
     */
    @Tool(description = "read content from a file")
    public String readFile(@ToolParam(description = "Name of the file to read") String fileName) {
        String filePath = FILE_DIR + File.separator + fileName;

        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error read file " + e.getMessage();
        }
    }


    /**
     * 文件写入工具
     */
    @Tool(description = "write content from a file")
    public String writeFile(@ToolParam(description = "Name of the file to write") String fileName,
                            @ToolParam(description = "content to write to the file") String content) {
        String filePath = FILE_DIR + File.separator + fileName;
        try {
            //创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "file write success to " + filePath;
        } catch (Exception e) {
            return "Error write file " + e.getMessage();
        }
    }


}
