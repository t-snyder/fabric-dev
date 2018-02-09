package sdkwrapper.vo.transaction;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * This represents the Response portion of a Fabric Transaction Endorsement Request. This is also sent along with the 
 * original Request Payload to the Orderer Service for the transaction inclusion onto the Ledger.
 * 
 * @author tim
 *
 */
public class FabricResponse
{
  private static final Logger logger = LogManager.getLogger( FabricResponse.class );

  private String responseCode    = null;
  private String description     = null;
  private String responsePayload = null;
  
  public String getResponseCode()    { return responseCode;    }
  public String getDescription()     { return description;     }
  public String getResponsePayload() { return responsePayload; }
  
  public void setResponseCode(    String responseCode    ) { this.responseCode    = responseCode;    }
  public void setDescription(     String description     ) { this.description     = description;     }
  public void setResponsePayload( String responsePayload ) { this.responsePayload = responsePayload; }
  
  public String toJSON()
  {
    Gson gson = new Gson();    
    return gson.toJson( this );
  }

  public static FabricResponse fromJSON( String msg )
  {
    logger.info( "Received fabric request msg to parse. Msg = " + msg );

    if( msg == null )
      return null;
    
    Gson gson     = new Gson();
    Type MSG_TYPE = new TypeToken<FabricResponse>() {}.getType();

    FabricResponse response = null;
    StringReader   in      = new StringReader( msg );
    JsonReader     reader  = new JsonReader( in );
      
    response = gson.fromJson(  reader, MSG_TYPE );
    in.close();

    try
    {
      reader.close();
    } catch( IOException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
 
    logger.info( "Parsed fabric response msg. Msg = " + response.toString() );

    return response;
  }
  
}
