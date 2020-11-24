package com.pb.mine.kafka.demo;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * 自定义的offset
 */
public class MyConsumerCustomizeOffset {


    public static void main(String[] args) {
        Properties props = new Properties();
        //kafka 集群，broker-list
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "win7xuniji:9092");
        // 自动提交
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // 自动提交延迟
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        // key和value反序列号
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        // 重置消费者的offset，只有在切换消费组或者在过期之后（默认7天）才会被重新拉取进行消费
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");


        // 用户分组
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        //消费者订阅主题
        consumer.subscribe(Arrays.asList("first"), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                //该方法会在 Rebalance 之前调用,比如存储在mysql
                commitOffset(currentOffset);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                //该方法会在 Rebalance 之后调用
                currentOffset.clear();
                for (TopicPartition partition : partitions) {
                    consumer.seek(partition, getOffset(partition));//定位到最近提交的 offset 位置继续消费
                }

            }
        });
        while (true) {
            //消费者拉取数据
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.offset() +";"+ record.key() +";"+  record.value());
            }
                // 手动同步提交，当前线程会阻塞直到 offset 提交成功
//            consumer.commitSync();

            // 异步提交
            consumer.commitAsync((offsets, exception) -> {
                System.out.println("offsets:" + offsets);
                if (exception != null) {
                    System.err.println("Commit failed for" + offsets);
                }
            });
        }

    }

    private static Map<TopicPartition, Long> currentOffset = new HashMap<>();


    //获取某分区的最新 offset
    private static long getOffset(TopicPartition partition) {
        // 从mysql获取
        return 0;
    }
    //提交该消费者所有分区的 offset
    private static void commitOffset(Map<TopicPartition, Long> currentOffset) {
        // 存储到mysql
    }
}
