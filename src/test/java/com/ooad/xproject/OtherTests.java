package com.ooad.xproject;

import com.alibaba.fastjson.JSON;
import com.ooad.xproject.bo.ProjSettingsBO;
import com.ooad.xproject.bo.TopicBO;
import org.junit.Test;

import java.sql.Timestamp;

public class OtherTests {
    @Test
    public void testTopic() {
        TopicBO topic = TopicBO.builder().topicName("Topic123").maxTeamSize(-1).build();
        System.out.println(topic);
        String str = JSON.toJSONString(topic);
        System.out.println(str);
        TopicBO topic2 = JSON.parseObject(str, TopicBO.class);
        System.out.println(topic2);
    }

    @Test
    public void testProjSettingsBO() {
        ProjSettingsBO ps = new ProjSettingsBO(true, 3, 4, Timestamp.valueOf("2020-12-31 23:59:59"), true);
        System.out.println(ps);
        String str = JSON.toJSONString(ps);
        System.out.println(str);
        ProjSettingsBO ps2 = JSON.parseObject(str, ProjSettingsBO.class);
        System.out.println(ps2);
    }
}
