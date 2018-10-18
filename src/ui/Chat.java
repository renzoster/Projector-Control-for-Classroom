package ui;

import control.Util;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import net.miginfocom.swing.MigLayout;


public class Chat extends JFrame{
    Ventana parent;
    
    JTextPane mensajes;
    JTextField mensaje;
    JButton enviar;
    
    public Chat(){
        construir();
        this.setVisible(rootPaneCheckingEnabled);
    }
    
    public void construir(){
        Chat vnt = this;
        
        setTitle("Soporte FACEE");
        setIconImage( Toolkit.getDefaultToolkit().getImage( getClass().getResource("/imagenes/urp.png") ) );
        
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                vnt.setVisible(false);
            }
        });
        
        // Set Layout
        MigLayout layout = new MigLayout("", "[grow,fill]", "[grow, fill][]");
        setLayout(layout);
        
        // Chat
        mensajes = new JTextPane();
        mensajes.setEditable(false);
        mensajes.setContentType("text/html");
        
        add(mensajes, "width 100%, span 2, wrap 10");
        
        mensaje = new JTextField();
        
        add(mensaje, "width 100%-120px, gpx 1, height pref+10px");
        
        enviar = new JButton();
        enviar.setText("Enviar");
        
        add(enviar, "wmax 120px, gpx 2, height pref+10px");
        
        setSize(440, 500);
        setLocationRelativeTo(null);
               
    }
    
    @Override
    public void setVisible(boolean b){
        super.setVisible(b);
        mensaje.requestFocus();
    }
}