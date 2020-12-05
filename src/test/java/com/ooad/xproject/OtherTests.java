package com.ooad.xproject;

import com.alibaba.fastjson.JSON;
import com.ooad.xproject.bo.TopicBO;
import org.junit.Test;

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
}
