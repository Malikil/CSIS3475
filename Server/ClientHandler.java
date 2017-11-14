package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import Client.Message;

public class ClientHandler implements Runnable
{
	private ObjectInputStream objIn;
	private ObjectOutputStream objOut;
	private BufferedReader strIn;
	private PrintWriter strOut;
	private Server parent;
	
	public ClientHandler(Socket connection, Server server)
	{
		parent = server;
		// Establish connections
		try
		{
			objOut = new ObjectOutputStream(connection.getOutputStream());
			objIn = new ObjectInputStream(connection.getInputStream());
			
			strOut = new PrintWriter(connection.getOutputStream(), true);
			strIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		}
		catch (IOException ex)
		{
			System.out.println("Problem establishing connections");
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			boolean loggedIn = false;
			BufferedReader userList = new BufferedReader(new FileReader(new File("Users.txt")));
			do
			{
				try
				{
					Message received = (Message)objIn.readObject();
					if(received.getType() == Command.LOGIN)
					{	
						String username = received.getUsername().toLowerCase();
						// Make received username lowercase to avoid case sensitivity
						String password = received.getPassword();
						String nextLine;
						while ((nextLine = userList.readLine()) != null)
						{
							String[] userInfo = nextLine.split(",");
							// Make username from file lowercase to avoid case sensitivity
							userInfo[0] = userInfo[0].toLowerCase();
							if (userInfo[0].equals(username))
								if (userInfo[1].equals(password))
								{
									// Send success to client
									Message send = new Message(Command.CONNECTION_SUCCESS);
									// Send available databases to client
									send.setDatabases(parent.getUserDatabases(userInfo[0]));
									objOut.writeObject(send);
									loggedIn = true;
									break;
								}
								else
								{
									Message send = new Message(Command.INCORRECT_PASSWORD);
									objOut.writeObject(send);
									break;
								}
						}
						if (!loggedIn)
						{
							objOut.writeObject(Command.INCORRECT_USER);
						}
					}
					
				}
				catch (IOException ex)
				{
					// Error receiving user/pass from client
				}
				catch (ClassNotFoundException ex)
				{
					ex.printStackTrace();
					break;
				}
			} while (!loggedIn);
			userList.close();
		}
		catch (IOException ex)
		{
			System.out.println("User file couldn't be found");
			try
			{
				objIn.readObject(); // Receive the String[] of user/pass, we just don't need it now
				objOut.writeObject(Command.MESSAGE);
				objOut.close();
				objIn.close();
				strOut.close();
				strIn.close();
			}
			catch (IOException | ClassNotFoundException e2) { System.out.println("Couldn't close sockets"); } // TODO DEBUG
			return;
		}
		
		// Client is logged in, now wait for commands
		while (true)
		{
			try
			{
				switch ((Command)objIn.readObject())
				{
				case ADD_COLUMN:
					break;
				case ADD_ENTRY:
					break;
				case ADD_TABLE:
					break;
				case DELETE_COLUMN:
					break;
				case DELETE_ENTRY:
					break;
				case DELETE_TABLE:
					break;
				case EDIT_ENTRY:
					break;
				case GET_TABLE:
					break;
				case GET_DATABASE:
					System.out.println("Received GET_DATABASE from client");
					String database = strIn.readLine(); System.out.println("Received database name from client");
					objOut.writeObject(Command.GET_DATABASE); System.out.println("Sent GET_DATABASE to client");
					objOut.writeObject(parent.getTableList(database)); System.out.println("Sent databases to client");
					break;
				case MESSAGE:
					parent.messageReceived(strIn.readLine());
					break;
				}
			}
			catch (ClassNotFoundException | IOException ex)
			{
				
			}
		}
	}
	
	public void sendMessage(String message)
	{
		try
		{
			objOut.writeObject(Command.MESSAGE);
			strOut.println(message);
		}
		catch (IOException ex)
		{
			
		}
	}
}
