<h1>Kafka由浅入深</h1>

1.kafka消息队列
1.1 定义
Kafka 是一个分布式的基于发布/订阅模式的消息队列（Message Queue），主要应用于大数据实时处理领域。

1.2 消息队列
1.2.1 使用场景
同步处理：



异步处理：



1.2.2 消息队列的优点
解耦
允许你独立的扩展或修改两边的处理过程，只要确保它们遵守同样的接口约束。

可恢复性
系统的一部分组件失效时，不会影响到整个系统。消息队列降低了进程间的耦合度，所以即使一个处理消息的进程挂掉，加入队列中的消息仍然可以在系统恢复后被处理。

缓冲
 有助于控制和优化数据流经过系统的速度，解决生产消息和消费消息的处理速度不一致的情况。

灵活性 & 峰值处理能力
在访问量剧增的情况下，应用仍然需要继续发挥作用，但是这样的突发流量并不常见。如果为以能处理这类峰值访问为标准来投入资源随时待命无疑是巨大的浪费。使用消息队列能够使关键组件顶住突发的访问压力，而不会因为突发的超负荷的请求而完全崩溃。

异步通信
很多时候，用户不想也不需要立即处理消息。消息队列提供了异步处理机制，允许用户把一个消息放入队列，但并不立即处理它。想向队列中放入多少消息就放多少，然后在需要的时候再去处理它们。



1.2.3 消息队列的几种模式
（1）点对点模式（一对一，消费者主动拉取数据，消息收到后消息清除）

消息生产者生产消息发送到Queue中，然后消息消费者从Queue中取出并且消费消息。

消息被消费以后，queue 中不再有存储，所以消息消费者不可能消费到已经被消费的消息。

Queue 支持存在多个消费者，但是对一个消息而言，只会有一个消费者可以消费。



（2）发布/订阅模式（一对多，消费者消费数据之后不会清除消息）

消息生产者（发布）将消息发布到 topic 中，同时有多个消息消费者（订阅）消费该消息。和点对点方式不同，发布到 topic 的消息会被所有订阅者消费。kafka是主动拉取，需要维护长轮询。

1.3 Kafka 基础架构


1）Producer ：消息生产者，就是向 kafka broker 发消息的客户端；

2）Consumer ：消息消费者，向 kafka broker 取消息的客户端；

3）Consumer Group （CG）：消费者组，由多个 consumer 组成。消费者组内每个消费者负责消费不同分区的数据，一个分区只能由一个组内消费者消费；消费者组之间互不影响。所有的消费者都属于某个消费者组，即消费者组是逻辑上的一个订阅者。

4）Broker ：一台 kafka 服务器就是一个 broker。一个集群由多个 broker 组成。一个 broker可以容纳多个 topic。

5）Topic ：可以理解为一个队列，生产者和消费者面向的都是一个 topic；

6）Partition：为了实现扩展性，一个非常大的 topic 可以分布到多个 broker（即服务器）上，一个 topic 可以分为多个 partition，每个 partition 是一个有序的队列；

7）Replica：副本，为保证集群中的某个节点发生故障时，该节点上的 partition 数据不丢失，且 kafka 仍然能够继续工作，kafka 提供了副本机制，一个 topic 的每个分区都有若干个副本，一个 leader 和若干个 follower。

8）leader：每个分区多个副本的“主”，生产者发送数据的对象，以及消费者消费数据的对象都是 leader。

9）follower：每个分区多个副本中的“从”，实时从 leader 中同步数据，保持和 leader 数据的同步。leader 发生故障时，某个 follower 会成为新的 follower。



2. 快速使用kafka
2.0 docker相关命令
sudo docker images         #查看安装的镜像
docker ps                  #查看运行的景象

2.0.5 禁用防火墙
CentOS7 禁用防火墙

systemctl stop firewalld.service
systemctl disable firewalld.service
systemctl status firewalld.service             #(查看关闭状态)

2.1 docker安装zookeeper
docker pull zookeeper        #安装zookeeper
docker run --privileged=true -d --name zookeeper --publish 2181:2181 -d zookeeper:latest    #启动zookeeper
docker exec -it zookeeper /bin/bash        #进入容器
 ./bin/zkCli.sh -server 192.168.132.130:2181                 #客户端连接服务器
