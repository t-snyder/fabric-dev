package sdkwrapper.runtimemgr;

import sdkwrapper.config.ConfigProperties;
import sdkwrapper.error.ErrorCommandIF;
import sdkwrapper.error.ErrorController;
import sdkwrapper.events.BlockEventProcessorIF;
import sdkwrapper.exceptions.ConfigurationException;
import sdkwrapper.service.FabricServices;

public interface RuntimeMgrIF
{
  public ErrorController       getErrorController();
  public FabricServices        getFabricServices();
  public BlockEventProcessorIF getBlockEventProcessor();
  public ConfigProperties      getConfig();
  
  public void initialize( String appPropsPath, String sdkConfigPath ) throws ConfigurationException;

  public void runErrorCmd( ErrorCommandIF cmd );
  public void sendResponseMsg( String msg );
  public void sendErrorMsg( String msg );
}
