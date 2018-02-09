package sdkwrapper.vo.transaction;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * This represents the Endorsement Request Payload. This is the object which is passed to the SDK Wrapper (in json format) to 
 * initiate a ledger transaction request.
 *  
 *   The requestContext is used to determine which of the com.cls.sdkwrapper.request.context implementation classes to use for 
 *   determining which channel to send the ledger request on. Thus in the examples provided it can contain:
 *      com.cls.sdkwrapper.request.context.FabricRequestPublic (or)
 *      com.cls.sdkwrapper.request.context.FabricRequestCommand;
 *   It is exepected that specific applications will need to provide their own implementations per their requirements. 
 *   
 *   The contextProps provide the ability to pass in any additional information necessary for setting up the Fabric Request
 *   context. These values are specific to the FabricRequestContextIF implementation.
 *   
 *   If the chaincode validates time based attribute(s) then the sendDateTime and timeTolerance attributes should be set.
 *   The reason is to provide a deterministic way of validating time across multiple nodes in various data centers. The 
 *   sendDateTime is the current Timestamp in string format. The timeTolerance is an app specific tolerance that it deems 
 *   acceptable. Each Endorsing Node will obtain its internal timestamp and determine whether this timestamp is within an
 *   acceptable tolerance of the clients sendDateTime. If not the transaction should be rejected for endorsement.
 *   
 *   The Properties (props) provides an application specific set of attributes to send with the transaction as a JSON list of
 *   key value pairs. It is the responsibility of the chaincode to understand the items.
 *   
 *   The tranPayload is a string representation of a JSON structure that represents the domain information of the transaction.
 *   It is the responsibility of the chaincode to understand this structure.
 *   
 * @author tim
 *
 */
public class FabricRequest
{
  private static final Logger logger = LogManager.getLogger( FabricRequest.class );

  private String     requestContext  = null;
  private Properties contextProps    = null;
  private String     sendDateTime    = null;
  private int        timeTolerance   = 0;
  private Properties props           = null;
  private String[]   tranPayload     = null;
  
  public String     getRequestContext() { return requestContext; }
  public Properties getContextProps()   { return contextProps;   }
  public String     getSendDateTime()   { return sendDateTime;   }
  public int        getTimeTolerance()  { return timeTolerance;  }
  public Properties getProps()          { return props;          }
  public String[]   getTranPayload()    { return tranPayload;    }
  
  public void setRequestContext( String     requestContext ) { this.requestContext = requestContext; }
  public void setContextProps(   Properties contextProps   ) { this.contextProps   = contextProps;   }
  public void setSendDateTime(   String     sendDateTime   ) { this.sendDateTime   = sendDateTime;   }
  public void setTimeTolerance(  int        timeTolerance  ) { this.timeTolerance  = timeTolerance;  }
  public void setProps(          Properties props          ) { this.props          = props;          }
  public void setTranPayload(    String[]   tranPayload    ) { this.tranPayload    = tranPayload;    }
 
  public String toJSON()
  {
    Gson gson = new Gson();    
    return gson.toJson( this );
  }
  
  public static FabricRequest deserializeJSON( String msg )
  {
    logger.info( "Received fabric request msg to parse. Msg = " + msg );

    if( msg == null )
      return null;
    
    Gson gson     = new Gson();
    Type MSG_TYPE = new TypeToken<FabricRequest>() {}.getType();

    FabricRequest request = null;
    StringReader  in      = new StringReader( msg );
    JsonReader    reader  = new JsonReader( in );
      
    request = gson.fromJson(  reader, MSG_TYPE );
    in.close();

    try
    {
      reader.close();
    } catch( IOException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    logger.info( "Parsed fabric request msg. Msg = " + request.toString() );

    return request;
  }
}
