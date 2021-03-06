package Server;

public interface Server
{
	public String[] getTableList(String database);
	public Table getTable(String dbname, String tableName);
	public void saveTable(String dbName, String tableName, Table table);
	public void addEntry(String database, String table, Comparable[] data);
	public void addColumns(String databaseName, String tableName, Column[] columns);
	public void addTable(String databaseName, String tableName);
	public void deleteEntry(String databaseName, String tableName, int key);
	public void deleteColumn(String databaseName, String tableName, Integer index);
	public void deleteTable(String databaseName, String tableName);
	public void editEntry(String databaseName, String tableName, Entry newEntry);
	public void createDatabase(String databaseName);
	public void createUser(User user);
	public User getUser(String username);
	public void disconnectClient(ClientHandler client);
}
