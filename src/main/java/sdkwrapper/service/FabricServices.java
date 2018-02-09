package sdkwrapper.service;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.BlockListener;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.EventHubException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;

import sdkwrapper.block.listener.FabricBlockListener;
import sdkwrapper.config.OrgContextJsonParser;
import sdkwrapper.error.ErrorController;
import sdkwrapper.exceptions.ConfigurationException;
import sdkwrapper.exceptions.FabricRequestException;
import sdkwrapper.exceptions.FailedEndorsementException;
import sdkwrapper.exceptions.SDKWrapperInfrastructureException;
import sdkwrapper.request.context.FabricRequestContextIF;
import sdkwrapper.request.context.RequestContextBuilder;
import sdkwrapper.runtimemgr.RuntimeMgrIF;
import sdkwrapper.vo.config.ChainCodeInfo;
import sdkwrapper.vo.config.ChannelContextVO;
import sdkwrapper.vo.config.OrdererNodeVO;
import sdkwrapper.vo.config.OrgContextVO;
import sdkwrapper.vo.config.OrgUserVO;
import sdkwrapper.vo.config.PeerVO;
import sdkwrapper.vo.transaction.FabricRequest;


public class FabricServices
{
  public static final String PORT_SEPARATOR        = ":";
  public static final int    MAX_PROPOSAL_ATTEMPTS = 3;
  
  private final Logger logger = LogManager.getLogger( FabricServices.class );
  
  private RuntimeMgrIF runtimeMgr = null;
  private HFClient     hfClient   = null;
  private OrgContextVO orgContext = null;
  
  private RequestContextBuilder contextBuilder = null;  
  
  private Map<String, OrgUserVO>        users      = new HashMap<String, OrgUserVO>();
  private Map<String, ChannelContextVO> channels   = new HashMap<String, ChannelContextVO>();
  private Map<String, ChainCodeInfo>    chainCodes = new HashMap<String, ChainCodeInfo>();
  
  public HFClient                      getHFClient()   { return hfClient;   }
  public OrgContextVO                  getOrgContext() { return orgContext; }
  public Map<String, OrgUserVO>        getUsers()      { return users;      }
  public Map<String, ChannelContextVO> getChannels()   { return channels;   }
  public Map<String, ChainCodeInfo>    getChainCodes() { return chainCodes; }

  public void setOrgContext( OrgContextVO context ) { this.orgContext = context; }
 
  public FabricServices( RuntimeMgrIF runMgr )
  {
    this.runtimeMgr = runMgr;
    this.contextBuilder = new RequestContextBuilder( this );
  }
  
