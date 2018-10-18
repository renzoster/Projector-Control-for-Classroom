package proyector;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public abstract class Proyector {
    protected Socket socket = null;
    DataOutputStream writer = null;
    BufferedReader reader = null;
    
    protected String IP;
    protected int PUERTO;
    
    public Proyector(){
    }
    
    // Encendido
    public abstract boolean getEncendido();
    public abstract void setEncendido( boolean encendido );
    
    // Estado Socket
    public boolean conectar(){
        try {
            socket = new Socket(IP, PUERTO);
            if( socket.isConnected() ){
                writer = new DataOutputStream( socket.getOutputStream() );
                reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    
    public abstract boolean conectado();
    
    public String enviarComando(String bytes){
        byte []comando = convertir(bytes);
        
        try {
            if( socket.isConnected() ){
                writer.write(comando);
                writer.flush();
                
                return reader.readLine();
            }
        } catch (Exception e) {}
        
        return null;
    }
    
    // Direccionamiento
    public void setDireccion(String ip){
        try {
            String []token = ip.split(":");
        
            IP = token[0];
            
            if( token.length>1 )
                PUERTO = Integer.parseInt(token[1]);
        } catch (Exception e) {}
    }
    
    public void setIP(String ip){
        IP = ip;
    }
    
    public void setPuerto(int puerto){
        PUERTO = puerto;
    }
    
    public void setPuerto(String puerto){
        try {
            PUERTO = Integer.parseInt(puerto);
        } catch (Exception e) {
        }
    }
    
    public static byte[] convertir(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    
}
