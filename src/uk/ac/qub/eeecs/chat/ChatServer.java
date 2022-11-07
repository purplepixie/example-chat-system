package uk.ac.qub.eeecs.chat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



public class ChatServer {
	
	public class ClientConnection extends Thread
	{
		private Socket socket;
		private ChatServer chatServer;
		private BufferedReader br = null;
		private BufferedWriter bw = null;
		
		public void Init(Socket s, ChatServer cs)
		{
			socket = s;
			chatServer = cs;
			try
			{
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Client Initialised");
		}
		
		public void run()
		{
			System.out.println("Client Running");
			try 
			{
				while(true)
				{
					String msg = br.readLine();
					if (msg == null)
						throw new IOException("Null from client, broken pipe");
					System.out.println("Client Sent: "+msg);
					this.chatServer.Send(msg);
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				chatServer.ClientError(this);
			}
		}
		
		public void Send(String msg)
		{
			try {
				bw.write(msg);
				bw.write("\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	private ServerSocket serverSocket;
	private boolean quit = false;
	private int port = 8080;
	private List<ClientConnection> clients;
	
	
	public void Start()
	{
		System.out.println("Starting Chat Server...");
		clients = new ArrayList<ClientConnection>();
		try
		{
			serverSocket = new ServerSocket(port);
			System.out.println("Listening on port "+port);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			quit = true;
			System.exit(1);
		}
		
		while(!quit)
		{
			try 
			{
				System.out.println("ServerSocket waiting for connection");
				Socket s = serverSocket.accept();
				System.out.println("Connection from client");
				ClientConnection client = new ClientConnection();
				client.Init(s,this);
				clients.add(client);
				client.start();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
		}
	}
	
	public void Send(String msg)
	{
		System.out.print("Sending \""+msg+"\": ");
		for(ClientConnection c: clients)
		{
			try
			{
				System.out.print("-");
				c.Send(msg);
				System.out.print("+ ");
			}
			catch(Exception e)
			{
				ClientError(c);
				System.out.print("X ");
			}
		}
		System.out.println(" DONE");
	}
	
	public void ClientError(ClientConnection con)
	{
		clients.remove(con);
	}
	
	public void setPort(int p) { port = p; }
	public int getPort() { return port; }

}