2.1 docker安装wurstmeister/zookeeper
docker pull wurstmeister/zookeeper        #安装zookeeper
docker run -d --name zookeeper -p 2181:2181 -t wurstmeister/zookeeper  #启动zookeeper
docker exec -it zookeeper /bin/bash
2.2 docker安装kafka
仓库：https://hub.docker.com/r/wurstmeister/kafka

docker pull wurstmeister/kafka        #docker下载kafka


#启动kafka 1
docker run  -d --name kafka1 \
    -p 9092:9092 \
    -e KAFKA_BROKER_ID=0 \
    -e KAFKA_ZOOKEEPER_CONNECT=192.168.132.130:2181 \
    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.132.130:9092 \
    -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -t wurstmeister/kafka 

#启动kafka 2
docker run  -d --name kafka2 \
    -p 9093:9093 \
    -e KAFKA_BROKER_ID=1 \
    -e KAFKA_ZOOKEEPER_CONNECT=192.168.132.130:2181 \
    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.132.130:9092 \
    -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9093 -t wurstmeister/kafka 

#启动kafka 3
docker run  -d --name kafka3 \
    -p 9094:9094 \
    -e KAFKA_BROKER_ID=1 \
    -e KAFKA_ZOOKEEPER_CONNECT=192.168.132.130:2181 \
    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.132.130:9092 \
    -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9094 -t wurstmeister/kafka 


搭建Kafka集群
    docker run -d --name kafka1 \
    -p 9093:9093 \
    -e KAFKA_BROKER_ID=1 \
    -e KAFKA_ZOOKEEPER_CONNECT=<宿主机IP>:2181 \
    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://<宿主机IP>:9093 \
    -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9093 -t wurstmeister/kafka

2.3 windows下使用kafka
下载地址：https://mirror.bit.edu.cn/apache/kafka/2.6.0/kafka_2.13-2.6.0.tgz

下载完成后，里面是自带zookeeper的。

启动脚本：

bin/windows/zookeeper-server-start.bat config/zookeeper.properties
bin/windows/kafka-server-start.bat config/server.properties
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
2.7 整体流程
启动zookeeper和kafka，kafka发送消息和消费消息



3 深入kafka
3.1 Kafka 工作流程及文件存储机制


Kafka 中消息是以 topic 进行分类的，生产者生产消息，消费者消费消息，都是面向 topic的。

topic 是逻辑上的概念，而 partition 是物理上的概念，每个 partition 对应于一个 log 文件，该 log 文件中存储的就是 producer 生产的数据。Producer 生产的数据会被不断追加到该log 文件末端，且每条数据都有自己的 offset。消费者组中的每个消费者，都会实时记录自己消费到了哪个 offset，以便出错恢复时，从上次的位置继续消费。





由于生产者生产的消息会不断追加到 log 文件末尾，为防止 log 文件过大导致数据定位效率低下，Kafka 采取了分片和索引机制，将每个 partition 分为多个 segment。每个 segment对应两个文件——“.index”文件和“.log”文件。这些文件位于一个文件夹下，该文件夹的命名规则为：topic 名称+分区序号。例如，first 这个 topic 有三个分区，则其对应的文件夹为 first-0,first-1,first-2。

00000000000000000000.index
00000000000000000000.log
00000000000000170410.index
00000000000000170410.log
00000000000000239430.index
00000000000000239430.log
默认log大小是1G，当满了后会生成新的log文件，命名则以偏移量+1。

查询消息定位文件时：1.二分查找法找到索引。2.index索引中数据大小固定。3.根据index中的偏移量和大小从.log获取数据





3.2 生产者
3.2.1 分区策略
    1.分区的原因

方便扩展
提高并发
    2.如何分区

    发送数据为ProducerRecord对象。



指明 partition 的情况下，直接将指明的值直接作为 partiton 值；
没有指明 partition 值但有 key 的情况下，将 key 的 hash 值与 topic 的 partition 数进行取余得到 partition 值； k.hashcode / 分区数量
既没有 partition 值又没有 key 值的情况下，第一次调用时随机生成一个整数（后面每次调用在这个整数上自增），将这个值与 topic 可用的 partition 总数取余得到 partition 值，也就是常说的 round-robin 算法。 也就是一个随机值，然后轮询（分区数量，假设3个分区）210210210
3.2.2 数据可靠性保证
为保证 producer 发送的数据，能可靠的发送到指定的 topic，topic 的每个 partition 收到producer 发送的数据后，都需要向 producer 发送 ack（acknowledgement 确认收到），如果producer 收到 ack，就会进行下一轮的发送，否则重新发送数据。



