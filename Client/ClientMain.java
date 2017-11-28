package Client;

import java.awt.HeadlessException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import Server.AVLTree;
import Server.Column;
import Server.Command;
import Server.Entry;
import Server.Message;
import Server.Table;
import Server.User;

public class ClientMain implements Client
{
	private ObjectInputStream objIn;
	private ObjectOutputStream objOut;
	private ClientGUI gui;
	private Table currentTable = null;
	private String[] databaseList;
	
	public ClientMain(Socket sock, ObjectOutputStream out, ObjectInputStream in, boolean admin) throws IOException
	{
		objOut = out;
		objIn = in;
		gui = new ClientGUI(this, admin);
	}
	
	public static void main(String[] args)
	{
		LoginGUI login = new LoginGUI();
		Socket sock = null;
		ObjectOutputStream out;
		ObjectInputStream in;
		
		while (true)
		{
			login.setVisible(true);
			if (login.isCancelled()) return;
			try
			{
				sock = new Socket(login.getEnteredIP(), 8001); 
				out = new ObjectOutputStream(sock.getOutputStream()); 
				in = new ObjectInputStream(sock.getInputStream());
				Message loginAttempt = new Message(Command.LOGIN,
						new String[] { login.getEnteredUser(), login.getEnteredPass() });
				out.writeObject(loginAttempt);
				loginAttempt = (Message)in.readObject();
				Command conf = loginAttempt.getCommandType(); 
				if (conf == Command.CONNECTION_SUCCESS)
				{
					User user = loginAttempt.getUser();
					ClientMain client = new ClientMain(sock, out, in, user.isAdmin());
					
					client.setDatabaseList(user.getDatabases());
					client.start();
				}
				else
					login.setMessage(conf);
						
			}
			catch (IOException ex)
			{
				try
				{
					sock.close();
				}
				catch (IOException io)
				{
					// Couldn't close socket
				}
			}
			catch (ClassNotFoundException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void start()
	{
		gui.setVisible(true);
		try
		{
				while (true)
				{
					Message received = (Message)objIn.readObject();
					switch (received.getCommandType())
					{
					case ADD_COLUMNS:
						Column[] columnList = received.getColumns();
						for(int i = 0 ; i < columnList.length ; i++)
							currentTable.addColumn(columnList[i]);
						setTable(currentTable);
						break;
					case ADD_ENTRY:
						 currentTable.addEntry(received.getEntry());
						 setTable(currentTable);
						break;
					case ADD_TABLE:
						gui.addTableName(received.getTableName());
						break;
					case DELETE_TABLE:
						setTable(null);
						break;
					case DELETE_COLUMN:
						currentTable.removeColumn(received.getColumnIndex());
						setTable(currentTable);
						break;
					case DELETE_ENTRY:
						currentTable.removeEntry(received.getKey());
						setTable(currentTable);
						break;
					case EDIT_ENTRY:
						currentTable.editEntry(received.getEntry());
						setTable(currentTable);
						break;
					case GET_ACTUAL_TABLE:
						setTable(received.getTable());
						break;
					case GET_TABLE_NAMES:
						gui.setTableList(received.getTableNames());
						break;
					case DATABASE_LIST:
						setDatabaseList(received.getDatabaseList());
						break;
					case ADD_USER:
						break;
					case EDIT_USER:
						break;
					case DELETE_USER:
						break;
					case ADD_DATABASE:
						String[] newDatabaseList = new String[databaseList.length +1];
						int i = 0;
						for(;i < databaseList.length; i++)
							newDatabaseList[i] = databaseList[i];
						newDatabaseList[i] = received.getDatabase();
						setDatabaseList(newDatabaseList);
						break;
					case USER_LIST:
						User[] userlist = received.getUserList();
						String[] users = new String[userlist.length];
						for(int v = 0; v<userlist.length;v++)
						{
							users[v] = userlist[v].getUsername();
						}
						this.refreshUserList(users);
						break;
					default:
						throw new IOException("Unexpected server command");
					}
				}
			
		}
		catch (ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}
		catch (IOException ex) { /* Skip to finally */ }
		finally
		{
			try
			{
				objIn.close();
				objOut.close();
			}
			catch (IOException ex) { /* Couldn't close streams */ }
		}
	}
	
	private void refreshUserList(String[] userlist) {
		gui.refreshUsers(userlist);
	}

	@Override
	public void setDatabaseList(String[] list)
	{
		databaseList = list;
		gui.setDatabases(list);
	}
	
	@Override
	public void createTable()
	{
	  	AddColumnGUI tableGUI = new AddColumnGUI(true); //true for create table, false for create column
	  	tableGUI.setVisible(true);
		try
		{
			if(tableGUI.getTableName() != null)
			{
				objOut.writeObject(new Message(Command.ADD_TABLE, tableGUI.getTableName()));
				if(tableGUI.getColumns() != null)
				objOut.writeObject(new Message(Command.ADD_COLUMNS, tableGUI.getColumns()));
			}
		}
		catch (IOException ex)
		{
			// TODO Catch block
		}
	}
	
	@Override
	public void deleteCurrentTable()
	{
		try
		{
			objOut.writeObject(new Message(Command.DELETE_TABLE, null));
		}
		catch (IOException ex)
		{
			// TODO Catch block
		}
	}

	@Override
	public void getTableNames(String database)
	{
		try
		{
			objOut.writeObject(new Message(Command.GET_TABLE_NAMES, database));
		}
		catch (IOException ex)
		{
		}
	}
	
	public void getTable(String tableName)
	{
		try
		{
			objOut.writeObject(new Message(Command.GET_ACTUAL_TABLE, tableName));
		}
		catch (IOException ex)
		{
		}
	}
	
	public void setTable(Table newTable)
	{
		if(newTable == null)
		{
			currentTable = null;
			gui.setFieldList(null);
			gui.setTable(null,null);
		}
		else
		{
			currentTable = newTable;
			String[] colNames =  currentTable.getColumnNames();
			gui.setFieldList(colNames);
			gui.setTable(currentTable.asArray(),colNames);
		}
	}

	@Override
	public void deleteColumn(int selectedIndex)
	{
		try
		{
			objOut.writeObject(new Message(Command.DELETE_COLUMN, selectedIndex));
		}
		catch (IOException e)
		{
			// TODO Catch block
		} 
	}
	
	@Override
	public void createEntry(String[] headers)
	{ 
		EditEntryGUI addEnt = new EditEntryGUI(headers);
		addEnt.setVisible(true);
		Comparable[] newEntry = addEnt.getData();
		 try
		 {
			 if(newEntry != null)
		 	objOut.writeObject(new Message(Command.ADD_ENTRY, newEntry)); 
		 }
		 catch (IOException e)
		 {
		 } 
	}
	
	@Override
	public void deleteEntry(int key)
	{
		try
		{
			objOut.writeObject(new Message(Command.DELETE_ENTRY, key));
		}
		catch (IOException e)
		{
			// TODO Catch block
		}
	}

	@Override
	public void addColumn() 
	{
		AddColumnGUI newCols = new AddColumnGUI(false);
		newCols.setVisible(true);
		Column[] addedColumns = newCols.getColumns();
		 		try
		 		{
		 			if(addedColumns!=null)
		 			objOut.writeObject(new Message(Command.ADD_COLUMNS, addedColumns));
		 		}
		 		catch (IOException ex)
		 		{
		 		}  
	}

	@Override
	public void editEntry(int entryKey)
	{
		Entry editEntry = gui.getSelectedEntry();
 		EditEntryGUI editGUI = new EditEntryGUI(currentTable.getColumnNames(), editEntry);
 		editGUI.setVisible(true);
		// Data validation
 		String[] newValues = editGUI.getData();
 		if (newValues != null)
 		{
 			Column[] cols = currentTable.getColumns();
			try
  			{
 				for (int i = 0; i < cols.length; i++)
 				{
 					if (cols[i].getType() == Column.NUMBER)
 						editEntry.setfield(i, Double.parseDouble(newValues[i]));
 						else
 						editEntry.setfield(i, newValues[i]);
 				}
 				
 				objOut.writeObject(new Message(Command.EDIT_ENTRY, editEntry));
 			}
			catch (NumberFormatException e)
 			{
 				JOptionPane.showMessageDialog(gui, "Error converting value to Number\n" + e.getMessage());
			}
  			catch (IOException ex)
  			{
 -				ex.printStackTrace();
 +				
  			}
		}
	}

	@Override
	public String[] getColumnNames()
	{
		if (currentTable != null)
			return currentTable.getColumnNames();
		else
			return new String[0];
	}

	@Override
	public void applySearch(String[] values, String[] comparisons, int[] fields)
	{
		Comparable[] filterValues = new Comparable[values.length];
		Column[] cols = currentTable.getColumns();
		for (int i = 0; i < values.length; i++)
		{
			switch (cols[fields[i]].getType())
			{
			case Column.STRING:
				filterValues[i] = values[i];
				break;
			case Column.NUMBER:
				filterValues[i] = Double.parseDouble(values[i]);
				break;
			}
		}
		AVLTree<Entry> newTree = currentTable.getTree();
		for (int i = 0; i < values.length; i++)
		{
			Entry.setComparer(fields[i]);
			newTree = newTree.reconstructTree();
			// Create dud entry
			Comparable[] tempDat = new Comparable[cols.length];
			tempDat[fields[i]] = filterValues[i];
			newTree = newTree.getRange(new Entry(-1, tempDat), comparisons[i]);
		}
		// Display table
		gui.setTable(newTree.toArray(new Entry[newTree.size()]), currentTable.getColumnNames());
	}

	@Override
	public void createDatabase() {	
		try
		{
			String CDB = JOptionPane.showInputDialog("Create Database");
			if(CDB != null)
				objOut.writeObject(new Message(Command.ADD_DATABASE, CDB)); //sending String
		}
		catch (HeadlessException | IOException e)
		{	}
	}

	@Override
	public void deleteDatabase() {
		// TODO Auto-generated method stub
		try
		{
			objOut.writeObject(new Message(Command.DELETE_DATABASE, JOptionPane.showInputDialog("Delete Database"))); //sending String 
		}
		catch (HeadlessException | IOException e)
		{	}
	}

	@Override
	public void editUser(String username) {
		// TODO Auto-generated method stub
		try
		{
			objOut.writeObject(new Message(Command.EDIT_USER, new AddUserGUI(databaseList, username,false).getUser())); //TODO User Object
		}
		catch (HeadlessException | IOException e)
		{	}
	}

	@Override
	public void addUser() {
		// TODO Auto-generated method stub
		try
		{
			AddUserGUI adder = new AddUserGUI(databaseList);
			adder.setVisible(true);
			
			if(adder.getUser() != null){
				objOut.writeObject(new Message(Command.ADD_USER, adder.getUser()));
				}
		}
		catch (HeadlessException | IOException e)
		{	e.printStackTrace();}
	}

	@Override
	public void deleteUser(String username) {
		// TODO Auto-generated method stub
		try
		{
			objOut.writeObject(new Message(Command.DELETE_USER, username)); //TODO sending String username
		}
		catch (HeadlessException | IOException e)
		{ 	}
	}
	
	public void sort(int field)
 	{
 		Entry.setComparer(field);
 		newTree = newTree.reconstructTree();
 		gui.setTable(newTree.toArray(new Entry[newTree.size()]), currentTable.getColumnNames());
	}

	@Override
	public void requestUserList() {
		try
		{
			objOut.writeObject(new Message(Command.	USER_LIST, null)); 
		}
		catch (HeadlessException | IOException e)
		{ 	
		}
	}
}
