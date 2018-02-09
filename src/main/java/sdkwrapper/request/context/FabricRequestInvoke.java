package sdkwrapper.request.context;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdkwrapper.exceptions.ConfigurationException;
import sdkwrapper.service.FabricServices;
import sdkwrapper.vo.config.ChainCodeInfo;
import sdkwrapper.vo.config.ChannelContextVO;
import sdkwrapper.vo.config.OrgUserVO;

public class FabricRequestInvoke implements FabricRequestContextIF
{
  private final Logger logger = LogManager.getLogger( FabricRequestPublic.class );

  private OrgUserVO        orgUser        = null;
  private ChannelContextVO channelContext = null;
  private ChainCodeInfo    chainCode      = null;

  @Override
  public void initialize( FabricServices service, Properties props ) throws ConfigurationException
  {
    logger.info( "Start obtaining user, channel, and chaincode context." );

    if( service == null )
      throw new ConfigurationException( "service is a required attribute." );

    String org1      = props.getProperty( "sourceOrg" );
    String org2      = props.getProperty( "targetOrg" );
    String userId    = props.getProperty( "userId"    );
    String method    = props.getProperty( "method"    );
    String channelId = getChannelId( org1, org2 );
 
    if( org1      == null ) throw new ConfigurationException( "sourceOrg is a required message property." );
    if( org2      == null ) throw new ConfigurationException( "targetOrg is a required message property." );
    if( userId    == null ) throw new ConfigurationException( "userId is a required message property" );
    if( method    == null ) throw new ConfigurationException( "method (fqn) is a required message property" );
    if( channelId == null ) throw new ConfigurationException( "channelId could not be determined from message properties" );
    
    orgUser        = service.getUser(           userId    );
    channelContext = service.getChannels().get( channelId );
    chainCode      = service.getChainCode(      "mgmtorgcc" );
    
    if( orgUser        == null ) throw new ConfigurationException( "member user not set up for Organization" );
    if( channelContext == null ) throw new ConfigurationException( "public channel not found" );
    if( chainCode      == null ) throw new ConfigurationException( "public chaincode not found" );

    // Set invoke function name (fqn)
    chainCode.setFunctName( method );
    
    logger.info( "Obtained channel with channel = " + channelContext.getChannelName() );
  }

  @Override
  public OrgUserVO getOrgUser()
  {
    return orgUser;
  }

  @Override
  public ChannelContextVO getChannelContext()
  {
    return channelContext;
  }

  @Override
  public ChainCodeInfo getChainCodeInfo()
  {
    return chainCode;
  }

  private String getChannelId( String org1, String org2 )
  {
    if(( org1.compareTo( "Org1" ) == 0 ) && org2.compareTo( "Org2" ) == 0 ) return "mgmtorg2channel";
    if(( org1.compareTo( "Org1" ) == 0 ) && org2.compareTo( "Org3" ) == 0 ) return "mgmtorg3channel";
    if(( org1.compareTo( "Org1" ) == 0 ) && org2.compareTo( "Org4" ) == 0 ) return "mgmtorg4channel";
    
    return "mgmtorg2channel";
  }

}