1.副本同步策略



kafka是第二种策略：第一种方案冗余的数据太大，并且第二种网络延迟对kafka较小。

2.ISR

优化：使用ISR,Leader 维护了一个动态的 in-sync replica set (ISR)，意为和 leader 保持同步的 follower 集合。当 ISR 中的 follower 完成数据的同步之后，leader 就会给 follower 发送 ack。如果 follower长时间未向 eader同步数据，则该 follower将被 踢出ISR ， 该时间阈值由replica.lag.time.max.ms 参数设定。Leader 发生故障之后，就会从 ISR 中选举新的 leader。（再老版本中，会根据条数和时间来判断是否加入ISR，0.9版本之后只保留时间条件。）

3.ACK

0：producer 不等待 broker 的 ack，这一操作提供了一个最低的延迟，broker 一接收到还没有写入磁盘就已经返回，当 broker 故障时有可能丢失数据。

1：producer 等待 broker 的 ack，partition 的 leader 落盘成功后返回 ack，如果在 follower同步成功之前 leader 故障，那么将会丢失数据。

-1（all）：producer 等待 broker 的 ack，partition 的 leader 和 follower（ISR） 全部落盘成功后才返回 ack。但是如果在 follower 同步完成后，broker 发送 ack 之前，leader 发生故障，那么会造成数据重复。若果ISR只剩下leader也会丢数据。

4.数据一致性

消费数据一致性：



LEO:各个副本最大的偏移量。

HW：消费者能见到的最大偏移量。

生产数据一致性：（其实都是截取HW之后的数据）

（1）follower 故障
follower 发生故障后会被临时踢出 ISR，待该 follower 恢复后，follower 会读取本地磁盘记录的上次的 HW，并将 log 文件高于 HW 的部分截取掉，从 HW 开始向 leader 进行同步。等该 follower 的 LEO 大于等于该 Partition 的 HW，即 follower 追上 leader 之后，就可以重新加入 ISR 了。
（2）leader 故障
leader 发生故障之后，会从 ISR 中选出一个新的 leader，之后，为保证多个副本之间的数据一致性，其余的 follower 会先将各自的 log 文件高于 HW 的部分截掉，然后从新的 leader同步数据。
注意：这只能保证副本之间的数据一致性，并不能保证数据不丢失或者不重复。



3.2.3 Exactly Once
将服务器的 ACK 级别设置为-1，可以保证 Producer 到 Server 之间不会丢失数据，即 At Least Once 。相对的，将服务器 ACK 级别设置为 0，可以保证生产者每条消息只会被

发送一次，即 At Most Once 语义。 At Least Once 可以保证数据不丢失，但是不能保证数据不重复；相对的，At Least Once 可以保证数据不重复，但是不能保证数据不丢失。但是，对于一些非常重要的信息，比如说 交易数据，下游数据消费者要求数据既不重复也不丢失，即 Exactly Once。
kafka幂等性：Producer 不论向Server 发送多少次重复数据，Server 端都只会持久化一条。
enable.idompotence = true      // 开启幂等性

Kafka 的幂等性实现其实就是将原来下游需要做的去重放在了数据上游。开启幂等性的 Producer 在 初始化的时候会被分配一个 PID，发往同一 Partition 的消息会附带 Sequence Number。而 Broker端会对<PID, Partition, SeqNumber>做缓存，当具有相同主键的消息提交时，Broker只会持久化一条。 但是 PID 重启就会变化，同时不同的 Partition 也具有不同主键，所以幂等性无法保证跨分区跨会话的 Exactly Once。



3.3 消费者
3.3.1 消费方式
consumer 采用 pull（拉）模式从 broker 中读取数据。 push（推）模式很难适应消费速率不同的消费者，因为消息发送速率是由 broker 决定的。
push目标是尽可能以最快速度传递消息，但是这样很容易造成 consumer 来不及处理消息，典型的表现就是拒绝服务以及网络拥塞。
而 pull 模式则可以根据 consumer 的消费能力以适当的速率消费消息。
pull 模式不足之处是，如果 kafka 没有数据，消费者可能会陷入循环中，一直返回空数据。
针对这一点，Kafka 的消费者在消费数据时会传入一个时长参数 timeout，如果当前没有数据可供消费，consumer 会等待一段时间之后再返回，这段时长即为 timeout。

