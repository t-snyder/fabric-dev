package sdkwrapper.error;

/**
 * Interface which all the Error Commands loaded and processed by the ErrorController need to support.
 * 
 * @author tim
 *
 */
public interface ErrorCommandIF
{
  /**
   * Used within the ErrorController to load information to the Command.
   * 
   * @param errCode
   * @param description
   */
  public void loadError( int errCode, String description );

  /**
   * Called in order to process the error outcome.
   */
  public void processError();
}
