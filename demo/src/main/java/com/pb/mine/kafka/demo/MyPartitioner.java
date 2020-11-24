package com.pb.mine.kafka.demo;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**自定义分区器
 * @Title:
 * @Description:
 * @author pengbin1 <pengbin>
 * @date 2020/11/24 16:28
 * @param
 * @return
 * @throws
 */
public class MyPartitioner implements Partitioner {
    @Override
    public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        // 根据某个topic主题获取分区数
        Integer partitionerNum = cluster.partitionCountForTopic(s);
        // 如果o是对象记得重写toString 和 hashCode方法
        return o.toString().hashCode() % partitionerNum;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
