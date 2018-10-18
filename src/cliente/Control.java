
package cliente;

import control.*;
import proyector.*;
import chat.*;

/**
 *
 * @author Renzo
 */
public class Control {
    private static final String STYLE_SHEET = ".chat-box { margin: 2px; }"
			+ ".chat-box p { display: block; padding: 7px; margin-top: 2px; margin-bottom: 2px; word-wrap: break-word;}"
			+ ".chat-msg1 { background-color: #add8e6; }" + ".chat-msg2 { background-color: #e6adb5; }" + ".alert{background-color:red; color:#ffffff; padding:10px;}";
	private static final String DEFAULT_HTML_FORMAT = "<style>" + STYLE_SHEET + "</style>"
			+ "<div id=content class=chat-box> </div>";
        
    // Chat
    public SocketClient client;
    public Thread clientThread;
    
    // Proyector
    Proyector proyector;
    Ventana vnt;
    Config cfg;
    
    boolean ready = false;
    
    Thread hiloA;

    Control(Ventana vnt) {
        this.vnt = vnt;
        this.vnt.mensajes.setText(DEFAULT_HTML_FORMAT);
    }
    
    public void iniciar() {
        cfg = Config.instance();
        
        System.out.println(Util.get_ip());
        
        if( !cfg.exito() ){
            Util.error(vnt, "El aplicativo no está configurado.");
            System.exit(0);
        } 
        /*proyector = new PJLink();
        proyector.setIP( cfg.getOpcion("proyector") );
        proyector.setPuerto( cfg.getOpcion("puerto") );
        proyector.conectar();
        
        // Definir boton
        if( proyector.getEncendido() ){
            vnt.btnEncender.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/apagar.png")));
        } else {
            vnt.btnEncender.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/encender.png")));
        }
        
        vnt.btnEncender.setEnabled(true);*/
        vnt.btnSoporte.setEnabled(true);
        vnt.btnApagar.setEnabled(true);
        ready = true;
    }
    
    public void btnProyector(){
        if( !ready ){
            Util.mensaje(vnt, "Espere un momento. El programa está cargando.");
            return;
        }
        
        if( !proyector.conectado() ){
            Notificacion.mostrar(vnt, "Proyector no conectado a la red.", "Soporte URP", Notificacion.ERROR);
            return;
        }
        
        if( proyector.getEncendido() ){
            vnt.btnEncender.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/encender.png")));
            proyector.setEncendido(false);
        } else {
            vnt.btnEncender.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/apagar.png")));
            proyector.setEncendido(true);
        }
        
    }
    
    public void btnSoporte(){
        if( client==null ){
            
        
            try{
                client = new SocketClient(vnt, this);
                clientThread = new Thread(client);
                clientThread.start();
                client.send(new Mensaje("test", "testUser", "testContent", "SERVER"));
                //client.send(new Mensaje("login", "cliente", "", "SERVER"));
            }
            catch(Exception ex){
                
                Util.error(vnt, "[Application > Me] : Server not found");
                System.out.println(ex.getMessage());
            }
        
        }
    }
    
    public void btnEnviar(){
        String msg = vnt.mensaje.getText();
        String target = "All";
        
        if(!msg.isEmpty() && !target.isEmpty()){
            vnt.mensaje.setText("");
            client.send(new Mensaje("message", "nombre_cliente", msg, target));
        }
    }
    
    public void btnApagar(){
        if( !ready ){
            Util.mensaje(vnt, "Espere un momento. El programa está cargando.");
            return;
        }
        
        if( Util.confirmar(vnt, "¿Está seguro(a) de Apagar la PC?") ){
            Util.apagar_pc();
        }
    }
    
    public boolean ready(){
        return ready;
    }
    
    
}
