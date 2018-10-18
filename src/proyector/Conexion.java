/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyector;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author Renzo
 */
public class Conexion {
    private int PUERTO = 7120;
    private String IP = "127.0.0.1";

    private String salida;
    
    Socket socket = null;
    private boolean conectado = false;
    
    private OutputStreamWriter osw;
    private BufferedReader br;
    
    public Conexion(){
    }
    
    public Conexion(String ip, String puerto){
        setIP(ip, puerto);
    }
    
    public void enviarComando(byte []comando){
        if( !conectado ) return;
        
        try {
            socket.getOutputStream().write(comando);
            recibirDatos();
        } catch (Exception e) {
        }
    }
    
    public void enviarComando(String comando){
        if( !conectado ) return;
        
        try {
            osw = new OutputStreamWriter( socket.getOutputStream() );
            br = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
            
            String request = "";
            
            request += "%1";
            request += comando;
            
            osw.write(request + "\r");
            osw.flush();
            
            String respuesta = parseResponse( br.readLine() );
        } catch (Exception e) {
        } finally {
            if (osw != null){
                try{
                    osw.close();
                } catch (Exception e){}
            }

            if (br != null){
                try{
                    br.close();
                } catch (Exception e){}
            }
        }
    }
    
    private void recibirDatos(){
        salida = "";
        
        try {
            InputStream is = socket.getInputStream();
            
            int ch;
            while( ( ch = is.read() ) != -1 ){
                salida += (char)ch;
            }
        } catch (Exception e) {
        }
    }
    
    public String leerDatos(){
        return salida;
    }
    
    public boolean conectado(){
        if( null!=socket )
            return socket.isConnected();
        return conectado;
    }
    
    public void setIP(String ip, String puerto){
        try {
            this.IP = ip;
            this.PUERTO = Integer.parseInt(puerto);
            
            socket = new Socket(this.IP, this.PUERTO);
            
            recibirDatos();
            
            conectado = socket.isConnected();
        } catch (Exception e) {
            socket = null;
            conectado = false;
        }
    }
    
    
    private String parseResponse(String response){
        if (response != null){
            if (response.contains(" ERRA"))
            {
                response = "Authentication error.";
            }
            else if (response.endsWith("ERR1"))
            {
                response = "Unknown Command.";
            }
            else if (response.endsWith("ERR2"))
            {
                response = "Wrong Parameter.";
            }
            else if (response.endsWith("ERR3"))
            {
                response = "Device is not responding.";
            }
            else if (response.endsWith("ERR4"))
            {
                response = "Device has internal error.";
            }
            else
            {
                response = response.substring(7);
            }
        }

        return response;
    }
    

}
