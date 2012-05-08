package to.sindica.ahpm.tc.download.manager;

import junit.framework.Assert;
import org.fest.assertions.FileAssert;
import org.fest.swing.timing.Condition;
import org.fest.swing.timing.Pause;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: domix
 * Date: 07/05/12
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class DownloadTests {
  URL url;
  String file = "http://media.clickonero.com/mx/deals-2012/2012_05_07_libellule/libelule-deal.jpg";

  @Before
  public void setup() throws MalformedURLException {
    url = new URL(file);
  }

  @Test
  public void shouldDownloadAFile() throws MalformedURLException {
    Download download = new Download(url);

    waitUntilDownloadIsDone(download);
    download.getSize();
    download.getProgress();
    Assert.assertEquals(Download.COMPLETE, download.getStatus());
    Assert.assertEquals(file, download.getUrl());
  }

  @Test
  public void shouldResume() throws MalformedURLException {
    Download download = new Download(url);

    download.pause();
    Pause.pause(100);
    download.resume();

    waitUntilDownloadIsDone(download);
    Assert.assertEquals(Download.COMPLETE, download.getStatus());
  }

  @Test
  public void shouldDownloadFileAndPutInADesiredPlace() throws IOException {
    Download download = new Download(url, "build");
    waitUntilDownloadIsDone(download);
    Assert.assertEquals(Download.COMPLETE, download.getStatus());

    Assert.assertTrue(download.getDestinationFile().exists());
  }

  @Test
  public void shouldCancel() throws MalformedURLException {
    Download download = new Download(url);
    download.cancel();
    Assert.assertEquals(Download.CANCELLED, download.getStatus());
  }

  void waitUntilDownloadIsDone(final Download download) {
    Pause.pause(new Condition("Wait until download is complete.") {

      @Override
      public boolean test() {
        return download.getStatus() == Download.COMPLETE;
      }
    }, 1000 * 60 * 2);
  }
}
