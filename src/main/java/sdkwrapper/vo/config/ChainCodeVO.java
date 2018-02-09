package sdkwrapper.vo.config;

import java.io.Serializable;

import sdkwrapper.fabric.policy.EndorsementPolicyIF;

public class ChainCodeVO implements Serializable
{
  private static final long serialVersionUID = -8155017761390516371L;

  private String chainCodeName = null;
  private String chainCodeVersion = null;
  private String chainCodePath    = null;
  private String endorsementPolicyName = null;
  
  // Transient
  private EndorsementPolicyIF policy = null;

  public String getChainCodeName()         { return chainCodeName;         }
  public String getChainCodeVersion()      { return chainCodeVersion;      }
  public String getChainCodePath()         { return chainCodePath;         }
  public String getEndorsementPolicyName() { return endorsementPolicyName; }

  public void setChainCodeName(         String chainCodeName         ) { this.chainCodeName         = chainCodeName;         }
  public void setChainCodeVersion(      String chainCodeVersion      ) { this.chainCodeVersion      = chainCodeVersion;      }
  public void setChainCodePath(         String chainCodePath         ) { this.chainCodePath         = chainCodePath;         }
  public void setEndorsementPolicyName( String endorsementPolicyName ) { this.endorsementPolicyName = endorsementPolicyName; }
  
  public EndorsementPolicyIF getPolicy()   
  { 
    return policy; 
  }
}
