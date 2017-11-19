package Server;

import java.io.Serializable;

public class Message implements Serializable
{
	private static final long serialVersionUID = -5903038581933510537L;
	
	private Command type;
	private Object data;
	
	public Message(Command messageType, Object messageData)
	{
		type = messageType;
		switch (messageType)
		{
		case MESSAGE:
		case ADD_TABLE:
		case DELETE_TABLE:
		case GET_TABLE:
		case GET_DATABASE:
			if (!(messageData instanceof String))
				throw new IllegalArgumentException("messageData must be of type String for messageType " + messageType);
			break;
		case ADD_COLUMN:
			if (!(messageData instanceof Column))
				throw new IllegalArgumentException("messageData must be of type Column for messageType " + messageType);
			break;
		case DELETE_COLUMN:
			if (!(messageData instanceof Integer))
				throw new IllegalArgumentException("messageData must be of type Integer for messageType " + messageType);
			break;
		case ADD_ENTRY:
			if (!(messageData instanceof String))
				throw new IllegalArgumentException("messageData must be of type String for messageType " + messageType);
			break;
		case DELETE_ENTRY:
			if (!(messageData instanceof Entry))
				throw new IllegalArgumentException("messageData must be of type Entry for messageType " + messageType);
			break;
		case EDIT_ENTRY:
			if (!(messageData instanceof Entry))
				throw new IllegalArgumentException("messageData must be of type Entry for messageType " + messageType);
			break;
		case LOGIN:
		case DATABASE_LIST:
		case TABLE_LIST:
		case COLUMN_LIST:
			if (!(messageData instanceof String[]))
				throw new IllegalArgumentException("messageData must be of type String[] for messageType " + messageType.toString());
			break;
		}
		data = messageData;
	}
	
	public Command getCommandType() { return type; }
	
	public String getMessage()
	{
		if (data instanceof String)
			return (String)data;
		else
			return null;
	}
	
	public Entry getEntry()
	{
		if (data instanceof Entry)
		{
			return (Entry)data;
		}
		else
			return null;
	}
	
	public Comparable[] getAddEntry()
	{
		if (data instanceof String)
		{
			Comparable[] entry = ((String) data).split(",");
			return entry;
		}
		else
			return null;
	}
	
	public String getTable()
	{
		if (data instanceof String)
			return (String)data;
		else
			return null;
	}
	
	public String getDatabase()
	{
			return (String)data;
	}
	
	public String[] getDatabaseList()
	{
		if (data instanceof String[])
			return (String[])data;
		else
			return null;
	}
	
	public String[] getTableList()
	{
			return (String[])data;
	}
	
	public String[] getLogin()
	{
		if (data instanceof String[])
			return (String[])data;
		else
			return null;
	}
	
	public int getColToRmv() 
	{
			return (Integer)data;
	}
	
	public Column getColToAdd() 
	{
			return (Column)data;
	}
	
}
