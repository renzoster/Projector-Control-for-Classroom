package cliente;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import javax.swing.UIManager;
import ui.Ventana;

public class Programa {
    
    public static void setUISistema(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
    }

    public static void main(String[] args) {
        setUISistema();
        new Ventana().setVisible(true);
    }
    
    
    public static void restart(Runnable beforeRestart) throws IOException {
    try {
        // java binary
        String java = System.getProperty("java.home") + "/bin/java";
        // vm arguments
        List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        StringBuffer vmArgsOneLine = new StringBuffer();
        for (String arg : vmArguments) {
            // if it's the agent argument : we ignore it otherwise the
            // address of the old application and the new one will be in conflict
            if (!arg.contains("-agentlib")) {
                vmArgsOneLine.append(arg);
                vmArgsOneLine.append(" ");
            }
        }
        // init the command to execute, add the vm args
        final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);
        // program main and program arguments (be careful a sun property. might not be supported by all JVM) 
        String[] mainCommand = System.getProperty("sun.java.command").split(" ");
        // program main is a jar
        if (mainCommand[0].endsWith(".jar")) {
            // if it's a jar, add -jar mainJar
            cmd.append("-jar " + new File(mainCommand[0]).getPath());
        } else {
            // else it's a .class, add the classpath and mainClass
            cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
        }
        // finally add program arguments
        for (int i = 1; i < mainCommand.length; i++) {
            cmd.append(" ");
            cmd.append(mainCommand[i]);
        }
        
        Runtime.getRuntime().exec(cmd.toString());
        // execute the command in a shutdown hook, to be sure that all the
        // resources have been disposed before restarting the application
        /*Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    Runtime.getRuntime().exec(cmd.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
        // execute some custom code before restarting
        /*if (beforeRestart != null) {
            beforeRestart.run();
        }*/
        // exit
        System.exit(0);
    } catch (Exception e) {
        // something went wrong
        throw new IOException("Error while trying to restart the application", e);
    }
}
}
