/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
/**
 *
 * @author Renzo
 */
public class Util {
    public static String get_ip(){
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }
    
    public static boolean preg_match(String source, String regex){
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(source);
        
        return matcher.find();
    }
    
    public static void mensaje(JFrame ventana, String mensaje){
        JOptionPane.showMessageDialog(ventana, mensaje, "Soporte FACEE", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void error(JFrame ventana, String mensaje){
        JOptionPane.showMessageDialog(ventana, mensaje, "Soporte FACEE", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void alerta(JFrame ventana, String mensaje){
        JOptionPane.showMessageDialog(ventana, mensaje, "Soporte FACEE", JOptionPane.WARNING_MESSAGE);
    }
    
    public static boolean confirmar(JFrame ventana, String mensaje){
        int r = JOptionPane.showConfirmDialog(ventana, mensaje, "Soporte FACEE", JOptionPane.YES_NO_OPTION);
        return r==0;
    }
    
    public static void apagar_pc(){
        try {
            Util.shutdown();
        } catch (RuntimeException | IOException e){}
    }
    
    public static void shutdown() throws RuntimeException, IOException {
        String shutdownCommand;
        String operatingSystem = System.getProperty("os.name");

        if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
            shutdownCommand = "shutdown -h now";
        }
        else if ("Windows".equals(operatingSystem)) {
            shutdownCommand = "shutdown.exe -s -t 0";
        }
        else {
            throw new RuntimeException("Unsupported operating system.");
        }

        Runtime.getRuntime().exec(shutdownCommand);
        System.exit(0);
    }
}