  /**
   * Read the organization context json, parse into the domain objects (via OrgContextJsonParser), and initialize the 
   * channel connections.
   * 
   * @param contextPath
   * @throws CryptoException
   * @throws InvalidArgumentException
   * @throws MalformedURLException
   * @throws EnrollmentException
   * @throws Configorg.hyperledger.fabric.sdk.ChaincodeIDurationException
   */
  public void initialize( String contextPath, String defaultUserId )
   throws CryptoException, InvalidArgumentException, MalformedURLException, EnrollmentException, ConfigurationException
  {
    logger.info( "Start organization initialization with context file = " + contextPath );
 
    // Initialize Fabric SDK Controller
    hfClient = HFClient.createNewInstance();
    hfClient.setCryptoSuite( CryptoSuite.Factory.getCryptoSuite() );

    // Load the Organization's context Json file
    OrgContextJsonParser.buildOrgContext( this, contextPath );

    logger.info( "Parsed context file = " + contextPath );

    // Load Default User Context into SDK Client
    hfClient.setUserContext( users.get( defaultUserId ));
    
    // Initialize Channels
    channels.forEach(( k, v )->{
    try
    {
      initializeChannel( v );
    } catch( InvalidArgumentException | EventHubException | TransactionException | ConfigurationException e )
      {
        logger.error( "Error initializing channel = " + k + ". Error = " + e.getMessage() );
        runtimeMgr.getErrorController().handleError( ErrorController.CHANNEL_INITIALIZATION_ERROR, "Channel not Initialized. Channel = " + k );
      }
    });
  }
  
  
  public void requestTransaction( FabricRequest request )
    throws FabricRequestException, FailedEndorsementException, SDKWrapperInfrastructureException
  {
    if( request == null )
      throw new FabricRequestException( "Request = null. FabricRequest is required." );

    FabricRequestContextIF requestContext = null;
    try
    {
      requestContext = contextBuilder.buildContext( request );
    } 
    catch( FabricRequestException e )
    {
      String errMsg = "Could not process fabric request. Error encountered = " + e.getMessage();
      logger.error( errMsg );
      throw new FabricRequestException( errMsg );
    }
    
    String[] payload = request.getTranPayload();
    if( payload == null )
    {
      String errMsg = "Tran payload = null. Payload is required.";
      logger.error( errMsg );
      throw new FabricRequestException( errMsg );
    }
    
    OrgUserVO        orgUser        = requestContext.getOrgUser();
    ChannelContextVO channelContext = requestContext.getChannelContext();
    ChainCodeInfo    chainCodeInfo  = requestContext.getChainCodeInfo();
    
    if( orgUser == null )
    {
      String errMsg = "FabricRequestContextIF object did not contain required orgUser object.";
      logger.error( errMsg );
      throw new FabricRequestException( errMsg );
    }

    if( channelContext == null )
    {
      String errMsg = "FabricRequestContextIF object did not contain required channelContext object.";
      logger.error( errMsg );
      throw new FabricRequestException( errMsg );
    }

    if( chainCodeInfo == null )
    {
      String errMsg = "FabricRequestContextIF object did not contain required chainCodeInfo object.";
      logger.error( errMsg );
      throw new FabricRequestException( errMsg );
    }

    Channel     sdkChannel  = channelContext.getSdkChannel();
    ChaincodeID chainCodeId = getChainCodeId( chainCodeInfo );
  
    TransactionProposalRequest proposalRequest = hfClient.newTransactionProposalRequest();
    proposalRequest.setProposalWaitTime( channelContext.getInvokeWaitTime() );
    proposalRequest.setChaincodeID( chainCodeId );
    proposalRequest.setFcn(         chainCodeInfo.getFunctName() );
    proposalRequest.setArgs( payload );
    
    Collection<ProposalResponse> returnedEndorsements = null;
    int iter = 0;
    while( true )
    {
      try
      {
        returnedEndorsements = sdkChannel.sendTransactionProposal( proposalRequest, sdkChannel.getPeers() );
        logger.info( "Endorsements returned for payload = " + payload );
        break;
      } 
      // Generated by invalid arguments. As we do not know the correct ones, throw an exception back to the invoking code.
      catch( InvalidArgumentException | ProposalException e )
      {
        // Generated by system error, generally connection. Attempt to reconnect and retry a max number of times.

        if( iter >= MAX_PROPOSAL_ATTEMPTS )
        {
          String errMsg = "MAX_PROPOSAL_ATTEMPTS for endorsement failed with exception = " + e.getMessage();
          logger.error( errMsg );
          throw new FabricRequestException( errMsg );
        }
        
        String errMsg = "Endorsement Exception. Exception = " + e.getMessage();
        logger.error( errMsg );

        e.printStackTrace();

        iter++;
        // Force channel shutdown, release all channel resources and then initialize a new sdk channel.
        restartChannel( channelContext );
      }
    }

    // Determine whether the endorsement policy has been met via the policy associated with the chaincode.
    if( !chainCodeInfo.getPolicy().isPolicyMet( returnedEndorsements ))
    {
      if( chainCodeInfo.getPolicy().getFailedResponses() != null )
      {
        String errMsg = "Endorsement Failed. Found Failed Responses.";
        logger.error( errMsg );
        throw new FailedEndorsementException( chainCodeInfo.getPolicy().getFailedResponses() );
      }
      else
      {
        String errMsg = "Endorsement Failed. Minimum responses not received.";
        logger.error( errMsg );
        throw new FailedEndorsementException( errMsg );
      }
    }
    
    logger.info( "Sending ledger transaction to orderer." );
    try
    {
      // Note we are not capturing the CompleteableFuture returned here as it is only Completed for the transaction after.
      // the BlockEvent is returned from the Peer. Instead we are listening for and processing the Block Events.
      sdkChannel.sendTransaction( chainCodeInfo.getPolicy().getSuccessfulResponses(), sdkChannel.getOrderers() );
    } 
    catch( Exception e )
    {
      // All purpose catch for transport errors which are passed thru.
      String errMsg = "Error sending transaction to orderer. Error = " + e.getMessage();
      logger.error( errMsg );
      throw new FabricRequestException( errMsg );
    }
  }

  
  public OrgUserVO getUser( String id )
  {
    if( users.containsKey( id ))
      return users.get( id );
    
    return null;
  }

