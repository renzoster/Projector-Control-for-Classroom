package chat;

import cliente.Control;
import cliente.Ventana;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

public class SocketClient implements Runnable{
    
    public int port = 13000;
    public String serverAddr = "172.20.56.40";
    public Socket socket;
    public SocketTransmission transmission;
    public ObjectAdapter objectAdapter;
    Protocol protocol;
    
    public Ventana ui;
    public Control app;
    public ObjectInputStream In;
    public ObjectOutputStream Out;
//    public History hist;
    
    public SocketClient(Ventana frame, Control ctrl) throws IOException{
        app = ctrl;
        ui = frame; 
        
        //socket = new Socket(InetAddress.getByName(serverAddr), port);
        socket = new Socket(serverAddr, port);
        transmission = new SocketTransmission(socket);
        objectAdapter = new ObjectAdapter();
        protocol = new Protocol(objectAdapter, transmission);

        /*Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());*/
        
  //      hist = ui.hist;
    }
    
    private void appendHTML(String html) {
        HTMLDocument doc = (HTMLDocument) ui.mensajes.getDocument();
        Element contentElement = doc.getElement("content");
        try {
                if (contentElement.getElementCount() > 0) {
                        Element lastElement = contentElement.getElement(contentElement.getElementCount() - 1);
                        doc = (HTMLDocument) contentElement.getDocument();
                        doc.insertAfterEnd(lastElement, html);
                } else {
                        doc.insertAfterStart(contentElement, html);
                }
        } catch (BadLocationException | IOException e) {
                //MessageBox.showMessageBoxInUIThread(this, "Internal error: " + e.getMessage(), MessageBox.MESSAGE_ERROR);
        }
    }

