/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Notificacion {
    public static int ERROR = 0;
    public static int INFO = 1;
    public static int WARNING = 2;
    public static int NONE = 3;
    
    public static void mostrar(JFrame vnt, String mensaje){
        mostrar(vnt, mensaje, "Soporte URP", INFO);
    }
    
    public static void mostrar(JFrame vnt, String mensaje, String titulo){
        mostrar(vnt, mensaje, titulo, INFO);
    }
    
    public static void mostrar(JFrame vnt, String mensaje, String titulo, int tipo){
        try {
            if (SystemTray.isSupported()) {
                _mostrar(titulo, mensaje, tipo);
            } else {
                JOptionPane.showMessageDialog(vnt, mensaje, titulo, JOptionPane.ERROR);
            }
        } catch (Exception e) {}
    }
    private static void _mostrar(String titulo, String mensaje, int tipo){
        try {
            //Obtain only one instance of the SystemTray object
            SystemTray tray = SystemTray.getSystemTray();

            //If the icon is a file
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            //Alternative (if the icon is on the classpath):
            //Image image = Toolkit.getToolkit().createImage(getClass().getResource("icon.png"));

            TrayIcon trayIcon = new TrayIcon(image, "Soporte URP");
            //Let the system resize the image if needed
            trayIcon.setImageAutoSize(true);
            //Set tooltip text for the tray icon
            trayIcon.setToolTip("Soporte URP");
            tray.add(trayIcon);
            
            MessageType mt;
            switch( tipo ){
                case 0: mt = MessageType.ERROR; break;
                case 2: mt = MessageType.WARNING; break;
                case 3: mt = MessageType.NONE; break;
                default: mt = MessageType.INFO; break;
            }

            trayIcon.displayMessage(titulo, mensaje, mt);
        } catch (Exception e) {
        }
    }
}