  /**
   * Obain the SDK Channel for the id parameter.
   * 
   * @param id
   * @return
   */
  public Channel getSDKChannel( String id )
  {
    if( channels.containsKey( id ))
      return channels.get( id ).getSdkChannel();
    
    return null;
  }
  
  /**
   * Obtain the chaincode associated with the id parameter.
   * 
   * @param id
   * @return
   */
  public ChainCodeInfo getChainCode( String id )
  {
    if( chainCodes.containsKey( id ))
      return chainCodes.get( id );
    
    return null;
  }

  
  /**
   * Set a Block Event Listener on the channel for receiving Block Events.
   * 
   * @param channel
   * @param listener
   * @throws InvalidArgumentException
   */
  public void setBlockListener( Channel channel, BlockListener listener )
   throws InvalidArgumentException
  {
    channel.registerBlockListener( listener );
  }

  /**
   * Obtain the chainCodeId from the ChainCodeInfo object. This uses the Chaincode name, version and path.
   * 
   * @param chainInfo
   * @return
   */
  public ChaincodeID getChainCodeId( ChainCodeInfo chainInfo )
  {
    return ChaincodeID.newBuilder().setName( chainInfo.getChainCodeName() )
                                   .setVersion( chainInfo.getChainCodeVersion() )
                                   .setPath( chainInfo.getChainCodePath() )
                                   .build();
  }

