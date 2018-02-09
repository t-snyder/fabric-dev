package sdkwrapper.events;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.BlockInfo.EndorserInfo;
import org.hyperledger.fabric.sdk.BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.InvalidProtocolBufferException;

import sdkwrapper.block.listener.FabricBlockListener;
import sdkwrapper.runtimemgr.RuntimeMgrIF;
import sdkwrapper.vo.transaction.BlockEventInfo;
import sdkwrapper.vo.transaction.BlockTranActionEndorsement;
import sdkwrapper.vo.transaction.BlockTransaction;
import sdkwrapper.vo.transaction.BlockTransactionAction;


public class BlockEventProcessorImpl implements BlockEventProcessorIF
{
  private static Logger logger = LogManager.getLogger( BlockEventProcessorImpl.class );

  private RuntimeMgrIF runtimeMgr = null;
  
  /**
   * Public constructor requiring the loading of the ErrorController.
   * 
   * @param controller
   */
  public BlockEventProcessorImpl( RuntimeMgrIF runtime )
  {
    runtimeMgr = runtime;
  }

  /**
   * Not allowed constructor
   */
  @SuppressWarnings( "unused" )
  private BlockEventProcessorImpl()
  {
  }
  
  @Override
  public void processBlockTransactions( BlockEvent blockEvent )
  {
    BlockEventInfo blockInfo = new BlockEventInfo();

    try
    {
      logger.info( "Starting Block Event processing for block # " + blockEvent.getBlockNumber() + " on channel id = " + blockEvent.getChannelId() );   

      blockInfo.setBlockSeqNum(    blockEvent.getBlockNumber()                        );
      blockInfo.setChannelId(      blockEvent.getChannelId()                          );
      blockInfo.setBlockDataHash(  Hex.encodeHexString( blockEvent.getDataHash()     ));
      blockInfo.setBlockPriorHash( Hex.encodeHexString( blockEvent.getPreviousHash() ));

      // We need to determine if the block seq number is the next block sequence number to process. If it is not
      // then we need to process all block events prior starting from the last block event successfully processed.
      long lastSeqNumber = obtainLastChannelSeqNumber( blockInfo.getChannelId() );
      
      List<BlockTransaction> transactionList = new ArrayList<BlockTransaction>();
      blockInfo.setTransactionList( transactionList );
      
      Iterable<TransactionEvent> blockTranIter = blockEvent.getTransactionEvents();
      List<TransactionEvent>     tranEventList = ImmutableList.copyOf( blockTranIter );
      
      for( TransactionEvent tranEvent : tranEventList )
      {
        BlockTransaction tran = new BlockTransaction();
        blockInfo.getTransactionList().add( tran );
        
        tran.setBlockSeqNum(   blockInfo.getBlockSeqNum()   );
        tran.setTranType(      tranEvent.getType().name()   );
        tran.setTransactionId( tranEvent.getTransactionID() );
        tran.setTranTimestamp( tranEvent.getTimestamp()     );
        tran.setEpoch(         tranEvent.getEpoch()         );
        tran.setValid(         tranEvent.isValid()          );
        tran.setValidationCode( Byte.toString( tranEvent.getValidationCode() ));

        // We only want to process transactions which are of type transaction and not config.
        
        Iterable<TransactionActionInfo> blockTranInfoIter = tranEvent.getTransactionActionInfos();
        List<TransactionActionInfo>     blockTranInfoList = ImmutableList.copyOf( blockTranInfoIter );
        
        for( TransactionActionInfo tranInfo : blockTranInfoList )
        {
          BlockTransactionAction tranAction = new BlockTransactionAction();
          tran.getTranActions().add( tranAction );
          
          tranAction.setRequestPayload( new String( tranInfo.getChaincodeInputArgs( 0 )));
          
          int endorseCnt = tranInfo.getEndorsementsCount();
          if( endorseCnt > 0 )
          {
            for( int i = 0; i < endorseCnt; i++ )
            {
              EndorserInfo               eInfo       = tranInfo.getEndorsementInfo( i );
              BlockTranActionEndorsement endorsement = new BlockTranActionEndorsement();
              
              endorsement.setEndorserCert( eInfo.getEndorser() );
              endorsement.setSignature( eInfo.getSignature()   );
              
              tranAction.getEndorsements().add( endorsement );
            }
          }
          
          tranAction.setProposalResponseStatus(  tranInfo.getProposalResponseStatus()  );
          tranAction.setProposalResponsePayload( tranInfo.getProposalResponsePayload() );
          tranAction.setDescription(             tranInfo.getResponseMessage()         );
          tranAction.setResponseStatus(          tranInfo.getResponseStatus()          );
          
          TxReadWriteSetInfo rwSetInfo = tranInfo.getTxReadWriteSet();
          if( rwSetInfo != null && rwSetInfo.getNsRwsetCount() > 0 )
          {
            for( int i = 0; i < rwSetInfo.getNsRwsetCount(); i++ )
            {
              tranAction.getRwSet().add( rwSetInfo.getNsRwsetInfo( i ) );
            }
          }
        }
      }
      
      // We now have the block info and the transaction list. We need to process the transactions now.
      if( !blockInfo.getTransactionList().isEmpty() )
      {
        for( BlockTransaction tran : blockInfo.getTransactionList() )
        {
          if( !tran.getTranActions().isEmpty() )
          {
            for( BlockTransactionAction action : tran.getTranActions() )
            {
              runtimeMgr.sendResponseMsg( buildResponseMsg( action ));
            }
          }
        }
      }
    } catch( InvalidProtocolBufferException e )
      {
        // The block event did not get processed properly due to a transport exception. We need to deal with the 
        // exception prior to attempting to process the block event again.
        String errMsg = "ProtocolBufferException on block information. Error = " + e.getMessage();
        logger.error( errMsg );
        
        // TODO: attempt tp correct problem and reprocess.
      }
  }

  @Override
  public void listenerError( String channelName, FabricBlockListener listener, BlockEvent blockEvent, InvalidProtocolBufferException e )
  {
    
  }
  
  private long obtainLastChannelSeqNumber( String channelId )
  {
    
    return 0;
  }
  
  private String buildResponseMsg( BlockTransactionAction action )
  {
    return action.toJSON();
  }
}
