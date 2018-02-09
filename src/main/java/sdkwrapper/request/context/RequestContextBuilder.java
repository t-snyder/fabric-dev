package sdkwrapper.request.context;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import sdkwrapper.exceptions.ConfigurationException;
import sdkwrapper.exceptions.FabricRequestException;
import sdkwrapper.service.FabricServices;
import sdkwrapper.vo.transaction.FabricRequest;

public class RequestContextBuilder
{
  private static final Logger logger = LogManager.getLogger( RequestContextBuilder.class );
  
  private FabricServices fabricService = null;
  
  
  public RequestContextBuilder( FabricServices service )
  {
    this.fabricService = service;
  }

  /**
   * The buildContext method uses the information obtained within the request msg to build the appropriate class implementing
   * the FabricRequestContextIF interface. The purpose of the implementation is to provide the Fabric Services the necessary
   * information with which to determine the appropriate channel and chaincode. The information used from within the 
   * FabricRequest is:
   *    requestContext - this is the class path name for the FabricRequestIF implementation. ex - com.cls.sdkwrapper.request.context.FabricRequestPublic
   *    contextProps   - this is an optional implementation specific properties file. It can be used to provide information such has counterparty.
   *    
   * @param request
   * @return
   * @throws FabricRequestException
   */
  public FabricRequestContextIF buildContext( FabricRequest request )
   throws FabricRequestException
  {
    if( request == null || request.getRequestContext() == null || request.getTranPayload() == null )
    {
      String errMsg = "Request is invalid. Missing required attributes.";
      logger.error( errMsg );
      throw new FabricRequestException( errMsg );
    }
    
    FabricRequestContextIF context = null;
    try
    {
      context = (FabricRequestContextIF) Class.forName( request.getRequestContext() ).newInstance();
    } 
    catch( ClassNotFoundException | IllegalAccessException | InstantiationException e )
    {
      String errMsg = "Error instantiating FabricRequestContextIF implementationn class. Error = " + e.getMessage();
      logger.error( errMsg );
      throw new FabricRequestException( errMsg );
    }
    
    try
    {
      context.initialize( fabricService, request.getContextProps() );
    } 
    catch( ConfigurationException e )
    {
      throw new FabricRequestException( "Can not process ledger transaction request. Error = " + e.getMessage() );
    }
    
    return context;
  }
}
