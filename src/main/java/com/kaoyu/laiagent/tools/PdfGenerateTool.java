package com.kaoyu.laiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.kaoyu.laiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;
import java.nio.file.Paths;

/**
 * PDF 创建编写工具
 */
public class PdfGenerateTool {


    @Tool(description = "generate a PDF file with given content",returnDirect = false)
    public String generatePdf(@ToolParam(description = "Name of the PDF file") String fileName,
                              @ToolParam(description = "Content to write into the PDF") String content) {
        String fileDir = FileConstant.FILE_SAVE_DIR + File.separator + "pdf";
        String filePath = fileDir + File.separator + fileName;
        FileUtil.mkdir(fileDir);
        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {
            //使用微软雅黑
            String fontPath = Paths.get("src/main/java/com/kaoyu/laiagent/tools/fonts/msyh.ttc")
                    .toAbsolutePath().toString()+",0";
            PdfFont font = PdfFontFactory.createFont(fontPath,
                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            //创建段落
            Paragraph paragraph = new Paragraph(content);
            document.add(paragraph);
            return "PDF generate successfully to " + filePath;
        } catch (Exception e) {
            return "Error generate PDF" + e.getMessage();

        }


    }


}