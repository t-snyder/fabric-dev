package sdkwrapper.events;

import org.hyperledger.fabric.sdk.BlockEvent;

import com.google.protobuf.InvalidProtocolBufferException;

import sdkwrapper.block.listener.FabricBlockListener;

/**
 * Interface for implementing a Block Event Processor. An application should only require one
 * implementation of this processor interface.
 * 
 * @author tim
 *
 */
public interface BlockEventProcessorIF
{
  /**
   * Process the BlockEvent and return the list of transactions contained within the Block.
   * @param blockEvent
   * @return
   */
  public void processBlockTransactions( BlockEvent blockEvent );
  
  /**
   * If the listener encounters a Protobuf error then let the processor know so it can respond to the error condition as
   * the listener does not have that capability.
   * 
   * @param listener
   * @param blockEvent
   * @param e
   */
  public void listenerError( String channelName, FabricBlockListener listener, BlockEvent blockEvent, InvalidProtocolBufferException e );
}
