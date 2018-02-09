package sdkwrapper.kafka;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import sdkwrapper.config.ConfigKeysIF;
import sdkwrapper.config.ConfigProperties;
import sdkwrapper.runtimemgr.RuntimeMgrIF;


/**
 * Sample Kafka producer for sending messages to the embedded kafka broker.
 * 
 * @author tim
 *
 */
public class WrapperKafkaProducer
{
  private static final Logger logger = LogManager.getLogger( WrapperKafkaProducer.class );

  private KafkaProducer<String, String> producer   = null;
  private Properties                    props      = null;
 
  private RuntimeMgrIF     runtimeMgr = null;
  private ConfigProperties orgProps   = null;


  /**
   * Initialize the Kafka Producer prior to sending messages.
   * 
   * @param runtimeMgr
   */
  public WrapperKafkaProducer( RuntimeMgrIF runtimeMgr )
  {
    this.runtimeMgr = runtimeMgr;
    this.orgProps   = runtimeMgr.getConfig();
    
    props = new Properties();
    
    props.put( ConfigKeysIF.PRODUCER_TYPE,     orgProps.getProperty( ConfigKeysIF.PRODUCER_TYPE    ));
    props.put( ConfigKeysIF.QUEUE_TIME,        orgProps.getProperty( ConfigKeysIF.QUEUE_TIME       ));
    props.put( ConfigKeysIF.QUEUE_SIZE,        orgProps.getProperty( ConfigKeysIF.QUEUE_SIZE       ));
    props.put( ConfigKeysIF.BATCH_SIZE,        orgProps.getProperty( ConfigKeysIF.BATCH_SIZE       ));
    props.put( ConfigKeysIF.BOOTSTRAP_SERVERS, orgProps.getProperty( ConfigKeysIF.BOOTSTRAP_SERVERS  ));
    props.put( ConfigKeysIF.KEY_SERIALIZER,    orgProps.getProperty( ConfigKeysIF.KEY_SERIALIZER   ));
    props.put( ConfigKeysIF.VALUE_SERIALIZER,  orgProps.getProperty( ConfigKeysIF.VALUE_SERIALIZER ));
        
    producer = new KafkaProducer<String, String>( props );
  }
  
  /**
   * 
   * @param topic - A string identifier for the topic to send the message value to.
   * @param key   - A unique identifier for the message. This will be used to determine the appropriate partition on the topic.
   *                The key can also be used to determine duplicate messages and ensure a Write Once and Only Once semantic. 
   * @param value - The string value of the message.The representation of this value needs to be understood by the request 
   *                context objects which will determine the appropriate Fabric channel to send the request to. Also the value
   *                will be sent to the Fabric Peers for endorsement without modification so the appropriate chaincode must
   *                also understand the value representation. The default representation is a json representation of the
   *                FabricRequest value object.
   * 
   * @return Future<RecordMetadata> or null; Null is returned when the required method parameters are not included. Otherwise a
   *         Future is returned which can be inspected for the final send result.
   */
  public Future<RecordMetadata> send( String topic, String key, String value )
  {
    if( topic == null ) { logger.error( "Method attributes are required. Found null topic." ); return null; }
    if( key   == null ) { logger.error( "Method attributes are required. Found null key."   ); return null; }
    if( value == null ) { logger.error( "Method attributes are required. Found null value." ); return null; }
    
    return producer.send( new ProducerRecord<String, String>( topic, key, value ));
  }
  
}
