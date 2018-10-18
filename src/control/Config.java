package control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 *
 * @author Renzo
 */
public class Config {
    
    private Properties prop = null;
    private static Config instance;
    
    private boolean exito = false;
    
    public static Config instance(){
        if( instance==null ){
            instance = new Config();
        }
        return instance;
    }
    
    private Config(){
        leerDatos();
    }
    
    public String archivo(){
        return System.getProperty("user.dir") + "/config.conf";
    }
    
    public boolean existe(){
        try {
            File archivo = new File( archivo() );
            return archivo.exists();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getOpcion(String clave){
        if( prop!=null )
            return prop.getProperty(clave);
        
        return null;
    }
    
    public String getOpcion(String clave, String predef){
        if( prop!=null )
            return prop.getProperty(clave, predef);
        
        return null;
    }
    
    public void setOpcion(String clave, String valor){
        if( prop==null ){
            prop = new Properties();
        }
        prop.setProperty(clave, valor);
    }
    
    private void leerDatos(){
        try {
            prop = new Properties();
            String propFileName = System.getProperty("user.dir") + "/config.conf";
            
            Path path = Paths.get(propFileName);

            try (InputStream inputStream = Files.newInputStream(path)) {
                if (inputStream != null) {
                    prop.load(inputStream);
                    exito = true;
                } else {
                    prop = null;
                }
            }
        } catch (Exception e) {
            prop = null;
            return;
        }
    }
    
    public void guardarDatos(){
        try {
            String propFileName = System.getProperty("user.dir") + "/config.conf";
            File archivo = new File(propFileName);
            if( !archivo.exists() ){
                archivo.createNewFile();
            }
            
            FileOutputStream output = new FileOutputStream(propFileName);
            
            prop.store(output, "Soporte FACEE");
            output.close();
        } catch (Exception e) {
            return;
        }
    }
    
    public boolean exito(){
        return exito;
    }
}
