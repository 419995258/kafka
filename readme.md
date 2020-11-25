<h1>Kafka由浅入深</h1>

demo:kafka的实例demo

2.3 windows下使用kafka
下载地址：https://mirror.bit.edu.cn/apache/kafka/2.6.0/kafka_2.13-2.6.0.tgz

下载完成后，里面是自带zookeeper的。

启动脚本：

bin/windows/zookeeper-server-start.bat config/zookeeper.properties

bin/windows/kafka-server-start.bat config/server.properties

bin/windows/kafka-topics.bat --zookeeper localhost:2181 --create --replication-factor 1 --partitions 1 --topic first
bin/windows/kafka-console-producer.bat --broker-list localhost:9092 --topic first

bin/windows/kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic first



# demo
案例
## demo.Interceptor
拦截器