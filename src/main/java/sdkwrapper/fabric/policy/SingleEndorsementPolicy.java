package sdkwrapper.fabric.policy;

import java.util.Collection;
import java.util.HashSet;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

/**
 * This implements an Endorsement Policy where only 1 Peer needs to successfully endorse the transaction.
 * However, no Peer can reject it.
 * 
 * @author tim
 *
 */
public class SingleEndorsementPolicy implements EndorsementPolicyIF
{
  private static final int MINIMUM_SUCCESS = 1;
  
  private Collection<ProposalResponse> failedResponses  = new HashSet<ProposalResponse>();
  private Collection<ProposalResponse> successResponses = new HashSet<ProposalResponse>();
  
  
  public boolean isPolicyMet(Collection<ProposalResponse> responses)
  {
    failedResponses.clear();
    successResponses.clear();
    
    try
    {
      for( ProposalResponse response : responses )
      {
        int returnCode = response.getChaincodeActionResponseStatus();
        if( returnCode >= 200 && returnCode < 500 )
        {
          successResponses.add( response );
        }
        else if( returnCode >= 500 )
        {
          failedResponses.add( response );
        }
      }
    }
    catch( InvalidArgumentException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // If any failed response returned endorsement fails.
    if( !failedResponses.isEmpty() )
      return false;

    // Determine if there is a minimum of a single successful response 
    if( successResponses.size() >= MINIMUM_SUCCESS )
      return true;
    
    return false;
  }

  public Collection<ProposalResponse> getFailedResponses()
  {
    return failedResponses;
  }

  public Collection<ProposalResponse> getSuccessfulResponses()
  {
    return successResponses;
  }

}
