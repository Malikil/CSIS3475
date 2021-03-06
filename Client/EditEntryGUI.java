package Client;

import Server.Column;
import Server.Entry;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;

public class EditEntryGUI extends JDialog
{
	private JPanel panel;
	
	private JTextField[] newData;

	public String[] getData()
	{
		if (newData == null)
			return null;
		String[] temp = new String[newData.length];
		for (int i = 0; i < newData.length; i++)
			temp[i] = newData[i].getText();
		return temp;
	}

	/**
	 * Create the application.
	 * @param fields The names of the fields to display
	 * @param entry The entry to edit
	 */
	public EditEntryGUI(String[] fields, Entry entry)
	{
		initialize();
		setTitle("Edit Entry");
		newData = new JTextField[fields.length];
		for (int i = 0; i < fields.length; i++)
		{
			JLabel label = new JLabel(fields[i]);
			label.setBounds(10, i * 25 + 11, 90, 14);
			panel.add(label);
			JTextField newField = new JTextField();
			newField.setText(entry.getField(i) != null ?
							 entry.getField(i).toString() :
							 "");
			newData[i] = newField;
			newData[i].setBounds(110, i * 25 + 8, 120, 20);
			panel.add(newData[i]);
		}
		panel.setPreferredSize(new Dimension(0, fields.length * 25 + 11));
	}
	
	public EditEntryGUI(String[] fields)
	{
		initialize();
		setTitle("Create Entry");
		//edit = null;
		newData = new JTextField[fields.length];
		for (int i = 0; i < fields.length; i++)
		{
			JLabel label = new JLabel(fields[i]);
			label.setBounds(10, i * 25 + 11, 90, 14);
			panel.add(label);
			newData[i] = new JTextField("");
			newData[i].setBounds(110, i * 25 + 8, 120, 20);
			panel.add(newData[i]);
		}
		panel.setPreferredSize(new Dimension(0, fields.length * 25 + 11));
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		JDialog thisDialog = this;
		setResizable(false);
		setBounds(100, 100, 325, 282);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() { //exit
			@Override
			public void windowClosing(WindowEvent w)
			{
				newData = null;
				thisDialog.dispose();
			}
		});
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		getContentPane().setLayout(null);
		
		panel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.setLayout(null);
		
		scrollPane.setBounds(10, 10, 300, 200);
		getContentPane().add(scrollPane);
		
		JButton btnCommit = new JButton("Commit");
		btnCommit.setBounds(20, 221, 90, 23);
		btnCommit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				thisDialog.dispose();
			}
		});
		getContentPane().add(btnCommit);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(210, 221, 90, 23);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				newData = null;
				thisDialog.dispose();
			}
		});
		getContentPane().add(btnCancel);
	}
}
