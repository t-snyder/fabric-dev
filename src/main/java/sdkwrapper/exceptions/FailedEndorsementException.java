package sdkwrapper.exceptions;

import java.util.Collection;

import org.hyperledger.fabric.sdk.ProposalResponse;


public class FailedEndorsementException extends Exception
{
  private static final long serialVersionUID = 7634791921482044094L;

  private Collection<ProposalResponse> failedResponses = null;
  
  public FailedEndorsementException( Collection<ProposalResponse> failed )
  {
    this.failedResponses = failed;
  }
 
  public FailedEndorsementException( String errMsg )
  {
	super( errMsg );  
  }
  
  public Collection<ProposalResponse> getFailedResponses() { return failedResponses; }
}
