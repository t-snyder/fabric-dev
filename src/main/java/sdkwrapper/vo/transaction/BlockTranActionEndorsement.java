package sdkwrapper.vo.transaction;

/**
 * Represents an Endorsement of a particular transaction or Transaction Action if the transaction sent to the
 * orderer is an aggregate transaction.
 */
public class BlockTranActionEndorsement
{
  private byte[] endorserCert = null;
  private byte[] signature    = null;
  
  public byte[] getEndorserCert() { return endorserCert; }
  public byte[] getSignature()    { return signature;    }
  
  public void setEndorserCert( byte[] endorserCert ) { this.endorserCert = endorserCert; }
  public void setSignature(    byte[] signature    ) { this.signature    = signature;    }
}
