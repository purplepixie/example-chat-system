package uk.ac.qub.eeecs.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatClientAuto extends Thread {
	
	public class Chatter extends Thread
	{
		Socket socket;
		private BufferedWriter bw;
		
		public Chatter(Socket s)
		{
			socket = s;
			try
			{
				bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public void run()
		{
			try
			{
				while(true)
				{
					System.out.println("Sending!");
					bw.write("Hello There\n");
					bw.flush();
					Thread.sleep(5000);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	

	private String server = "127.0.0.1";
	private int port = 8080;
	private Socket socket;
	private BufferedReader br;
	
	
	public void run()
	{
		System.out.println("Connecting...");
		try
		{
			socket = new Socket(server, port);
			System.out.println("Connected");
			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			Chatter chat = new Chatter(socket);
			chat.start();
			
			boolean quit = false;
			while(!quit)
			{
				String msg = br.readLine();
				if (msg == null)
					quit = true;
				else
					System.out.println(">> "+msg);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void setPort(int p) { port = p; }
	public int getPort() { return port; }
	public void setServer(String s) { server = s; }
	public String getServer() { return server; }

}
