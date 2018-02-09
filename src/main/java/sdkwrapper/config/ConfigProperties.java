package sdkwrapper.config;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * ConfigProperties provides centralized access to the config.properties
 * properties file located within the zensa_config project. It is expected that
 * this properties file will contain the setup properties for the pipeline task
 * processing.
 * 
 * When running via eclipse the app.config.path should be set in the jvm
 */
public class ConfigProperties
{
  private static final Logger logger = LogManager.getLogger( ConfigProperties.class );

  private Properties props = new Properties();

  public ConfigProperties( String configPath )
  {
    InputStream inputStream = null;
    
    try
    {
      inputStream = ConfigProperties.class.getClassLoader().getResourceAsStream( configPath );

      if( inputStream == null )
        inputStream = new FileInputStream( new File( configPath ));

      props.load( inputStream );
    } 
    catch( IOException ex )
    {
      String errMsg = "Initializing properties found error: " + ex.getMessage();
      logger.error(  errMsg );
    } 
    finally
    {
      try
      {
        if( inputStream != null )
          inputStream.close();
      } 
      catch( IOException e )
      {
        // Do nothing
      }
    }
  }

  public Properties getProperties()
  {
    return props;
  }

  public String getProperty( String key )
  {
    if( props != null )
      return props.getProperty( key );
    
    return null;
  }

  public boolean hasProperty( String key )
  {
    if( props != null )
      return props.containsKey( key );
    
    return false;
  }

}