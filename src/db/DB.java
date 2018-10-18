package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import control.Util;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class DB {
    private Connection con;
    private boolean ready = false;
    
    private ArrayList<String[]> resultados = null;
    private ArrayList<String> columnas = null;
    private int last_id = 0, num_results = 0, rows_affected = 0;
    
    public boolean conectar(String url){
        try {
            con = DriverManager.getConnection(url);
            ready = true;
            return ready;
        } catch (Exception e) {
            ready = false;
            return ready;
        }
    }
    
    public boolean conectar_sqlite(String archivo){
        return conectar( String.format("jdbc:sqlite:%s", archivo) );
    }
    
    public boolean conectar_mysql(String host, String db, String usuario){
        return conectar_mysql(host, db, usuario, "");
    }
    
    public boolean conectar_mysql(String host, String db, String usuario, String pass){
        String url = String.format("jdbc:mysql://%s/%s?user=%s&password=%s", host, db, usuario, pass);
        return conectar(url);
    }
    
    public boolean conectar_ssql(String host, String db){
        String url = String.format("jdbc:sqlserver://%s;databaseName=%s;integratedSecurity=true;", host, db);
        return conectar(url);
    }
    
    public boolean conectar_ssql(String host, String db, String usuario, String pass){
        String url = String.format("jdbc:sqlserver://%s;databaseName=%s;user=%s;password=%s;", host, db, usuario, pass);
        return conectar(url);
    }
    
    public void cerrar(){
        try {
            if( null!=con ) con.close();
            
            ready = false;
        } catch (Exception e) {
        }
    }
    
    public boolean conectado(){
        return ready;
    }
    
    public int query(String query){
        if( !ready ) return -1;
        
        try {
            
            if( Util.preg_match(query, "(create|alter|truncate|drop)") ){
                Statement st = con.createStatement();
                
                last_id = 0;
                num_results = 0;
                
                st.execute(query);
                st.close();
            } else if( Util.preg_match(query, "(insert|delete|update|replace)") ){
                PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                
                int rows_num = st.executeUpdate();
                
                num_results = 0;
                
                if( Util.preg_match(query, "(insert|replace)") ){
                    ResultSet rs = st.getGeneratedKeys();
                    
                    while( rows_num!=0 && !rs.next() ){
                        last_id = rs.getInt(1);
                    }
                    
                    rs.close();
                }
                
                rows_affected=st.getUpdateCount();
                st.close();
                
                return rows_affected;
            } else {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                
                format_columns(rs);
                format_results(rs);
                
                num_results = resultados.size();
                
                st.close();
                rs.close();
                return num_results;
            }
            return 0;
        } catch (Exception e) {
            last_id = 0;
            num_results = 0;
            return -1;
        }
    }
    
    public int insert(String tabla, Data data){
        if( !ready ) return -1;
        
        String cols = data.insert_cols();
        String vals = data.insert_data();
        
        String query = String.format("INSERT INTO `%s` (%s) VALUES %s", tabla, cols, vals);
        
        return query(query);
    }
    
    public int update(String tabla, Data data){
        if( !ready ) return -1;
        
        int n = 0;
        String where = data.where_data();
        
        for( int i=0; data.datos().size() > i; i++ ){
            String query = String.format("UPDATE `%s` SET %s %s", tabla, data.update_data(i), where);
            
            n = query(query);
        }
        
        return n;
    }
    
    private void format_columns(ResultSet rs){
        try {
            columnas = new ArrayList();
            
            ResultSetMetaData md = rs.getMetaData();
            int total = md.getColumnCount();
            
            for(int i=1; total>=i; i++){
                columnas.add( md.getColumnName(i) );
            }
        } catch (Exception e) {
            columnas = null;
        }
    }
    
    private void format_results(ResultSet rs){
        try {
            resultados = new ArrayList();
            
            while( rs.next() ){
                String[] fila = new String[columnas.size()];
                
                for(int i=0; fila.length>i; i++){
                    fila[i] = rs.getString(i+1);
                }
                
                resultados.add(fila);
            }
        } catch (Exception e) {
            resultados = null;
        }
    }
    
    private void get_url(){
        
    }
    
    public ArrayList<String[]> get_results(){
        return get_results(null);
    }
    
    public ArrayList<String[]> get_results(String query){
        if( query!=null ) query(query);
        
        return resultados;
    }
    
    public String get_var(){
        return get_var(null);
    }
    
    public String get_var(String query){
        if( query!=null ) query(query);
        
        return (resultados!=null) ? resultados.get(0)[0] : null;
    }
    
    public String[] get_row(int index){
        return get_val(index);
    }
    
    public String get_val(){
        return get_val(0, 0);
    }
    
    public String[] get_val(int index){
        return (resultados!=null) ? resultados.get(index) : null;
    }
    
    public String get_val(int index, int column){
        if( resultados!=null ){
            return resultados.get(index)[column];
        }
        return null;
    }
    
    public String get_val(int index, String column){
        if( resultados!=null ){
            return resultados.get(index)[ columnas.indexOf(column) ];
        }
        return null;
    }
    
    public int get_num_results(){
        return num_results;
    }
    
    public int get_last_id(){
        return last_id;
    }
    
    public int get_rows_affected(){
        return rows_affected;
    }
    
}
