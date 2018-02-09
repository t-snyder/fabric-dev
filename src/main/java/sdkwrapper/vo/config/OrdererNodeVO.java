package sdkwrapper.vo.config;

import java.io.Serializable;

import org.hyperledger.fabric.sdk.Orderer;

import sdkwrapper.exceptions.ConfigurationException;

/**
 * Represents a persisted Fabric Orderer which has been associated with a particular Chain / Channel.
 * The transient attribute 'sdkOrderer' is loaded when a new 'Orderer' object is created by invoking
 * Fabric SDK functionality.
 * 
 * @author tim
 *
 */
public class OrdererNodeVO implements Serializable
{
  private static final long serialVersionUID = 4107160448157904297L;

  private String  nodeId          = null;
  private String  orgId           = null;
  private String  ipAddress       = null;
  private String  ordererPort     = null;
  private String  tlsCertificate  = null;
  private boolean trustServerCert = false;
  private String  sslProvider     = "openSSL";
  private String  negotiationType = "TLS";
 
  // Transient attributes
  private Orderer sdkOrderer      = null;
  
  public String  getNodeId()          { return nodeId;          }
  public String  getOrgId()           { return orgId;           }
  public String  getIpAddress()       { return ipAddress;       }
  public String  getOrdererPort()     { return ordererPort;     }
  public String  getTlsCertificate()  { return tlsCertificate;  }
  public boolean isTrustServerCert()  { return trustServerCert; }
  public String  getSslProvider()     { return sslProvider;     }
  public String  getNegotiationType() { return negotiationType; }
  public Orderer getSdkOrderer()      { return sdkOrderer;      }
  
  public void setNodeId(          String  nodeId          ) { this.nodeId          = nodeId;          }
  public void setOrgId(           String  orgId           ) { this.orgId           = orgId;           }
  public void setIpAddress(       String  ipAddress       ) { this.ipAddress       = ipAddress;       }
  public void setOrdererPort(     String  ordererPort     ) { this.ordererPort     = ordererPort;     }
  public void setTlsCertificate(  String  tlsCertificate  ) { this.tlsCertificate  = tlsCertificate;  }
  public void setTrustServerCert( boolean trustServerCert ) { this.trustServerCert = trustServerCert; }
  public void setSslProvider(     String  sslProvider     ) { this.sslProvider     = sslProvider;     }
  public void setNegotiationType( String  negotiationType ) { this.negotiationType = negotiationType; }
  public void setSdkOrderer(      Orderer orderer         ) { this.sdkOrderer      = orderer;         }
  
  
  /**
   * Returns the configured Orderer URL in the format (without the brackets):
   *   <peerId>@grpc://<ipAddress>:<endorsePort>
   * @return
   * @throws ConfigurationException
   */
  public String getOrdererUrl()
   throws ConfigurationException
  {
    if( nodeId      == null ) throw new ConfigurationException( "Node Id is not set"      );
    if( ipAddress   == null ) throw new ConfigurationException( "IP Address is not set"   );
    if( ordererPort == null ) throw new ConfigurationException( "Orderer Port is not set" );
    
//    return nodeId + "@grpc://" + ipAddress + ":" + ordererPort;
    System.out.println( "Orderer URL for " + nodeId + " = grpc://" + ipAddress + ":" + ordererPort );
    return "grpc://" + ipAddress + ":" + ordererPort;
  }
}
