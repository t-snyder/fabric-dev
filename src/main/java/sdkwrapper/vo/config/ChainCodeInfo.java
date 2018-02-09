package sdkwrapper.vo.config;

import java.io.Serializable;

import sdkwrapper.exceptions.ConfigurationException;
import sdkwrapper.fabric.policy.EndorsementPolicyIF;

public class ChainCodeInfo implements Serializable
{
  private static final long serialVersionUID = 4315011046736477263L;

  private ChainCodeVO         chainCodeVO = null;
  private EndorsementPolicyIF policy      = null;
  private String              functName   = null;
  
  public ChainCodeInfo( ChainCodeVO chainCode, EndorsementPolicyIF policy )
   throws ConfigurationException
  {
    if( chainCode == null ) throw new ConfigurationException( "Chain attribute is required" );
    if( policy    == null ) throw new ConfigurationException( "Endorsement Policy is required" );
    
    this.chainCodeVO = chainCode;
    this.policy      = policy;
  }
  
  /**
   * Preclude default constructor
   */
  @SuppressWarnings( "unused" )
  private ChainCodeInfo()
  {
  }
  
  public String getChainCodeName()         { return chainCodeVO.getChainCodeName();         }
  public String getChainCodeVersion()      { return chainCodeVO.getChainCodeVersion();      }
  public String getChainCodePath()         { return chainCodeVO.getChainCodePath();         }
  public String getEndorsementPolicyName() { return chainCodeVO.getEndorsementPolicyName(); }
  
  public EndorsementPolicyIF getPolicy()    { return policy;    }
  public String              getFunctName() { return functName; }
  
  public void setFunctName( String name ) { this.functName = name; }
}
