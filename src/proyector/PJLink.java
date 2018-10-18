/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyector;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Renzo
 */
public class PJLink extends Proyector{

    public PJLink() {
        this.PUERTO = 4352;
    }
    
    public PJLink(String direccion){
        this.setDireccion(direccion);
    }
    
    public PJLink(String ip, int puerto){
        this.setIP(ip);
        this.setPuerto(puerto);
    }
    
    public boolean conectado(){
        try {
            return socket.isConnected();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean getEncendido(){
        try {
            String respuesta = enviarComando("2531504F5752203F0D");
            respuesta = respuesta.replace("%1POWR=", "");

            // 1: Encendido | 3: Apagado a Encendido
            if( respuesta.equals("1") || respuesta.equals("3") )
                return true;
        } catch (Exception e) {}
        return false;
    }
    public void setEncendido(boolean encendido){
        try {
            String respuesta;
            if( encendido ){
                respuesta = enviarComando("2531504F575220310D");
            } else {
                respuesta = enviarComando("2531504F575220300D");
            }
        } catch (Exception e) {
        }
    }
    
    private String parseToken(String line){
        String token = "";

        if( line.startsWith("PJLINK 1 ") ){
            token = line.substring(9);
        }

        return token;
    }
    
    private String getRequestHash(String token, String authPass){
        try{
            return new BigInteger(1, MessageDigest.getInstance("MD5").digest((token + authPass).getBytes("ASCII"))).toString(16);
        } catch (Exception e){}

        return "";
    }
}
