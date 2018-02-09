package sdkwrapper.error;

import sdkwrapper.runtimemgr.RuntimeMgrIF;

public class ErrorController
{
  public static final int CHANNEL_INITIALIZATION_ERROR = 1001;
  public static final int CHANNEL_QUERY_ERROR          = 1002;
  

  private RuntimeMgrIF runtimeMgr = null;
  
  
  public ErrorController( RuntimeMgrIF runMgr )
  {
    this.runtimeMgr = runMgr;
  }
  
  
  /**
   * Provides services access propagation of fatal errors for either restart logic, or system shutdown.
   * 
   * @param errCode
   * @param description
   */
  public void handleError( int errCode, String description )
  {
    switch( errCode )
    {
      case CHANNEL_INITIALIZATION_ERROR : { processShutdownCmd( errCode, description ); break; }
    }
  }
  
  private void processShutdownCmd( int errCode, String description )
  {
    ShutdownCmd cmd = new ShutdownCmd();
    cmd.loadError( errCode, description );
    
    runtimeMgr.runErrorCmd( cmd );
  }
}
