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



2.4 server.properties参数详解
#broker 的全局唯一编号，不能重复
broker.id=0
#删除 topic 功能使能
delete.topic.enable=true
#处理网络请求的线程数量
num.network.threads=3
#用来处理磁盘 IO 的现成数量
num.io.threads=8
#发送套接字的缓冲区大小
socket.send.buffer.bytes=102400
#接收套接字的缓冲区大小
socket.receive.buffer.bytes=102400
#请求套接字的缓冲区大小
socket.request.max.bytes=104857600
#kafka 运行日志存放的路径
log.dirs=/opt/module/kafka/logs
#topic 在当前 broker 上的分区个数
num.partitions=1
#用来恢复和清理 data 下数据的线程数量
num.recovery.threads.per.data.dir=1
#segment 文件保留的最长时间，超时将被删除
log.retention.hours=168
#配置连接 Zookeeper 集群地址
zookeeper.connect=localhost:2181,localhost:2181,localhost:2181

2.6 kafka的相关命令
2.6.1 linux
查看当前服务器中的所有 topic
bin/kafka-topics.sh --zookeeper hadoop102:2181 --list
创建 topic
bin/kafka-topics.sh --zookeeper hadoop102:2181 --create --replication-factor 3 --partitions 1 --topic first
选项说明： --topic 定义 topic 名 --replication-factor 定义副本数 --partitions 定义分区数



删除 topic
bin/kafka-topics.sh --zookeeper hadoop102:2181 --delete --topic first
需要 server.properties 中设置 delete.topic.enable=true 否则只是标记删除。



发送消息
 bin/kafka-console-producer.sh --brokerlist hadoop102:9092 --topic first
消费消息
 bin/kafka-console-consumer.sh --zookeeper hadoop102:2181 --topic first
查看某个 Topic 的详情
bin/kafka-topics.sh --zookeeper hadoop102:2181 --describe --topic first
修改分区数
bin/kafka-topics.sh --zookeeper hadoop102:2181 --alter --topic first --partitions 6
2.6.2 windows
查看当前服务器中的所有 topic
bin/windows/kafka-topics.bat --zookeeper localhost:2181 --list
创建 topic
bin/windows/kafka-topics.bat --zookeeper localhost:2181 --create --replication-factor 1 --partitions 1 --topic first
选项说明： --topic 定义 topic 名 --replication-factor 定义副本数 --partitions 定义分区数



删除 topic
bin/windows/kafka-topics.bat --zookeeper localhost:2181 --delete --topic first
需要 server.properties 中设置 delete.topic.enable=true 否则只是标记删除。



发送消息
bin/windows/kafka-console-producer.bat --broker-list localhost:9092 --topic first
消费消息， --from-beginning ，从头展示消息
bin/windows/kafka-console-consumer.bat --zookeeper localhost:2181 --topic first   #旧版本
bin/windows/kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic first #新版本

查看某个 Topic 的详情
bin/windows/kafka-topics.bat --zookeeper localhost:2181 --describe --topic first
修改分区数
bin/windows/kafka-topics.bat --zookeeper localhost:2181 --alter --topic first --partitions 6

