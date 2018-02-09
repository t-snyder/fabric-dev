package sdkwrapper.block.listener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.BlockListener;
import org.hyperledger.fabric.sdk.Channel;

import com.google.protobuf.InvalidProtocolBufferException;

import sdkwrapper.events.BlockEventProcessorIF;

public class FabricBlockListener implements BlockListener
{
  private static Logger logger = LogManager.getLogger( FabricBlockListener.class );
  
  private String                channelName         = null;
  private Channel               sdkChannel          = null;
  private BlockEventProcessorIF blockEventProcessor = null;

  public FabricBlockListener( String channelName, Channel sdkChannel, BlockEventProcessorIF blockEventProcessor )
  {
    this.channelName         = channelName;
    this.sdkChannel          = sdkChannel;
    this.blockEventProcessor = blockEventProcessor;
  }
  
  public String  getChannelName() { return channelName; }
  public Channel getSdkChannel()  { return sdkChannel;  }

  
  @Override
  public void received( BlockEvent event )
  {
    String channelId   = null;
    long   blockSeqNum = 0;
    try
    {
      channelId   = event.getChannelId();
      blockSeqNum = event.getBlockNumber();

      System.out.println( "Block Listener received block num " + blockSeqNum );      

      logger.info( "Received block # " + blockSeqNum + " from channel " + channelId );
      blockEventProcessor.processBlockTransactions( event );
    }
    catch( InvalidProtocolBufferException e )
    {
      logger.error( "Protobuf Error receiving block event on channel = " + channelId + ". Error = " + e.getMessage() + ". Block not processed." );

      blockEventProcessor.listenerError( channelName, this, event, e );
    }
  }
  
  
}