3.3.2 分区分配策略
轮询RoundRobin
多个消费者都订阅一个topic，消费差距1
Range
消费者订阅不同，消费差距可能扩大
3.4 高效的kafka
顺序读写
对于一个普通硬盘而言，顺序读写的性能普遍是随机读写的数倍以上，而高端ssd的读写性能还是顺序>随机读写。



零拷贝



分布式
通过多分区实现并发读写。
3.5 zookeeper的作用
Kafka 集群中有一个 broker 会被选举为 Controller，负责管理集群 broker 的上下线，所有 topic 的分区副本分配和 leader 选举等工作。Controller 的管理工作都是依赖于 Zookeeper 的。





ZooKeeper 作为一个分布式的协调服务框架，主要用来解决分布式集群中，应用系统需要面对的各种通用的一致性问题。ZooKeeper 本身可以部署为一个集群，集群的各个节点之间可以通过选举来产生一个 Leader，选举遵循半数以上的原则，所以一般集群需要部署奇数个节点。

ZooKeeper 最核心的功能是，它提供了一个分布式的存储系统，数据的组织方式类似于 UNIX 文件系统的树形结构。由于这是一个可以保证一致性的存储系统，所以你可以放心地在你的应用集群中读写 ZooKeeper 的数据，而不用担心数据一致性的问题。分布式系统中一些需要整个集群所有节点都访问的元数据，比如集群节点信息、公共配置信息等，特别适合保存在 ZooKeeper 中。

在这个树形的存储结构中，每个节点被称为一个“ZNode”。ZooKeeper 提供了一种特殊的 ZNode 类型：临时节点。这种临时节点有一个特性：如果创建临时节点的客户端与 ZooKeeper 集群失去连接，这个临时节点就会自动消失。

在 ZooKeeper 内部，它维护了 ZooKeeper 集群与所有客户端的心跳，通过判断心跳的状态，来确定是否需要删除客户端创建的临时节点。ZooKeeper 还提供了一种订阅 ZNode 状态变化的通知机制：Watcher，一旦 ZNode 或者它的子节点状态发生了变化，订阅的客户端会立即收到通知。



左侧部分这棵树保存的是 Kafka 的 Broker 信息，/brokers/ids/[0…N]，每个临时节点对应着一个在线的 Broker，Broker 启动后会创建一个临时节点，代表 Broker 已经加入集群可以提供服务了，节点名称就是 BrokerID，节点内保存了包括 Broker 的地址、版本号、启动时间等等一些 Broker 的基本信息。如果 Broker 宕机或者与 ZooKeeper 集群失联了，这个临时节点也会随之消失。

右侧部分的这棵树保的就是主题和分区的信息。/brokers/topics/ 节点下面的每个子节点都是一个主题，节点的名称就是主题名称。每个主题节点下面都包含一个固定的 partitions 节点，pattitions 节点的子节点就是主题下的所有分区，节点名称就是分区编号。

每个分区节点下面是一个名为 state 的临时节点，节点中保存着分区当前的 leader 和所有的 ISR 的 BrokerID。这个 state 临时节点是由这个分区当前的 Leader Broker 创建的。如果这个分区的 Leader Broker 宕机了，对应的这个 state 临时节点也会消失，直到新的 Leader 被选举出来，再次创建 state 临时节点。

3.6 事务
Kafka 从 0.11 版本开始引入了事务支持。事务可以保证 Kafka 在 Exactly Once 语义的基础上，生产和消费可以跨分区和会话，要么全部成功，要么全部失败。

3.6.1 生产者事务
为了实现跨分区跨会话的事务，需要引入一个全局唯一的 Transaction ID，并将 Producer获得的PID 和Transaction ID 绑定。这样当Producer 重启后就可以通过正在进行的 TransactionID 获得原来的 PID。为了管理 Transaction，Kafka 引入了一个新的组件 Transaction Coordinator。Producer 就是通过和 Transaction Coordinator 交互获得 Transaction ID 对应的任务状态。TransactionCoordinator 还负责将事务所有写入 Kafka 的一个内部 Topic，这样即使整个服务重启，由于事务状态得到保存，进行中的事务状态可以得到恢复，从而继续进行。

解决精准一次性写入kafka，支持跨分区，解决幂等性

