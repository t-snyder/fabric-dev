package sdkwrapper.vo.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hyperledger.fabric.sdk.Channel;

import sdkwrapper.block.listener.FabricBlockListener;

/**
 * Represents a persisted chain object which is used within the Fabric Wrapper to manage Fabric SDK
 * invocations.
 * 
 * @author tim
 *
 */
public class ChannelContextVO implements Serializable
{
  private static final long serialVersionUID = -2323808360497202169L;

  private String                    channelName    = null;
  private String                    channelType    = null;
  private int                       invokeWaitTime = 0;
  private int                       deployWaitTime = 0;
  private int                       gossipWaitTime = 0;
  private Collection<PeerVO>        endorserPeers  = new ArrayList<PeerVO>();
  private Collection<OrdererNodeVO> ordererNodes   = new ArrayList<OrdererNodeVO>();
  private Collection<PeerVO>        eventHubs      = new ArrayList<PeerVO>();
  private FabricBlockListener       blockListener  = null;
  
  // Transient Attributes
  private Channel sdkChannel = null;
  
  public String  getChannelName()    { return channelName;    }
  public String  getChannelType()    { return channelType;    }
  public int     getInvokeWaitTime() { return invokeWaitTime; }
  public int     getDeployWaitTime() { return deployWaitTime; }
  public int     getGossipWaitTime() { return gossipWaitTime; }
  public Channel getSdkChannel()     { return sdkChannel;     }

  public Collection<PeerVO>        getEndorserPeers() { return endorserPeers; }
  public Collection<OrdererNodeVO> getOrdererNodes()  { return ordererNodes;  }
  public Collection<PeerVO>        getEventHubs()     { return eventHubs;     }
  public FabricBlockListener       getBlockListener() { return blockListener; }
  
  public void setChannelName(    String  channelName    ) { this.channelName    = channelName;    }
  public void setChannelType(    String  channelType    ) { this.channelType    = channelType;    }
  public void setInvokeWaitTime( int     invokeWaitTime ) { this.invokeWaitTime = invokeWaitTime; }
  public void setDeployWaitTime( int     deployWaitTime ) { this.deployWaitTime = deployWaitTime; }
  public void setGossipWaitTime( int     gossipWaitTime ) { this.gossipWaitTime = gossipWaitTime; }
  public void setSdkChannel(     Channel sdkChannel     ) { this.sdkChannel     = sdkChannel;     }
  
  public void setEndorserPeers( List<PeerVO>        peers    ) { this.endorserPeers = peers;    }
  public void setOrdererNodes(  List<OrdererNodeVO> nodes    ) { this.ordererNodes  = nodes;    }
  public void setEventHubs(     List<PeerVO>        hubs     ) { this.eventHubs     = hubs;     }
  public void setBlockListener( FabricBlockListener listener ) { this.blockListener = listener; }
}
