package sdkwrapper.fabric.policy;

import java.util.Collection;

import org.hyperledger.fabric.sdk.ProposalResponse;

public interface EndorsementPolicyIF
{

  public boolean isPolicyMet(Collection<ProposalResponse> responses);

  public Collection<ProposalResponse> getFailedResponses();

  public Collection<ProposalResponse> getSuccessfulResponses();
}