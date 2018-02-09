package sdkwrapper.fabric.policy;

import java.util.Collection;
import java.util.HashSet;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

/**
 * This implements a stakeholder Endorsement Policy where at least 2 stakeholders must successfully endorse
 * and no stakeholder can reject the endorsement.
 * 
 * @author tim
 *
 */
public class StakeholderEndorsementPolicy implements EndorsementPolicyIF
{
  private static final int MINIMUM_SUCCESS = 2;
  
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
