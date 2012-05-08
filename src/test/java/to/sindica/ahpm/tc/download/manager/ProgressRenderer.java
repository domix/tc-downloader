package to.sindica.ahpm.tc.download.manager;

/**
 * Created with IntelliJ IDEA.
 * User: domix
 * Date: 07/05/12
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

// This class renders a JProgressBar in a table cell.
class ProgressRenderer extends JProgressBar
    implements TableCellRenderer {

  // Constructor for ProgressRenderer.
  public ProgressRenderer(int min, int max) {
    super(min, max);
  }

  /* Returns this JProgressBar as the renderer
for the given table cell. */
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                 int row, int column) {
    // Set JProgressBar's percent complete value.
    setValue((int) ((Float) value).floatValue());
    return this;
  }
}
