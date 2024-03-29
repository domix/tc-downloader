package to.sindica.ahpm.tc.download.manager;

/**
 * Created with IntelliJ IDEA.
 * User: domix
 * Date: 07/05/12
 * Time: 12:55
 * To change this template use File | Settings | File Templates.
 */
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

// This class downloads a file from a URL.
public class Download extends Observable implements Runnable {

  // Max size of download buffer.
  private static final int MAX_BUFFER_SIZE = 1024;

  private URL url; // download URL
  private int size; // size of download in bytes
  private int downloaded; // number of bytes downloaded
  private Status status; // current status of download
  private String destination = "";
  private File destinationFile = null;

  // Constructor for Download.
  public Download(URL url) {
    this.url = url;
    size = -1;
    downloaded = 0;
    status = Status.DOWNLOADING;

    // Begin the download.
    download();
  }

  public Download(URL url, String destination) {
    this.destination = destination;
    this.url = url;
    size = -1;
    downloaded = 0;
    status = Status.DOWNLOADING;

    // Begin the download.
    download();
  }

  // Get this download's URL.
  public String getUrl() {
    return url.toString();
  }

  // Get this download's size.
  public int getSize() {
    return size;
  }

  // Get this download's progress.
  public float getProgress() {
    return ((float) downloaded / size) * 100;
  }

  // Get this download's status.
  public Status getStatus() {
    return status;
  }

  // Pause this download.
  public void pause() {
    status = Status.PAUSED;
    stateChanged();
  }

  // Resume this download.
  public void resume() {
    status = Status.DOWNLOADING;
    stateChanged();
    download();
  }

  // Cancel this download.
  public void cancel() {
    status = Status.CANCELLED;
    stateChanged();
  }

  // Mark this download as having an error.
  private void error() {
    status = Status.ERROR;
    stateChanged();
  }

  // Start or resume downloading.
  private void download() {
    Thread thread = new Thread(this);
    thread.start();
  }

  // Get file name portion of URL.
  private String getFileName(URL url) {
    String fileName = url.getFile();
    return fileName.substring(fileName.lastIndexOf('/') + 1);
  }

  public File getDestinationFile() {
    return destinationFile;
  }

  // Download file.
  public void run() {
    RandomAccessFile file = null;
    InputStream stream = null;

    try {
      // Open connection to URL.
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      // Specify what portion of file to download.
      connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
      //connection.add

      // Connect to server.
      connection.connect();

      // Make sure response code is in the 200 range.
      if (connection.getResponseCode() / 100 != 2) {
        error();
      }

      // Check for valid content length.
      int contentLength = connection.getContentLength();
      if (contentLength < 1) {
        error();
      }

      /* Set the size for this download if it hasn't been already set. */
      if (size == -1) {
        size = contentLength;
        stateChanged();
      }

      if(destination.length() > 0 && !destination.endsWith("/")) {
        destination = destination + "/";
      }

      destinationFile = new File(destination + getFileName(url));
      // Open file and seek to the end of it.
      file = new RandomAccessFile(destinationFile, "rw");
      file.seek(downloaded);

      stream = connection.getInputStream();
      while (status == Status.DOWNLOADING) {
        /* Size buffer according to how much of the
           file is left to download. */
        byte buffer[];
        if (size - downloaded > MAX_BUFFER_SIZE) {
          buffer = new byte[MAX_BUFFER_SIZE];
        } else {
          buffer = new byte[size - downloaded];
        }

        // Read from server into buffer.
        int read = stream.read(buffer);
        if (read == -1)
          break;

        // Write buffer to file.
        file.write(buffer, 0, read);
        downloaded += read;
        stateChanged();
      }

      /* Change status to complete if this point was reached because downloading has finished. */
      if (status == Status.DOWNLOADING) {
        status = Status.COMPLETE;
        stateChanged();
      }
    } catch (Exception e) {
      error();
    } finally {
      // Close file.
      if (file != null) {
        try {
          file.close();
        } catch (Exception e) {
        }
      }

      // Close connection to server.
      if (stream != null) {
        try {
          stream.close();
        } catch (Exception e) {
        }
      }
    }
  }

  // Notify observers that this download's status has changed.
  private void stateChanged() {
    setChanged();
    notifyObservers();
  }
}