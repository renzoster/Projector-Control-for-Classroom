/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import cliente.Programa;
import control.Config;
import control.Util;
import db.DB;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

import view.FitImageJLabel;

public class Ventana extends JFrame{
    Chat chat = null;
    Config cfg;
    
    // Sistema
    DB db = null;
    
    // Interfaz
    List<FitImageJLabel> btnProyector = new ArrayList<>();
    FitImageJLabel btnApagar, btnSoporte;
    
    public Ventana(){
        cfg = Config.instance();
        
        construir();
        
        java.awt.EventQueue.invokeLater(() -> {
            sincronizar();
        });
    }
    
    public void construir(){
        // Iniciando variables
        Ventana obj = this;
        int height = 0;
        
        // Titulo
        setTitle("Soporte FACEE");
        setIconImage( Toolkit.getDefaultToolkit().getImage( getClass().getResource("/imagenes/urp.png") ) );
        
        // Operaciones de Ventana
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                setExtendedState(JFrame.ICONIFIED);
                centrar(obj);
                if( chat!=null ){
                    if( !chat.isVisible() )
                        centrar(chat);
                }
            }
            
            @Override
            public void windowDeiconified(WindowEvent e){
                
            }
        });
        
        addKeyListener(shortcutKeyListener());
        
        // Set Layout
        MigLayout layout = new MigLayout();
        setLayout(layout);
        
        
        // Botones de Proyector
        //String proyector = "172.17.20.138|172.17.20.139";
        String proyector = cfg.getOpcion("proyector");
        if( proyector!=null ){
            StringTokenizer proyectores = new StringTokenizer( proyector, "|");

            while( proyectores.hasMoreElements() ){
                String ip = (String)proyectores.nextElement();
                FitImageJLabel boton = new FitImageJLabel();
                boton.setSize(320, 77);
                boton.setBorder(new CompoundBorder( boton.getBorder(), new EmptyBorder(5, 0, 5, 0) ));
                boton.setIcon( new ImageIcon( getClass().getResource("/imagenes/encender.png") ) );
                boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                boton.addMouseListener(new MouseAdapter(){
                    public void mouseClicked(MouseEvent e){
                        if( boton.isEnabled() ){
                            Util.alerta(obj, "Proyector "+ip);
                        }
                    }
                });
                add(boton, "wrap");
                btnProyector.add(boton);

                height += 10;
            }
        }
        
        //btnProyector.get(0).setEnabled(false);
        
        // BotonProyector
        btnSoporte = new FitImageJLabel();
        btnSoporte.setSize(320, 83);
        btnSoporte.setAlignmentX(SwingConstants.CENTER);
        btnSoporte.setBorder(new CompoundBorder( btnSoporte.getBorder(), new EmptyBorder(5, 0, 5, 0) ));
        btnSoporte.setIcon( new ImageIcon( getClass().getResource("/imagenes/soporte.png") ) );
        btnSoporte.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSoporte.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                if( chat == null ){
                    chat = new Chat();
                    chat.setVisible(true);
                } else {
                    chat.setVisible(true);
                }
                
            }
        });
        add(btnSoporte, "wrap");
        
        
        // BotonApagado
        btnApagar = new FitImageJLabel();
        btnApagar.setSize(320, 77);
        btnApagar.setBorder(new CompoundBorder( btnApagar.getBorder(), new EmptyBorder(5, 0, 5, 0) ));
        btnApagar.setIcon( new ImageIcon( getClass().getResource("/imagenes/apagarpc.png") ) );
        btnApagar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnApagar.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if( Util.confirmar(obj, "¿Está seguro(a) de Apagar la PC?") ){
                    Util.apagar_pc();
                }
            }
        });
        add(btnApagar, "wrap");
        
        
        height += 10;
        
        JLabel creditos = new JLabel("<html>Desarrollado por <b>Renzo Zamora</b><br/>Laboratorio de Cómputo FACEE</html>");
        creditos.setFont(creditos.getFont().deriveFont(~java.awt.Font.BOLD));
        height += creditos.getHeight() + 20;
        add(creditos, "align center");
        
        height += this.getLayout().preferredLayoutSize(this).height;

        setSize(340, height);
        setLocationRelativeTo(null);
       
    }
    
    public KeyListener shortcutKeyListener() {
        KeyListener listener = new KeyListener() {
            @Override
            public void keyReleased(KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_F2){
                    System.exit(0);
                } else if(evt.getKeyCode() == KeyEvent.VK_F10){
                    sincronizar(true);
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}
        };
        
        return listener;
    }
    
    public void centrar(JFrame vnt){
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        
        int w = vnt.getSize().width;
        int h = vnt.getSize().height;
        
        int x = (d.width-w)/2;
        int y = (d.height-h)/2;
        
        vnt.setLocation(x, y);
    }
    
    public void sincronizar(){
        sincronizar(false);
    }
    
    public void sincronizar(boolean forzar){
        if( (!forzar && !cfg.existe()) || forzar ){
            if( db==null ){
                db = new DB();
                db.conectar_mysql("localhost", "labos", "root");
                //db.conectar_ssql("172.17.21.200", "proyector", "uproyector", "facee3001");
            }
            
            if( !db.conectado() ){
                Util.error(this, "No se ha podido conectar a la BD.");
                return;
            }
            
            ArrayList<String[]> data = db.get_results( String.format("SELECT nombre, ip, proyector FROM salon WHERE ip=\"%s\"", Util.get_ip()) );
            if( data.size() >= 1 ){
                cfg.setOpcion("nombre", data.get(0)[0]);
                cfg.setOpcion("pc", data.get(0)[1]);
                cfg.setOpcion("proyector", data.get(0)[2]);
                cfg.guardarDatos();
                
                try {
                    Programa.restart(null);
                } catch (Exception e) {
                    Util.alerta(this, "Cierre la aplicación y vuélvala a abrir.");
                }
            } else {
                Util.error(this, "Su equipo no está habilitado para usar esta aplicación");
                System.exit(0);
            }
        }
    }
}
