package com.pb.mine.kafka.demo;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class MyProduce {


    /** kafka发送消息demo，生产者
     * @Title:
     * @Description:
     * @author pengbin1 <pengbin>
     * @date 2020/11/23 20:55
     * @param
     * @return
     * @throws
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        //kafka 集群，broker-list，ip配置在/etc/hosts 里配置下
        // 192.168.132.128 win7xuniji.localdomain win7xuniji
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "win7xuniji:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        //重试次数
        props.put(ProducerConfig.RETRIES_CONFIG, 1);
        //批次大小
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 2);
        //等待时间
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        //RecordAccumulator 缓冲区大小
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        // key和value的序列号方式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        // 自定义的分区规则器
        // props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,"com.pb.mine.kafka.demo.MyPartitioner");

        Producer<String, String> producer = new
                KafkaProducer<>(props);

        //回调函数，该方法会在 Producer 收到 ack 时调用，为异步调用.
        //如果指定分区，就按照分区来，否则根据key % 分区数 。
        producer.send(new ProducerRecord<String, String>("first", "pb01", "1"),(recordMetadata, e) -> {
            System.out.println("消息发送成功:" + recordMetadata.partition() + "," + recordMetadata.offset());
        }).get(); // 如果有 get() 就是同步发送
        producer.close();
    }

}
