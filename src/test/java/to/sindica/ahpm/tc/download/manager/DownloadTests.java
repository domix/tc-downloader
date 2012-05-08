package to.sindica.ahpm.tc.download.manager;

import junit.framework.Assert;
import org.fest.swing.timing.Condition;
import org.fest.swing.timing.Pause;
import org.junit.Before;
import org.junit.Test;

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
  public static final AtomicInteger counter = new AtomicInteger(0);

  @Before
  public void setup() throws MalformedURLException {
    url = new URL(file);
    counter.set(0);
  }

  @Test
  public void shouldDownloadAFile() throws MalformedURLException {
    Download download = new Download(url);
    download.addObserver(new DownloadObserver());

    waitUntilDownloadIsDone();
    download.getSize();
    download.getProgress();
    Assert.assertEquals(Download.COMPLETE, download.getStatus());
    Assert.assertEquals(file, download.getUrl());
  }

  @Test
  public void shouldResume() throws MalformedURLException {
    Download download = new Download(url);

    download.addObserver(new DownloadObserver());

    download.pause();
    Pause.pause(100);
    download.resume();

    waitUntilDownloadIsDone();
    Assert.assertEquals(Download.COMPLETE, download.getStatus());
  }

  @Test
  public void shouldCancel() throws MalformedURLException {
    Download download = new Download(url);
    download.cancel();
    Assert.assertEquals(Download.CANCELLED, download.getStatus());
  }

  void waitUntilDownloadIsDone() {
    Pause.pause(new Condition("Wait until download is done.") {

      @Override
      public boolean test() {
        return counter.get() > 0;
      }
    }, 1000 * 60 * 2);
  }
}

class DownloadObserver implements Observer {
  public void update(Observable observable, Object o) {
    if (observable instanceof Download) {
      Download d = (Download) observable;
      String status = Download.STATUSES[d.getStatus()];
      System.out.println(status);

      if (Download.STATUSES[2].equals(status)) {
        DownloadTests.counter.incrementAndGet();
      }
    }
  }
}
