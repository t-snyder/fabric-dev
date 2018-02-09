package sdkwrapper.vo.config;

import java.io.Serializable;

public class OrgContextVO implements Serializable
{
  private static final long serialVersionUID = -563707677320006909L;
 
  private String orgId   = null;
  private String orgName = null;
  private String mspId   = null;
  private String mspPath = null;
  
  public String getOrgId()   { return orgId;   }
  public String getOrgName() { return orgName; }
  public String getMspId()   { return mspId;   }
  public String getMspPath() { return mspPath; }
  
  public void setOrgId(   String orgId   ) { this.orgId   = orgId;   }
  public void setOrgName( String orgName ) { this.orgName = orgName; }
  public void setMspId(   String mspId   ) { this.mspId   = mspId;   }
  public void setMspPath( String mspPath ) { this.mspPath = mspPath; }
}
