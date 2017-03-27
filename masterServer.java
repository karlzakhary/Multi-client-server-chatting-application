import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;




public class masterServer {
	  protected static  Socket ss;
	  protected ObjectInputStream  i;
	  protected  ObjectOutputStream o;
	  protected static  ServerSocket Mserver;
	  protected int port;
	  protected final static int maxServers =4;
	  protected static serverThread[] serverThreads = new serverThread[maxServers];
	  
	  
	  public static void main(String []args){
		  int serverPort = 6500;
		  String host = "localhost";
		  
		  
		 //if new values are inserted use new values else use default  
		  if (args.length < 2) {
		      System.out
		          .println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
		              + "Now using host=" + host + ", portNumber=" + serverPort);
		    } else {
		      host = args[0];
		      serverPort = Integer.valueOf(args[1]).intValue();
		    }
		  
		  
		  try {
		        Mserver = new ServerSocket(6500);
		      } catch (IOException e) {
		        System.out.println(e);
		      }
		  
		  
		  while(true){
			  
			  try{
				  ss=Mserver.accept();
				  int serverNumber = 0;
				  for(serverNumber = 0 ; serverNumber < maxServers ; serverNumber++){
					  if(serverThreads[serverNumber] == null){
						  (serverThreads[serverNumber] = new serverThread(ss,serverNumber)).start();
						  break;
					  }else if (serverNumber == maxServers) {
				          ss.close();
				        }
				      }
				  } catch (IOException e) {
				        System.out.println(e);
				      }
				    }
		  
		  
		  
		  
		  
		  
				  }
				

}
class serverThread extends Thread{
	protected int serverNumber;
	protected Socket s;
	protected ServerSocket serverSocket;
	protected Socket connToM;
	protected ObjectInputStream i, mI;
	protected ObjectOutputStream o, mO;
	protected static serverThread[] runingServers;
	
	

	 serverThread(Socket ss, int serverNumber) {
		this.connToM =ss;
		this.serverNumber=serverNumber;
		
		try{
		this.mI=new ObjectInputStream(ss.getInputStream());
		this.mO= new ObjectOutputStream(ss.getOutputStream());
		}catch(IOException e){
			e.printStackTrace();
			closeThread();
			
			
		}
	
	}
	
	public void run(){
		
		
			String serverRequest ="";
			
			while(true ){
				try {
					serverRequest= (String) mI.readObject();
				} catch (ClassNotFoundException|IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				
				switch(serverRequest.substring(0, 3)){
				case "quit":    closeThread()    ;break;
				case "gSer": getServerList();break;
				case "gUr": whereIs(serverRequest.substring(3,serverRequest.length() ));break;
				case "gAm": try {
						getAllMembers();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}break;
				default:break;
				}
			}
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	private synchronized  void getAllMembers() throws IOException {
		for(int i = 0 ; i <runingServers.length;i++){
			if(runingServers[i]!=null){
				runingServers[i].mO.writeObject("gM");
				
				
			}
			}
		
	}

	private synchronized  void whereIs(String substring) {
		
		// TODO Auto-generated method stub
		for(int i = 0 ; i <runingServers.length;i++){
			if(runingServers[i]!=this){
				try {
					runingServers[i].mO.writeObject("get "+substring);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		}
		
		
	}

	private synchronized void getServerList() {
		try {
			o.writeObject(runingServers);
			o.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private synchronized void closeThread() {
		// TODO Auto-generated method stub
		
		try {
			mI.close();
			mO.close();
			s.close();
			for(int i = 0 ; i <runingServers.length;i++){
				if(runingServers[i]==this){
					runingServers[i]=null;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	

}
