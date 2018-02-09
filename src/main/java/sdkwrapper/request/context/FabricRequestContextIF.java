package sdkwrapper.request.context;

import java.util.Properties;

import sdkwrapper.exceptions.ConfigurationException;
import sdkwrapper.service.FabricServices;
import sdkwrapper.vo.config.ChainCodeInfo;
import sdkwrapper.vo.config.ChannelContextVO;
import sdkwrapper.vo.config.OrgUserVO;

/**
 * It is the responsibility of each implementation of this interface to provide a constructor
 * which loads the values to obtain for each of the getters within this interface. Normally the 
 * will pass in the FabricServices and optionally a properties file.
 * 
 * @author tim
 *
 */
public interface FabricRequestContextIF
{
  public void initialize( FabricServices services, Properties props )
   throws ConfigurationException;
  
  public OrgUserVO        getOrgUser();
  public ChannelContextVO getChannelContext();
  public ChainCodeInfo    getChainCodeInfo();
}
