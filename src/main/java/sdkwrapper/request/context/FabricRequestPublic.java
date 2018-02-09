package sdkwrapper.request.context;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdkwrapper.exceptions.ConfigurationException;
import sdkwrapper.service.FabricServices;
import sdkwrapper.vo.config.ChainCodeInfo;
import sdkwrapper.vo.config.ChannelContextVO;
import sdkwrapper.vo.config.OrgUserVO;



public class FabricRequestPublic implements FabricRequestContextIF
{
  private final Logger logger = LogManager.getLogger( FabricRequestPublic.class );

  private OrgUserVO        orgUser        = null;
  private ChannelContextVO channelContext = null;
  private ChainCodeInfo    chainCode      = null;
  
  public void initialize( FabricServices service, Properties props ) 
    throws ConfigurationException
  {
    logger.info( "Start obtaining Public user, channel, and chaincode context." );

    if( service == null )
      throw new ConfigurationException( "service is a required attribute." );

    orgUser        = service.getUser(           "member" );
    channelContext = service.getChannels().get( "public" );
    chainCode      = service.getChainCode(      "public" );
    
    if( orgUser        == null ) throw new ConfigurationException( "member user not set up for Organization" );
    if( channelContext == null ) throw new ConfigurationException( "public channel not found" );
    if( chainCode      == null ) throw new ConfigurationException( "public chaincode not found" );

    logger.info( "Obtained Public channel with channel = " + channelContext.getChannelName() );
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

}