3.6.2 消费者事务
对于 Consumer 而言，事务的保证就会相对较弱，尤其时无法保证 Commit 的信息被精确消费。这是由于 Consumer 可以通过 offset 访问任意信息，而且不同的 Segment File 生命周期不同，同一事务的消息可能会出现重启后被删除的情况。



4. 实际上手kafka
<!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients -->
		<dependency>
			<groupId>org.apache.kafka</groupId>
			<artifactId>kafka-clients</artifactId>
			<version>2.6.0</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka -->
		<dependency>
			<groupId>org.apache.kafka</groupId>
			<artifactId>kafka_2.13</artifactId>
			<version>2.6.0</version>
		</dependency>

4.1 生产者API
4.1.1 发送流程
Kafka 的 Producer 发送消息采用的是异步发送的方式。在消息发送的过程中，涉及到了 两个线程——main 线程和 Sender 线程，以及一个线程共享变量——RecordAccumulator。 main 线程将消息发送给 RecordAccumulator，Sender 线程不断从 RecordAccumulator 中拉取消息发送到 Kafka broker。




batch.size：只有数据积累到 batch.size 之后，sender 才会发送数据。
linger.ms：如果数据迟迟未达到 batch.size，sender 等待 linger.time 之后就会发送数据。

4.1.2 消息发送demo
 Properties props = new Properties();
        //kafka 集群，broker-list
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

获取分区源码解析：通过hash和分区数取模，之后进行轮询，因为第一次不知道走哪个分区（走可用的分区）
// 如果没有指定分区，就通过murmur2计算hash和分区数取模
 public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster, int numPartitions) {
        return keyBytes == null ? this.stickyPartitionCache.partition(topic, cluster) : Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
    }
自定义分区规则器

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

 // 自定义的分区规则器
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,"com.pb.mine.kafka.demo.MyPartitioner");

同步策略，使用get()，利用了java.util.concurrent.Future<V>  能够有返回值

producer.send(new ProducerRecord<String, String>("first", "pb01", "1"),(recordMetadata, e) -> {
            System.out.println("消息发送成功:" + recordMetadata.partition() + "," + recordMetadata.offset());
        }).get(); // 如果有 get() 就是同步发送

4.2 消费者API
Consumer 消费数据时的可靠性是很容易保证的，因为数据在 Kafka 中是持久化的，故不用担心数据丢失问题。由于 consumer 在消费过程中可能会出现断电宕机等故障，consumer 恢复后，需要从故障前的位置的继续消费，所以 consumer 需要实时记录自己消费到了哪个 offset，以便故障恢复后继续消费。所以 offset 的维护是 Consumer 消费数据是必须考虑的问题。

4.2.1 demo
enable.auto.commit：是否开启自动提交 offset 功能
auto.commit.interval.ms：自动提交 offset 的时间间隔
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
        consumer.subscribe(Arrays.asList("first"));
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

无论是同步提交还是异步提交 offset，都有可能会造成数据的漏消费或者重复消费。先提交 offset 后消费，有可能造成数据的漏消费；而先消费后提交 offset，有可能会造成数据的重复消费。


4.3 自定义存储 offset
Kafka 0.9 版本之前，offset 存储在 zookeeper，0.9 版本及之后，默认将 offset 存储在 Kafka 的一个内置的 topic 中。除此之外，Kafka 还可以选择自定义存储 offset。 offset 的维护是相当繁琐的，因为需要考虑到消费者的 Rebalace。
当有新的消费者加入消费者组、已有的消费者推出消费者组或者所订阅的主题的分区发 生变化，就会触发到分区的重新分配，重新分配的过程叫做 Rebalance。
消费者发生 Rebalance 之后，每个消费者消费的分区就会发生变化。因此消费者要首先获取到自己被重新分配到的分区，并且定位到每个分区最近提交的 offset 位置继续消费。
要实现自定义存储 offset，需要借助 ConsumerRebalanceListener，以下为示例代码，其 中提交和获取 offset 的方法，需要根据所选的 offset 存储系统自行实现。


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








P.S

加入ISR条件是：再老版本中，会根据条数和时间来判断是否加入ISR，0.9版本之后只保留时间条件。（因为条数是批量发送，所以可能会造成频繁的踢出拉入）  
若果ISR只剩下leader，ack为-1也会丢数据。  
如果ack-1会造成数据重复
保证消息有序（一个分区、同步发送消息get()）
