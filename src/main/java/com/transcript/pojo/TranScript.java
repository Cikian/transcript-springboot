package com.transcript.pojo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Title: TranScript
 * @Author Cikian
 * @Package com.transcript.pojo
 * @Date 2024/7/17 上午1:34
 * @description: transcript: 笔录实体类
 */

@Data
public class TranScript {
    String title;
    List<QAData> content;
    String recorder;
    String time;
}
