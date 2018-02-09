package sdkwrapper.runtimemgr;

import java.net.MalformedURLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;

import sdkwrapper.config.ConfigKeysIF;
import sdkwrapper.config.ConfigProperties;
import sdkwrapper.error.ErrorCommandIF;
import sdkwrapper.error.ErrorController;
import sdkwrapper.events.BlockEventProcessorIF;
import sdkwrapper.events.BlockEventProcessorImpl;
import sdkwrapper.exceptions.ConfigurationException;
import sdkwrapper.kafka.WrapperKafkaConsumer;
import sdkwrapper.kafka.WrapperKafkaProducer;
import sdkwrapper.service.FabricServices;


/**
 * Provides the main method for starting up the sdk wrapper functionality.
 * 
 * @author tim
 *
 */
public class RuntimeMgrEmbedded implements RuntimeMgrIF
{
  private static final Logger logger = LogManager.getLogger( RuntimeMgrEmbedded.class );
  
  private FabricServices        fabricServices      = null;
  private ErrorController       errorController     = null;
  private BlockEventProcessorIF blockEventProcessor = null;  
  
  private ConfigProperties      config              = null;
  private WrapperKafkaProducer  producer            = null;
  private WrapperKafkaConsumer  consumer            = null;

  private String                responseTopic = null;
  private String                errorTopic    = null;
  
  public ErrorController       getErrorController()     { return errorController;     }
  public FabricServices        getFabricServices()      { return fabricServices;      }
  public BlockEventProcessorIF getBlockEventProcessor() { return blockEventProcessor; }
  public WrapperKafkaProducer  getProducer()            { return producer;            }
  public ConfigProperties      getConfig()              { return config;              }
  
  
  @Override
  public void initialize(String appPropsPath, String sdkConfigPath) 
   throws ConfigurationException 
  {
    logger.info( "Initializing the RuntimeMgr" );
    
    errorController     = new ErrorController( this );
    blockEventProcessor = new BlockEventProcessorImpl( this );
	
    logger.info( "Obtaining the organization configuration properties file." );
    loadOrgContextConfig( appPropsPath  );
    startFabricServices(  sdkConfigPath );
	
    producer = new WrapperKafkaProducer( (RuntimeMgrIF) this );
    consumer = new WrapperKafkaConsumer( (RuntimeMgrIF) this );

    responseTopic = config.getProperty( ConfigKeysIF.RESPONSE_TOPIC );
    errorTopic    = config.getProperty( ConfigKeysIF.ERROR_TOPIC    );
    
    // start consuming messages
    consumer.consume();
  }

  @Override
  public void runErrorCmd( ErrorCommandIF cmd ) 
  {
    cmd.processError();
  }

  public void sendResponseMsg( String responseMsg )
  {
    String key = UUID.randomUUID().toString();
    
    Future<RecordMetadata> result = null;
    try
    {
      result = producer.send( responseTopic, key, responseMsg );
    }
    catch( Exception e )
    {
      System.out.println( "Producer Exception encountered. Error = " + e.getMessage() );
    }

    // This is for informational purposes and not necessarily needed.
    while( !result.isDone() ) 
    {
      try
      {
        Thread.sleep(100);
      } catch( InterruptedException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } //sleep for 1 millisecond before checking again
    }
    
    try
    {
      RecordMetadata data = result.get();
      System.out.println( "Response topic = " + data.topic() + "; Offset = " + data.offset() + "; Partition = " + data.partition() );
    } 
    catch( InterruptedException | ExecutionException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void sendErrorMsg( String errorMsg )
  {
    String key = UUID.randomUUID().toString();
    
    Future<RecordMetadata> result = null;
    try
    {
      result = producer.send( errorTopic, key, errorMsg );
    }
    catch( Exception e )
    {
      System.out.println( "Producer Exception encountered. Error = " + e.getMessage() );
    }

    // This is for informational purposes and not necessarily needed.
    while( !result.isDone() ) 
    {
      System.out.println( "Error Msg Task is not completed yet...." );
      try
      {
        Thread.sleep(100);
      } catch( InterruptedException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } //sleep for 1 millisecond before checking again
    }
    
    try
    {
      RecordMetadata data = result.get();
      System.out.println( "Error topic = " + data.topic() + "; Offset = " + data.offset() + "; Partition = " + data.partition() );
    } 
    catch( InterruptedException | ExecutionException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private void loadOrgContextConfig( String propsPath )
   throws ConfigurationException
  {
	  config = new ConfigProperties( propsPath ); 
  }
  
  private void startFabricServices( String sdkConfigPath )
   throws ConfigurationException
  {
    fabricServices = new FabricServices( this );
    
    // Obtain defaultUserId
    String defaultUserId = config.getProperty( "default.userid" );
    if( defaultUserId == null )
      defaultUserId = "User1";
    
    try
    {
      fabricServices.initialize( sdkConfigPath, defaultUserId );
    } 
    catch( ConfigurationException e )
    {
      throw new ConfigurationException( "Error Starting FabricServices. Error = " + e.getMessage() );
    }
    catch( EnrollmentException e )
    {
      throw new ConfigurationException( "Error Starting FabricServices. Error = " + e.getMessage() );
    }
    catch( CryptoException e )
    {
      throw new ConfigurationException( "Error Starting FabricServices. Error = " + e.getMessage() );
    }
    catch( InvalidArgumentException e )
    {
      throw new ConfigurationException( "Error Starting FabricServices. Error = " + e.getMessage() );
    }
    catch( MalformedURLException e )
    {
      throw new ConfigurationException( "Error Starting FabricServices. Error = " + e.getMessage() );
    }
  }
  
  public static void main( String[] args )
  {
    String contextPath   = null;
    String sdkConfigPath = null;

    if( args.length < 1 ) contextPath = "/usr/lib/config/org-context.properties";
     else contextPath = args[0];
	
    if( args.length < 2 ) sdkConfigPath = "/usr/lib/config/config-sdk";
     else sdkConfigPath = args[1];
	
	
    RuntimeMgrIF runtimeMgr = new RuntimeMgrEmbedded();
	
    try
    {
      runtimeMgr.initialize( contextPath, sdkConfigPath);
    } 
    catch( ConfigurationException e )
    {
      System.out.println( "Configuration Error. Stopping system. Error = " + e.getMessage() );
      System.exit( -1 );
    }
  }
}
