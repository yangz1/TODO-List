import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel{
	
	private String[] columnNames = {"Tasks","Delete", "Time", "Checkoff"};
	private Object[][] data = {};

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
	
	public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 1) {
            return false;
        } else {
            return true;
        }
    }
	
	public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }

}
