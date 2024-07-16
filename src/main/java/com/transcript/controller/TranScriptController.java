package com.transcript.controller;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import com.deepoove.poi.util.PoitlIOUtils;
import com.transcript.pojo.QAData;
import com.transcript.pojo.TranScript;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Title: TranScriptController
 * @Author Cikian
 * @Package com.transcript.controller
 * @Date 2024/7/17 上午1:35
 * @description: transcript: controller
 */

@RestController
public class TranScriptController {
    @PostMapping("/ts")
    public HttpServletResponse poitl(@RequestBody TranScript tranScript, HttpServletResponse rep) {
        Map<String, Object> datas = buildContent(tranScript);

        ApplicationHome h = new ApplicationHome(getClass());
        File jarF = h.getSource();
        String dirPath = jarF.getParentFile().toString() + "/templete/";

        File templeteFile = new File(dirPath+ "/templete.docx");
        String filename = "";
        try (
                ServletOutputStream out = rep.getOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(out);
                XWPFTemplate template = XWPFTemplate.compile(templeteFile).render(datas);
        ) {
            filename = tranScript.getTitle() + ".docx";
            rep.setContentType("application/octet-stream");
            rep.setHeader("Access-Control-Expose-Headers","filename");
            rep.setHeader("filename", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 输出word文件流,输出后关闭流
            template.write(bos);
            bos.flush();
            out.flush();
            PoitlIOUtils.closeQuietlyMulti(template, bos, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rep;
    }

    public Map<String, Object> buildContent(TranScript tranScript) {
        Map<String, Object> datas = new HashMap<>();
        ArrayList<Map<String, Object>> produces = new ArrayList<>();

        Style titleStyle = Style.builder()
                .buildColor("000000")
                .buildFontFamily("黑体")
                .buildFontSize(20)
                .build();
        Style questionStyle = Style.builder()
                .buildBold()
                .buildColor("000000")
                .buildFontFamily("微软雅黑")
                .buildFontSize(12)
                .build();
        Style answerStyle = Style.builder()
                .buildItalic()
                .buildColor("000000")
                .buildFontFamily("宋体")
                .buildFontSize(12)
                .build();
        Style timeStyle = Style.builder()
                .buildItalic()
                .buildColor("7f7f7f")
                .buildFontFamily("等线")
                .buildFontSize(10)
                .build();

        List<QAData> content = tranScript.getContent();

        for (QAData qa : content) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("question", new TextRenderData("问：" + qa.getQuestion(), questionStyle));
            dataMap.put("answer", new TextRenderData("答：" + qa.getAnswer(), answerStyle));
            produces.add(dataMap);
        }

        datas.put("title", new TextRenderData(tranScript.getTitle(), titleStyle));
        datas.put("produces", produces);
        datas.put("recorder", tranScript.getRecorder());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(new Date());
        datas.put("time", new TextRenderData(time, timeStyle));

        return datas;
    }
}
