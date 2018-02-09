package sdkwrapper.vo.config;

import java.io.Serializable;
import java.security.PrivateKey;
import java.util.HashSet;
import java.util.Set;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

public class OrgUserVO implements User, Serializable
{
  private static final long serialVersionUID = 2764248946366184717L;

  private String      name = null;
  private Set<String> roles       = new HashSet<String>();
  private String      account     = null;
  private String      affiliation = null;
  private String      mspId       = null;
  
  private PrivateKey  enrollPrivKey = null;  // 
  private String      enrollCert    = null;
  private String      enrollPubKey  = null;  // Not Used
 
  public String      getName()          { return name;          }
  public Set<String> getRoles()         { return roles;         }
  public String      getAccount()       { return account;       }
  public String      getAffiliation()   { return affiliation;   }
  public String      getMspId()         { return mspId;         }
  public PrivateKey  getEnrollPrivKey() { return enrollPrivKey; }
  public String      getEnrollCert()    { return enrollCert;    }
  public String      getEnrollPubKey()  { return enrollPubKey;  }
  public String      getMSPID()         { return mspId;         }
  
  public void setName(          String      name          ) { this.name          = name;          }
  public void setRoles(         Set<String> roles         ) { this.roles         = roles;         }
  public void setAccount(       String      account       ) { this.account       = account;       }
  public void setAffiliation(   String      affiliation   ) { this.affiliation   = affiliation;   }
  public void setMspId(         String      mspId         ) { this.mspId         = mspId;         }
  public void setEnrollPrivKey( PrivateKey  enrollPrivKey ) { this.enrollPrivKey = enrollPrivKey; }
  public void setEnrollCert(    String      enrollCert    ) { this.enrollCert    = enrollCert;    }
  public void setEnrollPubKey(  String      enrollPubKey  ) { this.enrollPubKey  = enrollPubKey;  } 
  
  public Enrollment getEnrollment()
  {
    return new UserEnrollment( enrollPrivKey, enrollPubKey, enrollCert );
  }
 
  
  public class UserEnrollment implements Enrollment
  {
    private PrivateKey key        = null;
    private String     publicKey  = null;
    private String     cert       = null;
    
    public UserEnrollment( PrivateKey key, String publicKey, String certificate )
    {
      this.key        = key;
      this.publicKey  = publicKey;
      this.cert       = certificate;
    }
    
    public PrivateKey getKey()       { return key;        }
    public String     getPublicKey() { return publicKey;  }
    public String     getCert()      { return cert;       }
  }
}
