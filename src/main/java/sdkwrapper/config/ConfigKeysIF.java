package sdkwrapper.config;

public interface ConfigKeysIF
{
  public static final String ORG_ID          = "org.context";
  
  public static final String KAFKA_LOGFILE      = "kafka.logfile";
  public static final String KAFKA_LOGDIR       = "kafka.logdir";
  public static final String BROKER_HOSTNAME    = "broker.hostname";
  public static final String BROKER_PORT        = "broker.port";
  public static final String BROKER_ID          = "broker.id";
  public static final String ZOOKEEPER_CONNECT  = "zookeeper.connect";
  public static final String LOG_FLUSH_INTERVAL = "log.flush.interval.messages";
  
  public static final String CONSUMER_TOPICS   = "consumer.topics";
  public static final String RESPONSE_TOPIC    = "response.topic";
  public static final String ERROR_TOPIC       = "error.topic";
  public static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
  public static final String CONSUMER_GROUP_ID = "group.id";
  
  public static final String PRODUCER_TOPIC   = "producer.topic"; 
  public static final String PRODUCER_TYPE    = "producer.type";
  public static final String QUEUE_TIME       = "queue.time";
  public static final String QUEUE_SIZE       = "queue.size";
  public static final String BATCH_SIZE       = "batch.size";
  public static final String BROKER_LIST      = "broker.list";

  public static final String KEY_SERIALIZER     = "key.serializer";
  public static final String VALUE_SERIALIZER   = "value.serializer";
  public static final String KEY_DESERIALIZER   = "key.deserializer";
  public static final String VALUE_DESERIALIZER = "value.deserializer";
  
  public static final String ZK_HOST          = "zk.host";
  public static final String ZK_PORT          = "zk.port";
  public static final String ZK_SNAPSHOT_DIR  = "zk.snapshot.dir";
  public static final String ZK_LOGFILE_DIR   = "zk.logfile.dir";
  public static final String ZK_TICK_TIME     = "zk.tick.time";
  
}
