package com.kaoyu.laiagent.tools;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 终端操作工具
 */
@Slf4j
public class TerminalOperationTool {

    private final File workingDir = new File(System.getProperty("user.dir"));

    /**
     * 执行终端命令工具
     */
    @Tool(description = "execute a terminal command")
    public String executeCommand(@ToolParam(description = "The terminal command to execute") String command) {
        StringBuilder output =new StringBuilder();
        String[] cmdArray = getStrings(command);
        try {
            Process process = RuntimeUtil.exec(null, workingDir, cmdArray);
            boolean finish = process.waitFor(5, TimeUnit.SECONDS);
            if (!finish){
                // 超时强制终止进程
                log.error("Command execution timeout: {}", command);
                process.destroyForcibly();
                return String.format("Command execution timeout: %s", command);
            }
            int result = process.exitValue();
            if (result == 0){
                log.info("Command executed successfully: {}", command);
                return RuntimeUtil.getResult(process);
            } else {
                log.error("Command execution failed: {}", command);
                return String.format("Command execution failed: %s, exit code: %d", command, result);
            }
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }

    /**
     * 判断电脑系统
     *
     */
    private static String[] getStrings(String command) {
        // 根据操作系统包装命令，以支持内置 shell 命令
        String os = System.getProperty("os.name").toLowerCase();
        String[] cmdArray;
        if (os.contains("win")) {
            // Windows: 使用 cmd /c 包装，支持 dir、cd 等内置命令
            cmdArray = new String[]{"cmd", "/c", command};
        } else {
            // Linux/Mac: 使用 sh -c 包装
            cmdArray = new String[]{"/bin/sh", "-c", command};
        }
        return cmdArray;
    }


}