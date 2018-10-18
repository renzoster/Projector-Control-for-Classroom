package db;

import control.Util;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Renzo
 */
public class Data {        
    private ArrayList<String> columnas = null;
    private ArrayList<String[]> datos = null;
    private ArrayList<String> where = null;
    
    public Data(){    
    }
    
    public ArrayList<String> columnas(){
        return columnas;
    }
    
    public ArrayList<String[]> datos(){
        return datos;
    }
    
    public ArrayList<String> donde(){
        return where();
    }
    
    public ArrayList<String> where(){
        return where;
    }
    
    
    public void columnas(String ... cols){
        columnas = new ArrayList();
        
        for( String col : cols ){
            columnas.add(col);
        }
    }
    
    public void datos(String ... data){
        if( columnas==null ) return;
        if( datos==null ) datos = new ArrayList();
        
        ArrayList<String> temp = new ArrayList();
        
        temp.addAll(Arrays.asList(data));
        
        datos.add( temp.toArray(new String[temp.size()]) );
    }
    
    public void donde(String ... condicion){
        where(condicion);
    }
    
    public void where(String ... condicion){
        String output = "";
        
        switch (condicion.length) {
            case 1:
                if( Util.preg_match(condicion[2], "(and|or)") ){
                    output = condicion[0].toUpperCase();
                }
                break;
            case 2:
                output = String.format("%s = \"%s\"", condicion[0], condicion[1]);
                break;
            case 3:
                if( Util.preg_match(condicion[2], "(in)") ){
                    output = String.format("%s IN (\"%s\")", condicion[0], implode("\",", (String[])array_splice(condicion, 2) ) );
                }
                break;
            default:
                return;
        }
        
        where.add(output);
    }
    
    public void cerrar(){
        where = columnas = null;
        datos = null;
    }
    
    public String insert_cols(){
        if( columnas==null || datos==null ) return "";
        
        return implode( ",", columnas.toArray() );
    }
    
    public String insert_data(){
        if( columnas==null || datos==null ) return "";
        
        StringBuilder output = new StringBuilder();
        
        for( int i=0; datos.size() > i; i++ ){
            if( i!=0 ){
                output.append(", ");
            }
            output.append("(\"");
            output.append( implode("\",\"", datos.get(i), true) );
            output.append("\")");
        }
        
        return output.toString();
    }
    
    public String update_data(){
        return update_data(0);
    }
    
    public String update_data(int index){
        if( columnas==null || datos==null ) return "";
        
        StringBuilder output = new StringBuilder();
        String []fila = datos.get(index);
        
        for( int i=0; columnas.size()>i; i++ ){
            if( i!=0 )
                output.append(", ");
            output.append( columnas.get(i) );
            output.append("=\"");
            output.append( fila[i] );
            output.append("\"");
        }
        
        return output.toString();
    }
    
    public String where_data(){
        if( where==null ) return "";
        
        return implode(" ", where.toArray(new String[where.size()]), true );
    }
    
    public static Object array_splice(Object[] array, int start){
        return array_splice(array, start, array.length);
    }
    
    public static Object array_splice(Object[] array, int start, int end){
        return Arrays.copyOfRange(array, start, end);
    }
    
    public static String implode(String glue, Object[] array){
        return implode(glue, array, false);
    }
    
    public static String implode(String glue, Object[] array, boolean filter){
        StringBuilder output = new StringBuilder();
        
        for( int i=0; array.length > i; i++ ){
            if( i!=0 ){
                output.append(glue);
            }
            output.append( array[i].toString().replace("\"", "\\\"") );
        }
        
        return output.toString();
    }
}
