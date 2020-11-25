package com.pb.mine.kafka.demo.Interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * @Title: 拦截器
 * @Description:
 * @author pengbin1 <pengbin>
 * @date 2020/11/25 15:27
 * @param
 * @return
 * @throws
 */
public class TimeInterceptor implements ProducerInterceptor {

    private int errorCounter = 0;
    private int successCounter = 0;


    @Override
    public void configure(Map<String, ?> configs) {

    }

    /**
     * 发送前修改value的数据
     * @param record
     * @return
     */
    @Override
    public ProducerRecord onSend(ProducerRecord record) {
        // 创建一个新的 record，把时间戳写入消息体的最前部（修改value的值）
        return new ProducerRecord(record.topic(), record.partition(), record.timestamp(), record.key(),
                System.currentTimeMillis() + "," + record.value().toString());
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        // 统计成功和失败的次数，在每次ack之后处理
        if (exception == null) {
            successCounter++;
        } else {
            errorCounter++;
        }

    }

    @Override
    public void close() {
        // 保存结果
        System.out.println("Successful sent: " + successCounter);
        System.out.println("Failed sent: " + errorCounter);
    }


}
