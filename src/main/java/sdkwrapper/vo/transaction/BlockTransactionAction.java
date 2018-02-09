package sdkwrapper.vo.transaction;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo.NsRwsetInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class BlockTransactionAction
{
  private static final Logger logger = LogManager.getLogger( BlockTransactionAction.class );

  private String requestPayload          = null;
  private String description             = null;
  private String responseMsg             = null;
  private int    responseStatus          = 0;
  private byte[] proposalResponsePayload = null;
  private int    proposalResponseStatus  = 0;
  
  private List<BlockTranActionEndorsement> endorsements = new ArrayList<BlockTranActionEndorsement>();
  private List<NsRwsetInfo>                rwSet        = new ArrayList<NsRwsetInfo>();
  
  
  public String getRequestPayload()          { return requestPayload;          }
  public String getDescription()             { return description;             }
  public String getResponseMsg()             { return responseMsg;             }
  public int    getResponseStatus()          { return responseStatus;          }
  public byte[] getProposalResponsePayload() { return proposalResponsePayload; }
  public int    getProposalResponseStatus()  { return proposalResponseStatus;  }
  
  public List<BlockTranActionEndorsement> getEndorsements() { return endorsements; }
  public List<NsRwsetInfo>                getRwSet()        { return rwSet;        }
  
  public void setRequestPayload(          String requestPayload          ) { this.requestPayload          = requestPayload;          }
  public void setDescription(             String description             ) { this.description             = description;             }
  public void setResponseMsg(             String responseMsg             ) { this.responseMsg             = responseMsg;             }
  public void setResponseStatus(          int    responseStatus          ) { this.responseStatus          = responseStatus;          }
  public void setProposalResponsePayload( byte[] proposalResponsePayload ) { this.proposalResponsePayload = proposalResponsePayload; }
  public void setProposalResponseStatus(  int    proposalResponseStatus  ) { this.proposalResponseStatus  = proposalResponseStatus;  }

  /**
   * Note - This only works if the chaincode supports the sending in of a FabricRequest object.
   * 
   * @return
   */
  public FabricRequest obtainRequestObject()
  {
    logger.info( "BlockTransactionAction.obtainRequest marshalling json = " + requestPayload );
    
    Gson gson         = new Gson();
    Type REQUEST_TYPE = new TypeToken<FabricRequest>() {}.getType();

    FabricRequest request = null;
    StringReader  in      = new StringReader( requestPayload );
    JsonReader    reader  = new JsonReader(   in );
      
    //convert the json to OrgContext
    request = gson.fromJson( reader, REQUEST_TYPE );
    in.close();

    try
    {
      reader.close();
    } catch( IOException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    logger.info( "BlockTransactionAction.obtainRequest marshalled json = " + requestPayload  );
    return request;
  }

  /**
   * Note - This only works if the chaincode supports returning a FabricResponse serialized object.
   * 
   * @return
   */
  public FabricResponse obtainResponseObject()
  {
    logger.info( "BlockTransactionAction.obtainResponse marshalling json = " + proposalResponsePayload );
    
    Gson gson         = new Gson();
    Type RESPONSE_TYPE = new TypeToken<FabricResponse>() {}.getType();

    FabricResponse response = null;
    StringReader   in       = new StringReader( new String( proposalResponsePayload ));
    JsonReader     reader   = new JsonReader(   in );
      
    //convert the json to OrgContext
    response = gson.fromJson( reader, RESPONSE_TYPE );
    in.close();

    try
    {
      reader.close();
    } catch( IOException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    logger.info( "BlockTransactionAction.obtainRequest marshalled json = " + requestPayload  );
    return response;
  }

  public String toJSON()
  {
    Gson gson = new Gson();    
    return gson.toJson( this );
  }

  public static BlockTransactionAction fromJSON( String msg )
  {
    logger.info( "Received blocktransactionaction msg to parse. Msg = " + msg );

    if( msg == null )
      return null;
    
    Gson gson     = new Gson();
    Type MSG_TYPE = new TypeToken<BlockTransactionAction>() {}.getType();

    BlockTransactionAction response = null;
    StringReader           in      = new StringReader( msg );
    JsonReader             reader  = new JsonReader( in );
      
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

    logger.info( "Parsed BlockTransactionAction msg. Msg = " + response.toString() );

    return response;
  }
  
}
