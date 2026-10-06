package com.kaoyu.laiagent.tools;


import org.springframework.ai.tool.annotation.Tool;

public class TerminateTool {

    @Tool(description = """
            Terminate the current interaction when the user's request has been fulfilled, or when the assistant is unable to proceed further with the task.
            Call this tool to end the work once all tasks are completed.
            """)
    public String doTerminate(){
        return "任务结束";
    }


}