    @Override
    public void run() {
        boolean keepRunning = true;
        while(keepRunning){
            try {
                Object receivedObject = protocol.receiveObject();
                if (receivedObject instanceof Mensaje){
                    Mensaje msg = (Mensaje)receivedObject;
                    
                    System.out.println("Incoming : "+msg.toString());
                
                    String username = "cliente";

                    if(msg.type.equals("message")){
                        if(msg.recipient.equals(username)){
                            appendHTML("["+msg.sender +" > Me] : " + msg.content + "\n");
                        }
                        else{
                            appendHTML("["+ msg.sender +" > "+ msg.recipient +"] : " + msg.content + "\n");
                        }

                        if(!msg.content.equals(".bye") && !msg.sender.equals(username)){
                            String msgTime = (new Date()).toString();
                        }
                    } else if(msg.type.equals("login")){
                        if(msg.content.equals("TRUE")){
                            ui.mensaje.setEditable(true);ui.enviar.setEnabled(true);
                            appendHTML("[SERVER > Me] : Login Successful\n");
                        }
                        else{
                            appendHTML("[SERVER > Me] : Login Failed\n");
                        }
                    } else if(msg.type.equals("test")){
                        ui.mensaje.setEditable(false);ui.enviar.setEnabled(false);
                        /*ui.jButton1.setEnabled(false);
                        ui.jButton2.setEnabled(true); ui.jButton3.setEnabled(true);
                        ui.jTextField3.setEnabled(true); ui.jPasswordField1.setEnabled(true);
                        ui.mensaje.setEditable(false); ui.jTextField2.setEditable(false);
                        ui.jButton7.setEnabled(true);*/
                    }
                }
                
            } catch (EOFException ex) {
                if( ex.getMessage()==null ){
                    System.out.println(ex);
                    keepRunning = false;
                    System.out.println("Error 1: "+ex.getMessage());
                    appendHTML("<div class=alert>[Application > Me] : Connection Failure</div>\n");
                    ui.mensaje.setEditable(false);ui.enviar.setEnabled(false);


                    app.clientThread.stop();

                    System.out.println("Exception SocketClient run()");
                    ex.printStackTrace();
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        /*while(keepRunning){
            try {
                Mensaje msg = (Mensaje) In.readObject();
                System.out.println("Incoming : "+msg.toString());
                
                if(msg.type.equals("message")){
                    if(msg.recipient.equals(ui.username)){
                        appendHTML("["+msg.sender +" > Me] : " + msg.content + "\n");
                    }
                    else{
                        appendHTML("["+ msg.sender +" > "+ msg.recipient +"] : " + msg.content + "\n");
                    }
                                            
                    if(!msg.content.equals(".bye") && !msg.sender.equals(ui.username)){
                        String msgTime = (new Date()).toString();
                        
                        try{
//                            hist.addMessage(msg, msgTime);
                            DefaultTableModel table = (DefaultTableModel) ui.historyFrame.jTable1.getModel();
                            table.addRow(new Object[]{msg.sender, msg.content, "Me", msgTime});
                        }
                        catch(Exception ex){}  
                    }
                }
                else if(msg.type.equals("login")){
                    if(msg.content.equals("TRUE")){
                        ui.jButton2.setEnabled(false); ui.jButton3.setEnabled(false);                        
                        ui.jButton4.setEnabled(true); ui.jButton5.setEnabled(true);
                        appendHTML("[SERVER > Me] : Login Successful\n");
                        ui.jTextField3.setEnabled(false); ui.jPasswordField1.setEnabled(false);
                    }
                    else{
                        appendHTML("[SERVER > Me] : Login Failed\n");
                    }
                }
                else if(msg.type.equals("test")){
                    ui.jButton1.setEnabled(false);
                    ui.jButton2.setEnabled(true); ui.jButton3.setEnabled(true);
                    ui.jTextField3.setEnabled(true); ui.jPasswordField1.setEnabled(true);
                    ui.mensaje.setEditable(false); ui.jTextField2.setEditable(false);
                    ui.jButton7.setEnabled(true);
                }
                else if(msg.type.equals("newuser")){
                    if(!msg.content.equals(ui.username)){
                        boolean exists = false;
                        for(int i = 0; i < ui.model.getSize(); i++){
                            if(ui.model.getElementAt(i).equals(msg.content)){
                                exists = true; break;
                            }
                        }
                        if(!exists){ ui.model.addElement(msg.content); }
                    }
                }
                else if(msg.type.equals("signup")){
                    if(msg.content.equals("TRUE")){
                        ui.jButton2.setEnabled(false); ui.jButton3.setEnabled(false);
                        ui.jButton4.setEnabled(true); ui.jButton5.setEnabled(true);
                        appendHTML("[SERVER > Me] : Singup Successful\n");
                    }
                    else{
                        appendHTML("[SERVER > Me] : Signup Failed\n");
                    }
                }
                else if(msg.type.equals("signout")){
                    if(msg.content.equals(ui.username)){
                        appendHTML("["+ msg.sender +" > Me] : Bye\n");
                        ui.jButton1.setEnabled(true); ui.jButton4.setEnabled(false); 
                        ui.mensaje.setEditable(true); ui.jTextField2.setEditable(true);
                        
                        for(int i = 1; i < ui.model.size(); i++){
                            ui.model.removeElementAt(i);
                        }
                        
                        ui.clientThread.stop();
                    }
                    else{
                        ui.model.removeElement(msg.content);
                        appendHTML("["+ msg.sender +" > All] : "+ msg.content +" has signed out\n");
                    }
                }
                else if(msg.type.equals("upload_req")){
                    
                    if(JOptionPane.showConfirmDialog(ui, ("Accept '"+msg.content+"' from "+msg.sender+" ?")) == 0){
                        
                        JFileChooser jf = new JFileChooser();
                        jf.setSelectedFile(new File(msg.content));
                        int returnVal = jf.showSaveDialog(ui);
                       
                        String saveTo = jf.getSelectedFile().getPath();
                        if(saveTo != null && returnVal == JFileChooser.APPROVE_OPTION){
//                            Download dwn = new Download(saveTo, ui);
//                            Thread t = new Thread(dwn);
 //                           t.start();
                            //send(new Message("upload_res", (""+InetAddress.getLocalHost().getHostAddress()), (""+dwn.port), msg.sender));
  //                          send(new Message("upload_res", ui.username, (""+dwn.port), msg.sender));
                        }
                        else{
                            send(new Mensaje("upload_res", ui.username, "NO", msg.sender));
                        }
                    }
                    else{
                        send(new Mensaje("upload_res", ui.username, "NO", msg.sender));
                    }
                }
                else if(msg.type.equals("upload_res")){
                    if(!msg.content.equals("NO")){
                        int port  = Integer.parseInt(msg.content);
                        String addr = msg.sender;
                        
                        ui.jButton5.setEnabled(false); ui.jButton6.setEnabled(false);
//                        Upload upl = new Upload(addr, port, ui.file, ui);
 //                       Thread t = new Thread(upl);
   //                     t.start();
                    }
                    else{
                        appendHTML("[SERVER > Me] : "+msg.sender+" rejected file request\n");
                    }
                }
                else{
                    appendHTML("[SERVER > Me] : Unknown message type\n");
                }
            }
            catch(Exception ex) {
                keepRunning = false;
                appendHTML("[Application > Me] : Connection Failure\n");
                ui.mensaje.setEditable(false);ui.enviar.setEnabled(false);
                
                for(int i = 1; i < ui.model.size(); i++){
                    ui.model.removeElementAt(i);
                }
                
                ui.clientThread.stop();
                
                System.out.println("Exception SocketClient run()");
                ex.printStackTrace();
            }
        }*/
    }
    
    public void send(Mensaje msg){
        try {
            //Out.writeObject(msg);
            //Out.flush();
            protocol.sendObject(msg);
            System.out.println("Outgoing : "+msg.toString());
            
            if(msg.type.equals("message") && !msg.content.equals(".bye")){
                String msgTime = (new Date()).toString();
                try{
//                    hist.addMessage(msg, msgTime);               
 //                   DefaultTableModel table = (DefaultTableModel) ui.historyFrame.jTable1.getModel();
 //                   table.addRow(new Object[]{"Me", msg.content, msg.recipient, msgTime});
                }
                catch(Exception ex){}
            }
        } 
        catch (IOException ex) {
            System.out.println("Exception SocketClient send()");
            System.out.println(ex.getMessage());
        }
    }
    
    public void closeThread(Thread t){
        t = null;
    }
}
