package sdkwrapper.request.context;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdkwrapper.exceptions.ConfigurationException;
import sdkwrapper.service.FabricServices;
import sdkwrapper.vo.config.ChainCodeInfo;
import sdkwrapper.vo.config.ChannelContextVO;
import sdkwrapper.vo.config.OrgUserVO;

public class FabricRequestCommand implements FabricRequestContextIF
{
  private final Logger logger = LogManager.getLogger( FabricRequestCommand.class );

  private OrgUserVO        orgUser        = null;
  private ChannelContextVO channelContext = null;
  private ChainCodeInfo    chainCode      = null;
  
  public void initialize( FabricServices service, Properties props )
    throws ConfigurationException
  {
    logger.info( "Start obtaining P2CLS user, channel, and chaincode context." );

    if( service == null )
      throw new ConfigurationException( "service is a required attribute." );
    
    if( props.getProperty( "counterParty" ) == null )
      throw new ConfigurationException( "counterParty is a required property key" );
    
    orgUser        = service.getUser(      "member" );
    channelContext = obtainChannel(        service  );
    chainCode      = service.getChainCode( "p2cls"  );
    
    if( orgUser        == null ) throw new ConfigurationException( "member user not set up for Organization" );
    if( channelContext == null ) throw new ConfigurationException( "public channel not found" );
    if( chainCode      == null ) throw new ConfigurationException( "public chaincode not found" );

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

  private ChannelContextVO obtainChannel( FabricServices service )
  {
    String orgId = service.getOrgContext().getOrgId();
    
    if( orgId != null )
    {
      return service.getChannels().get( orgId.toLowerCase() );
    }
    
    return null;
  }
}
