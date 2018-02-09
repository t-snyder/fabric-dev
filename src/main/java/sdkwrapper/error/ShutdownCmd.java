package sdkwrapper.error;

public class ShutdownCmd implements ErrorCommandIF
{
  private int    errCode     = 0;
  private String description = null;

  @Override
  public void loadError(int errCode, String description)
  {
    this.errCode     = errCode;
    this.description = description;
  }

  @Override
  public void processError()
  {
    // Do logging here to indicate the Business Services are being shutdown and the reason.

    // Should do this a little nicer.
    System.exit( -1 );
  }

}