  /**
   * Initialize the SDK Fabric channel and the connection to the channel.
   * 
   * @param channelVO
   * @throws InvalidArgumentException
   * @throws EventHubException
   * @throws TransactionException
   * @throws ConfigurationException
   */
  private void initializeChannel( ChannelContextVO channelVO )
   throws InvalidArgumentException, EventHubException, TransactionException, ConfigurationException
  {
    logger.info( "Loading Channel for - channelName  - " + channelVO.getChannelName() );
    Channel channel = hfClient.newChannel( channelVO.getChannelName() );
    
    // Build and load the Fabric Peers authorized to this chain and used for Endorsement
    for( PeerVO peerVO : channelVO.getEndorserPeers() )
    {
      // Build Peer Properties file. Valid properties are:
      //  pemfile - File location for x509 pem certificate for SSL
      //  trustServerCertificate - boolean override CN to match pemfile certificate - for dev only.
      //  hostnameOverride - Specify the certificates CN - for dev only
      //  sslProvider - Specify the SSL provider - 'openSSL' or 'JDK'
      //  negotiationType - Specify the type of negotiation - TLS or planText
      Properties props = new Properties();
      props.put( "pemfile", peerVO.getTlsCertificate() );
      props.put( "trustServerCertificate", peerVO.isTrustServerCert() );
      props.put( "sslProvider", peerVO.getSslProvider() );
      props.put( "negotiationType", peerVO.getNegotiationType() );
      
      Peer sdkPeer = hfClient.newPeer( peerVO.getPeerId(), peerVO.getEndorseUrl() );
      peerVO.setSdkPeer( sdkPeer );
      channel.addPeer(   sdkPeer );
    }
    
    // Build the SDK Orderer objects for the channel
    for( OrdererNodeVO nodeVO : channelVO.getOrdererNodes() )
    {
      // See Properties file comments above
      Properties props = new Properties();
      props.put( "pemfile", nodeVO.getTlsCertificate() );
      props.put( "trustServerCertificate", nodeVO.isTrustServerCert() );
      props.put( "sslProvider", nodeVO.getSslProvider() );
      props.put( "negotiationType", nodeVO.getNegotiationType() );
      
      Orderer sdkOrderer = hfClient.newOrderer( nodeVO.getNodeId(), nodeVO.getOrdererUrl() );
      nodeVO.setSdkOrderer( sdkOrderer );
      channel.addOrderer(   sdkOrderer );
    }
    
    // Build and load the Fabric Peers used as an Event Hub for this channel. Note this does not register
    // a listener on the channel event hub.
    for( PeerVO peerVO : channelVO.getEventHubs() )
    {
      Properties props = new Properties();
      props.put( "pemfile", peerVO.getTlsCertificate() );
      props.put( "trustServerCertificate", peerVO.isTrustServerCert() );
      props.put( "sslProvider", peerVO.getSslProvider() );
      props.put( "negotiationType", peerVO.getNegotiationType() );
      
      EventHub eventHub = hfClient.newEventHub( peerVO.getPeerId(), peerVO.getEventHubUrl(), props );
      peerVO.setSdkEventHub( eventHub );
      channel.addEventHub(   eventHub );
    }
    
    // Now initialize the chain within the SDK
    channel.setTransactionWaitTime( channelVO.getInvokeWaitTime() );
    channel.setDeployWaitTime(      channelVO.getDeployWaitTime() );
 
    try
    {
      channel.initialize();
    } 
    catch( InvalidArgumentException e )
    {
      System.out.println( "Error initializing channel. Error = " + e.getMessage() );
      e.printStackTrace();  
    }
    catch( TransactionException e )
    {
      System.out.println( "Error initializing channel. Error = " + e.getMessage() );
      e.printStackTrace();
    }
    
    // Now set the block event listeners for the Event Hubs.
    if( channel.getEventHubs() == null || channel.getEventHubs().size() < 1 )
    {
      throw new ConfigurationException( "No event Hubs found." );
    }

    FabricBlockListener listener = new FabricBlockListener( channelVO.getChannelName(), channel, runtimeMgr.getBlockEventProcessor() );
    channel.registerBlockListener( listener );

    channelVO.setSdkChannel(    channel  );
    channelVO.setBlockListener( listener );   
    
    System.out.println( "Channel = " + channelVO.getChannelName() + "; initialized flag = " + channel.isInitialized() + "; shutdown flag = " + channel.isShutdown() );
  }
  
  private void restartChannel( ChannelContextVO channelContext )
   throws SDKWrapperInfrastructureException
  {
    channelContext.getSdkChannel().shutdown( true );
    channelContext.setBlockListener( null );
    
    try
    {
      initializeChannel( channelContext );
    } 
    catch( InvalidArgumentException e )
    {
      throw new SDKWrapperInfrastructureException( "Error Restarting Channel. Error = " + e.getMessage() );
    } 
    catch( EventHubException e )
    {
      throw new SDKWrapperInfrastructureException( "Error Restarting Channel. Error = " + e.getMessage() );
    } 
    catch( TransactionException e )
    {
      throw new SDKWrapperInfrastructureException( "Error Restarting Channel. Error = " + e.getMessage() );
    } 
    catch( ConfigurationException e )
    {
      throw new SDKWrapperInfrastructureException( "Error Restarting Channel. Error = " + e.getMessage() );
    }
  }
}
