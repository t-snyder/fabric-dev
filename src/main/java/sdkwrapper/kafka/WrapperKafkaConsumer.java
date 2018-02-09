package sdkwrapper.kafka;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import sdkwrapper.config.ConfigKeysIF;
import sdkwrapper.config.ConfigProperties;
import sdkwrapper.exceptions.ConfigurationException;
import sdkwrapper.exceptions.FabricRequestException;
import sdkwrapper.exceptions.FailedEndorsementException;
import sdkwrapper.exceptions.SDKWrapperInfrastructureException;
import sdkwrapper.runtimemgr.RuntimeMgrIF;
import sdkwrapper.service.FabricServices;
import sdkwrapper.vo.transaction.FabricRequest;

public class WrapperKafkaConsumer
{
  private static final Logger logger = LogManager.getLogger( WrapperKafkaConsumer.class );

  private RuntimeMgrIF                  runtimeMgr    = null;
  private KafkaConsumer<String, String> consumer      = null;
  private Properties                    props         = null;
  private ConfigProperties              orgProps      = null; 
  private List<String>                  topics        = null;
  private FabricServices                fabricService = null;

  @SuppressWarnings( "unused" )
  private WrapperKafkaConsumer()
  {
  }
  
  public WrapperKafkaConsumer( RuntimeMgrIF runtimeMgr )
   throws ConfigurationException
  {
    logger.info( "Starting WrapperKafkaConsumer." );

    if( runtimeMgr == null )
    {
      String errMsg = "Constructor attributes are required.";
      logger.error( errMsg );
      throw new ConfigurationException( errMsg );
    }
    
    this.runtimeMgr    = runtimeMgr;
    this.orgProps      = this.runtimeMgr.getConfig();
    this.fabricService = this.runtimeMgr.getFabricServices();
    this.topics        = obtainTopics();

    if( topics == null )
    {
      String errMsg = "Could not obtain consumer topics.";
      logger.error( errMsg );
      throw new ConfigurationException( errMsg );
    }
    
    props   = new Properties();
    
    props.put( ConfigKeysIF.BOOTSTRAP_SERVERS,  orgProps.getProperty( ConfigKeysIF.BOOTSTRAP_SERVERS  ));
    props.put( ConfigKeysIF.CONSUMER_GROUP_ID,  orgProps.getProperty( ConfigKeysIF.CONSUMER_GROUP_ID  ));
    props.put( ConfigKeysIF.KEY_DESERIALIZER,   orgProps.getProperty( ConfigKeysIF.KEY_DESERIALIZER   ));
    props.put( ConfigKeysIF.VALUE_DESERIALIZER, orgProps.getProperty( ConfigKeysIF.VALUE_DESERIALIZER ));
    
    consumer = new KafkaConsumer<>(props);
    logger.info( "KafkaConsumer started in WrapperKafkaConsumer." );
    
    consumer.subscribe( topics );
    logger.info( "KafkaConsumer subscribed to topics list = " + topics );
  }
  
  public void consume()
  {
    logger.info( "Starting to consume messages." );
    
    while( true ) 
    {
      ConsumerRecords<String, String> records = null;

      try
      {
        records = consumer.poll( 5000 );
      } 
      catch( Exception e )
      {
        System.out.println( "Consumer poll encountered an error. Error = " + e.getMessage() );
        System.exit( -1 );
      }

      if( records != null && !records.isEmpty() )
      {
        for( ConsumerRecord<String, String> record : records )
        {
          logger.info( "Received fabric request msg. Key = " + record.key() + ". Value = " + record.value() );

          FabricRequest request = FabricRequest.deserializeJSON( record.value() );
        
          try
          {
            fabricService.requestTransaction( request );
          } 
          catch( FabricRequestException e )
          {
            String errMsg = "Could not process fabric request for Key = " + record.key() + ". Value = " + record.value() + ". Error encountered = " + e.getMessage();
            logger.error( errMsg );
            runtimeMgr.sendErrorMsg( errMsg );
          } 
          catch( FailedEndorsementException e )
          {
            String errMsg = "Endorsement Failed for Key = " + record.key() + ". Value = " + record.value() + ". Error encountered = " + e.getMessage();
            logger.error( errMsg );
            runtimeMgr.sendErrorMsg( errMsg );
          } 
          catch( SDKWrapperInfrastructureException e )
          {
            String errMsg = "Could not process fabric request for Key = " + record.key() + ". Value = " + record.value() + ". Error encountered = " + e.getMessage();
            logger.error( errMsg );
            runtimeMgr.sendErrorMsg( errMsg );
          }
        }
      }
      
      // update consumer offset
      consumer.commitSync();
    }
  }
  
  public void close()
  {
    if( consumer != null )
      consumer.close();
  }
  
  private List<String> obtainTopics()
  {
    String topicStr = orgProps.getProperty( ConfigKeysIF.CONSUMER_TOPICS );
    
    if( topicStr != null )
    {
      String[] topics = topicStr.split( "," );
      
      for( int i = 0; i < topics.length; i++ )
      {
        topics[i] = topics[i].trim();
      }

      return (List<String>) Arrays.asList( topics ); 
    }
    
    return null;
  }
}
