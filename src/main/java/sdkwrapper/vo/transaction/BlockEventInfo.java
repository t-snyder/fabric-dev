package sdkwrapper.vo.transaction;

import java.util.List;

public class BlockEventInfo
{
  private long                   blockSeqNum     = 0;
  private String                 channelId       = null;
  private String                 blockDataHash   = null;
  private String                 blockPriorHash  = null;
  private List<BlockTransaction> transactionList = null;
 
  public long                   getBlockSeqNum()     { return blockSeqNum;     }
  public String                 getChannelId()       { return channelId;       }
  public String                 getBlockDataHash()   { return blockDataHash;   }
  public String                 getBlockPriorHash()  { return blockPriorHash;  }
  public List<BlockTransaction> getTransactionList() { return transactionList; }
  
  public void setBlockSeqNum(     long                   blockSeqNum     ) { this.blockSeqNum     = blockSeqNum;     }
  public void setChannelId(       String                 channelId       ) { this.channelId       = channelId;       }
  public void setBlockDataHash(   String                 blockDataHash   ) { this.blockDataHash   = blockDataHash;   }
  public void setBlockPriorHash(  String                 blockPriorHash  ) { this.blockPriorHash  = blockPriorHash;  }
  public void setTransactionList( List<BlockTransaction> transactionList ) { this.transactionList = transactionList; }
  
}
