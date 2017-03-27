import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class server {
	protected static int serverNum=0;
	  protected static Socket cS,mS;
	  
	  protected static  ServerSocket serverToC;
	  protected final static int maxClients= 50;
	  protected static clientThread[] activeClients = new clientThread[maxClients];
	  
	 
	  
	public static void main(String[]args){
		serverNum++;
		 int serverPort = 6500;
		 int clientPort = 6000;
		 String host = "localhost";
		  
		  
		 //if new values are inserted use new values else use default  
		  if (args.length < 2) {
		      System.out
		          .println("Usage: java server <host> <portNumber>\n"
		              + "Now using host=" + host + ", portNumber=" + serverPort);
		    } else {
		      host = args[0];
		      serverPort = Integer.valueOf(args[1]).intValue();
		    }
		  
		  
		  try {
			  mS = new Socket(host,serverPort);
			  
			  serverToC = new ServerSocket(clientPort); 
			 cS = new Socket();
		       
		      } catch (IOException e) {
		        System.out.println(e);
		      }
		  
		  
		  while(true){
			  
			  try{
				 
				  cS = serverToC.accept();
				  if(cS!=null){
				  int clientNumber = 0;
				  for(clientNumber = 0 ; clientNumber < maxClients ; clientNumber++){
					  if(activeClients[clientNumber] == null){
						 
						  activeClients[clientNumber] = new clientThread(mS,cS,clientNumber,activeClients,cS.getInputStream(),cS.getOutputStream());
						  activeClients[clientNumber].run();
						  break;
					  }else if (clientNumber == maxClients) {
						  ObjectOutputStream output = new ObjectOutputStream(cS.getOutputStream());
						  output.writeObject("Server is too busy");
						  output.close();
				          cS.close();
				          
				          // to do 
				        }
				  }
				  }   
			  }catch (IOException|NullPointerException e) {
				        System.out.println(e);
				      }
				   }
				    
		  
		  
		
	}

}



class clientThread extends Thread{
	
	protected String clientName;
	protected int clientNumber;
	protected Socket cS,mS;
	protected ObjectInputStream input,mI;
	protected ObjectOutputStream output,mO	;
	protected clientThread[] activeClients = new clientThread[50];
	
	
	
	
	

	public clientThread(Socket mS, Socket cS, int clientNumber, clientThread[] activeClient, InputStream inputStream, OutputStream outputStream) {
		
		this.cS = cS;
		this.mS = mS;
		this.clientNumber = clientNumber;
		this.activeClients = activeClient;
		
		try {
			this.output = new ObjectOutputStream(outputStream);
			this.input =new ObjectInputStream( inputStream);
			mI= new ObjectInputStream( mS.getInputStream());
	       mO= new ObjectOutputStream( mS.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void run(){
		
		clientThread[] activeClients = this.activeClients;
		
		
		
		try{
		
		
			
			
			sendMessage("pleaze enter a userName");
			String name =(String) input.readObject();
			sendMessage("Welcome "+ name +"to our Chatroom for commands enter cmd");
			
			
			synchronized (this) {
		        for (int i = 0; i < activeClients.length; i++) {
		          if (activeClients[i] != null && activeClients[i] == this) {
		            this.clientName =  name;
		            break;
		          }
		        }
		        for (int i = 0; i <  activeClients.length; i++) {
		          if (activeClients[i] != null && activeClients[i] != this) {
		        	  activeClients[i].sendMessage("*** A new user " + name
		                + " entered the chat room !!! ***");
		          }
		        }
		      }
			
			
			
			
			while (true){
				String msg = (String)input.readObject();
				String masterServerReq = (String) mI.readObject();
				String [] m = msg.split("(?!^)");
				
				switch(msg){
				 case "cmd": showCom() ;break;
				 case "quit": closeThread();break;
				 case "to": privateMessage();break;
				 case "getMembers": getMembersList();break;
				 
				default: publicMessage(msg) ;break;
				
				
					
				}
				switch(masterServerReq.substring(0, 2)){
				case "gM": mO.writeObject(activeClients);break;
				case "get": checkFor(masterServerReq.substring(3));break;
				}
				
				
			}
			
			
			
			
			
			
			
			
			
			
			
		}catch (IOException|ClassNotFoundException e) {
			e.getMessage();
			e.printStackTrace();
			this.closeThread();
			
	    } 
		
	}
	
	
	private String checkFor(String substring) {
		// TODO Auto-generated method stub
		return null;
	}

	private void getMembersList() {
		// TODO Auto-generated method stub
		synchronized (this) {
            for (int i = 0; i < activeClients.length; i++) {
              if (activeClients[i] != null && activeClients[i].clientName != null){
            	  this.sendMessage("client" + i + "  "+activeClients[i].clientName);
              }
              
            }
        
		}
            	  
		
		
	}

	private void publicMessage(String msg) {
		// TODO Auto-generated method stub
		synchronized (this) {
            for (int i = 0; i < activeClients.length; i++) {
              if (activeClients[i] != null && activeClients[i].clientName != null) {
            	  activeClients[i].sendMessage("<" + clientName + "> " + msg);
              }
            }
            try {
				mO.writeObject("gAm");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          }
		
	}

	private void privateMessage() {
		sendMessage("Please enter the userName of the destination");
		String desName,privateMsg="";
		
		try {
			desName = (String) input.readObject();
			if (!desName.isEmpty()) {
	            synchronized (this) {
	              for (int i = 0; i < activeClients.length; i++) {
	                if (activeClients[i] != null && activeClients[i] != this
	                    && activeClients[i].clientName != null
	                    && activeClients[i].clientName.equals(desName)) {
	                	activeClients[i].sendMessage("<" + clientName + "> has started a private chat window enter quit to end " );
	                 
	                  this.sendMessage(">" + clientName + "> private session has started enter quit to end ");
	                  
	                  while(!privateMsg.equalsIgnoreCase("quit")){
	                	  privateMsg = (String) input.readObject();
	                	  activeClients[i].sendMessage(privateMsg);
	                  }
	                  
			
	                }
		}
	            }
	            }
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			closeThread();
		}
		
	
		
			
		}
            

	private void closeThread() {
		// TODO Auto-generated method stub
		publicMessage(clientName + "is leaving ....");
		 synchronized (this) {
		        for (int i = 0; i <activeClients.length; i++) {
		          if (activeClients[i] == this) {
		        	  activeClients[i] = null;
		          }
		        }
		      }
		      /*
		       * Close the output stream, close the input stream, close the socket.
		       */
		     closeConnection();
		
	}

	private void closeConnection() {
		// TODO Auto-generated method stub
		
			 try {
				input.close();
				output.close();
			    cS.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      
	
	}

	private void showCom() {
		// TODO Auto-generated method stub
		sendMessage("enter quit to quit");
		sendMessage("enter to for private chat");
		sendMessage("enter get for membersList");
		
	}

	public void sendMessage(String m){
		try {
			output.writeObject("/n Server - " + m);
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
