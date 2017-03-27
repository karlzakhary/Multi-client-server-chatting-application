import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


public class client {
	protected static final long serialVersionUID = 1L;
	protected  ObjectOutputStream o;
	protected  ObjectInputStream i;
	protected  Socket cS;
	protected  String message;
	protected String host;
	protected int serverPort;
	  
	  
	public  static void main(String [] args){
		  int serverPort = 6000;
			
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
			  client c = new client();
			  c.run();
			  
			  
			  
	  }
	  
	  private void run(){
		  
		  
		  try {
			  connectToServer(host,6000);
			  setupStreams();
			  
			 do{
				  String message= (String) i.readObject();
				  sendMessage(message);
				  
				  
				  
			  }while(!(message).equals("quit"));
			 closeApp();
			  
		       
		     
		  
		  
		 
		  
  } catch (IOException|NullPointerException e) {
        System.out.println(e);
      } catch (ClassNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
		  
	  }
	  
	  private void sendMessage(String message){
			try{
				o.writeObject("Client- " +message);
				o.flush();
				showMessage("\nClient- " + message);
			}catch(IOException e ){
				
			}
		}
	  
	  private  void connectToServer(String host, int portN)throws IOException{
			showMessage("Connecting....\n");
			cS = new Socket(host,portN);
			showMessage("Connected to .." + cS.getInetAddress().getHostName());
		}
		
		private  void setupStreams() throws IOException{
			o = new ObjectOutputStream(cS.getOutputStream());
			o.flush();
			i= new ObjectInputStream(cS.getInputStream());
			showMessage("Connected,,");
			
			
			  }
			
			
			
			
		

	private  void closeApp() {
			// TODO Auto-generated method stub
			try{
				o.close();
				i.close();
				cS.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}

	private  void showMessage(String string) {
		// TODO Auto-generated method stub
		
	}
	  
	
}
