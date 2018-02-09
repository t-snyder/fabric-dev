package sdkwrapper.request.context;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdkwrapper.exceptions.ConfigurationException;
import sdkwrapper.service.FabricServices;
import sdkwrapper.vo.config.ChainCodeInfo;
import sdkwrapper.vo.config.ChannelContextVO;
import sdkwrapper.vo.config.OrgUserVO;

public class FabricRequestContext implements FabricRequestContextIF
{
  private final Logger logger = LogManager.getLogger( FabricRequestContext.class );

  private OrgUserVO        orgUser        = null;
  private ChannelContextVO channelContext = null;
  private ChainCodeInfo    chainCode      = null;
  
  public void initialize( FabricServices service, Properties props )
    throws ConfigurationException
  {
    logger.info( "Start obtaining P2CLS user, channel, and chaincode context." );

    if( service == null )
      throw new ConfigurationException( "service is a required attribute." );
    
    if( props == null )
      throw new ConfigurationException( "props is a required attribute" );
    
    String memberId = props.getProperty( "memberId"  );
    String source   = props.getProperty( "sourceOrg" );
    String target   = props.getProperty( "targetOrg" );
    String method   = props.getProperty( "method"    );
    
    orgUser        = service.getUser(      memberId );
    channelContext = obtainChannel(        service, source, target );
    chainCode      = service.getChainCode( "mgmtorgcc" );
    
    if( orgUser        == null ) throw new ConfigurationException( "member user not set up for Organization" );
    if( channelContext == null ) throw new ConfigurationException( "public channel not found" );
    if( chainCode      == null ) throw new ConfigurationException( "public chaincode not found" );

    chainCode.setFunctName( method );

    logger.info( "Obtained P2CLS channel for channel = " + channelContext.getChannelName() );
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

  private ChannelContextVO obtainChannel( FabricServices service, String source, String target )
  {
    if( source.compareTo( "Org1" ) == 0 && target.compareTo( "Org2" ) == 0 ) return service.getChannels().get( "mgmtorg2channel" );
    if( source.compareTo( "Org1" ) == 0 && target.compareTo( "Org3" ) == 0 ) return service.getChannels().get( "mgmtorg3channel" );
    if( source.compareTo( "Org1" ) == 0 && target.compareTo( "Org4" ) == 0 ) return service.getChannels().get( "mgmtorg4channel" );
    
    return null;
  }
}
