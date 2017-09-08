import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.PanelUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JTable;

public class TODOList {

	private JFrame frame;
	private JTextField textField;
	private JTextField timeField;
	private JTextField importField;
	
	private LinkedList<String> myList;
	private LinkedList<String> myTime;
	private JPanel panel_1;
	private JButton btnReset;
	private JTable table;
	private JButton enterTime;
	private Object selectedItem;
	private JButton btnExport;
	private JButton btnImport;
	private JButton btnRemoveTask;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TODOList window = new TODOList();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public TODOList() {
		initialize();
	}

	private void initialize() {
		myList = new LinkedList<>();
		myTime = new LinkedList<>();
		frame = new JFrame();
		frame.setTitle("TODO List");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel buttonPane = new JPanel();
		frame.getContentPane().add(buttonPane, BorderLayout.EAST);
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));
		
		timeField = new JTextField();
		timeField.setMaximumSize(new Dimension(200, 30));
		buttonPane.add(timeField);
		
		enterTime = new JButton("Enter Time");
		enterTime.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int currentRow = table.getSelectedRow();
				DefaultTableModel model = (DefaultTableModel) table.getModel(); 
				model.setValueAt(timeField.getText(), currentRow, 1);
				model.setValueAt(new Boolean(true), currentRow, 2);
				myTime.set(currentRow, timeField.getText());
				timeField.setText("");
			}
		});
		buttonPane.add(enterTime);
		
		btnExport = new JButton("Export");
		btnExport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Writer output = null;
				File file = new File("Export.txt");
				try {
					output = new BufferedWriter(new FileWriter(file)); 
					String encoded = URLEncoder.encode(listParser(myList, myTime), "UTF-8");		  
					System.out.println(encoded);
					output.write(encoded);
					output.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		buttonPane.add(btnExport);
		
		importField = new JTextField();
		importField.setMaximumSize(new Dimension(200, 30));
		buttonPane.add(importField);
		
		btnImport = new JButton("Import");
		btnImport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					byte[] content = Files.readAllBytes(Paths.get(importField.getText()));
					String input = new String(content,"UTF-8");
					String decoded = URLDecoder.decode(input, "UTF-8");
					System.out.println("Mydecode: "+decoded);
					importField.setText("");
					importParse(decoded);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		buttonPane.add(btnImport);
		
		btnRemoveTask = new JButton("Remove Task");
		btnRemoveTask.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int currentRow = table.getSelectedRow();
				DefaultTableModel model = (DefaultTableModel) table.getModel(); 
				model.removeRow(currentRow);
				myList.remove(currentRow);
				myTime.remove(currentRow);
			}
		});
		buttonPane.add(btnRemoveTask);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
//To add a row: DefaultTableModel model = (DefaultTableModel) table.getModel(); model.addRow(new Object[]{"Column 1", "Column 2", "Column 3"}); You can also remove rows with this method.Aug 23, 2010
		//Create table for showing data and checking off
		String[] colname = {"Task","Time","Checkoff"};
		Object[][] data = {};
		table = new JTable(new DefaultTableModel(data,colname){
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					// TODO Auto-generated method stub
					return getValueAt(0, columnIndex).getClass();
				}
		});
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		frame.getContentPane().add(scrollPane,BorderLayout.CENTER);
		
		//Textfield for adding task
		textField = new JTextField();
		textField.setColumns(10);
		panel.add(textField);
		
		//Add task button
		JButton btnAddATask = new JButton("Add a task");
		btnAddATask.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				myList.add(textField.getText());
				myTime.add("Incomplete");
				DefaultTableModel model = (DefaultTableModel) table.getModel(); 
				model.addRow(new Object[]{textField.getText(), "Incomplete", new Boolean(false)}); 
				textField.setText("");
			}
		});
		panel.add(btnAddATask);
		
		//Reset button
		btnReset = new JButton("Reset");
		btnReset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				myList = new LinkedList<>(); 
				DefaultTableModel model = (DefaultTableModel) table.getModel(); 
				model.setRowCount(0);
			}
		});
		panel.add(btnReset);
		
	}
	
	private String listParser(LinkedList<String> task, LinkedList<String> time) {
		String output = "<html>";
		for (int i = 0; i < task.size(); i++){
			output = output + time.get(i) +" "+task.get(i) + "<br>";
		}
		output = output+"</html>";
		System.out.println("Here is list parser: "+output);
		return output;
	}
	
	private void importParse(String str){
		String myStr = str.substring(6,str.length()-7);
		String[] myStrArr = myStr.split("<br>");
		myList = new LinkedList<>();
		myTime = new LinkedList<>();
		for(String s : myStrArr){
			int cur = 0;
			for(int i = 0; i <s.length();i++){
				if(s.charAt(i) == ' '){
					cur = i;
					break;
				}
			}
			myTime.add(s.substring(0, cur));
			myList.add(s.substring(cur));
		}

		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for(int i = 0; i < myList.size(); i++){ 
			model.addRow(new Object[]{myList.get(i), myTime.get(i), new Boolean(true)}); 
			
		}
	}

}
