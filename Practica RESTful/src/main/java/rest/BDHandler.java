package rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/*
 * Clase que utilizaremos para manejar la base de datos
 */

public class BDHandler {

    private Connection con;
    private void conectar() throws SQLException {
        try {

            String drv = "com.mysql.jdbc.Driver";
            Class.forName(drv).newInstance();
            String serverAddress = "localhost:3306";
            String user = "restuser";
            String pwd = "restuser";
            String url = "jdbc:mysql://"+serverAddress+"?useTimezone=true&serverTimezone=UTC";
            con=DriverManager.getConnection(url, user, pwd);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }



    public Hashtable<Integer, User> getUsers() throws SQLException{
        Hashtable<Integer, User> usuario = new Hashtable();
        if(con==null) {
            conectar();
        }


        PreparedStatement pst = con.prepareStatement("SELECT * FROM upmsocial.Usuarios;");
        ResultSet rs = pst.executeQuery();
        int i =1;
        while(rs.next()) {
            User user = new User(rs.getString("n_usuario"), rs.getString("nombre"), rs.getString("apellido"), rs.getString("email"), rs.getString("fecha_usuario"));
            usuario.put(i,user);
            i++;

        }
        pst.close();
        return usuario;
    }

    public Integer numUsers() throws SQLException {
        if(con==null) {
            conectar();
        }
        PreparedStatement pst1 = con.prepareStatement("SELECT count(*) FROM upmsocial.Usuarios;");
        ResultSet rs1 = pst1.executeQuery();

        int id = 0;
        if(rs1.next())
            id = rs1.getInt(1);
        pst1.close();
        return id;
    }

    public User addUser(String n_usuario, String nombre, String apellido, String email) throws SQLException, CampoVacio {
        if(n_usuario.equals("") || nombre.equals("") || apellido.equals("") || email.equals("")) {
            throw new CampoVacio();
        }


        Calendar c = Calendar.getInstance();
        String fecha = Integer.toString(c.get(Calendar.YEAR))+"-"+Integer.toString(c.get(Calendar.MONTH))+"-"+Integer.toString(c.get(Calendar.DATE));

        if(con==null)
            conectar();

        PreparedStatement pst = con.prepareCall("INSERT INTO `upmsocial`.`Usuarios` (`n_usuario`, `nombre`, `apellido`, `email`, `fecha_usuario`) VALUES (?,?,?,?,?);");
        pst.setString(1, n_usuario);
        pst.setString(2, nombre);
        pst.setString(3, apellido);
        pst.setString(4, email);
        pst.setString(5, fecha);
        pst.executeUpdate();
        pst.close();
        User user = new User(n_usuario,nombre,apellido,email,fecha);
        return user;
    }

    public boolean existUser(String nuser) throws SQLException {
        if(con==null)
            conectar();

        PreparedStatement pst1 = con.prepareStatement("SELECT * FROM upmsocial.Usuarios WHERE n_usuario = ?;");
        pst1.setString(1, nuser);
        ResultSet rs1 = pst1.executeQuery();
        if(rs1.next()) {
            pst1.close();
            return true;
        }
        else {
            pst1.close();
            return false;
        }
    }

    public User getDataUser(String nuser) throws SQLException{
        if(con==null)
            conectar();

        User user = null;
        PreparedStatement pst1 = con.prepareStatement("SELECT * FROM upmsocial.Usuarios WHERE n_usuario = ?;");
        pst1.setString(1, nuser);
        ResultSet rs1 = pst1.executeQuery();
        if(rs1.next()) {
            user = new User(rs1.getString("n_usuario"), rs1.getString("nombre"), rs1.getString("apellido"), rs1.getString("email"), rs1.getString("fecha_usuario"));
        }

        pst1.close();
        return user;
    }

    public Status postStatus(String username, String content) throws SQLException, CampoVacio {
        if(content.equals("empty")) {
            throw new CampoVacio();
        }

        Calendar c = Calendar.getInstance();
        String fecha = Integer.toString(c.get(Calendar.YEAR))+"-"+Integer.toString(c.get(Calendar.MONTH))+"-"+Integer.toString(c.get(Calendar.DATE));
        if(con==null)
            conectar();
        Status status = new Status(0,username,content,fecha);
        PreparedStatement pst = con.prepareStatement("INSERT INTO `upmsocial`.`Estado` (`n_usuario`, `contenido`, `fecha_estado`) VALUES (?,?,?);", Statement.RETURN_GENERATED_KEYS);
        pst.setString(1, username);
        pst.setString(2, content);
        pst.setString(3, fecha);
        pst.executeUpdate();
        ResultSet generatedID = pst.getGeneratedKeys();
        if (generatedID.next()) {
            status.setId(generatedID.getInt(1));
        }
        pst.close();
        return status;
    }

    public int deleteStatus(String username, int idstatus) throws SQLException{
        if(con==null)
            conectar();

        PreparedStatement pst = con.prepareStatement("DELETE FROM `upmsocial`.`Estado` WHERE id_estado=? && n_usuario=?;");
        pst.setInt(1, idstatus);
        pst.setString(2, username);
        int affectedRows = pst.executeUpdate();
        pst.close();
        return affectedRows;
    }

    boolean checkDate(String dt) {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            formatoFecha.setLenient(false);
            formatoFecha.parse(dt);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    Hashtable<Integer, Status> getStatus(String username,int offset,int count,String dt) throws SQLException, FormatoFecha{
        if(con==null)
            conectar();
        boolean checkDate = checkDate(dt);
        if(checkDate) {
            Hashtable<Integer, Status> statements = new Hashtable<Integer, Status>();
            PreparedStatement pst = con.prepareStatement("SELECT * FROM upmsocial.Estado WHERE n_usuario = ? && fecha_estado>? order by fecha_estado LIMIT ?,?");
            pst.setString(1, username);
            pst.setString(2, dt);
            pst.setInt(3, offset-1);
            pst.setInt(4, count);
            ResultSet rs = pst.executeQuery();
            int i = 1;
            while(rs.next()) {
                Status status = new Status(rs.getInt("id_estado"), rs.getString("n_usuario"), rs.getString("contenido"), rs.getString("fecha_estado"));
                statements.put(i, status);
                i++;
            }
            pst.close();
            return statements;
        }
        else {
            throw new FormatoFecha();
        }
    }

    Hashtable<Integer, Status> getStatus2(String username, String dtstart, String dtend) throws SQLException, FormatoFecha{
        if(con==null)
            conectar();
        boolean checkStart = checkDate(dtstart);
        boolean checkEnd = checkDate(dtend);
        if(checkStart && checkEnd) {
            Hashtable<Integer, Status> statements = new Hashtable<Integer, Status>();
            PreparedStatement pst = con.prepareStatement("SELECT * FROM upmsocial.Estado WHERE n_usuario = ? && fecha_estado>? && fecha_estado<?;");
            pst.setString(1, username);
            pst.setString(2, dtstart);
            pst.setString(3,dtend);
            ResultSet rs = pst.executeQuery();
            int i = 1;
            while(rs.next()) {
                Status status = new Status(rs.getInt("id_estado"), rs.getString("n_usuario"), rs.getString("contenido"), rs.getString("fecha_estado"));
                statements.put(i, status);
                i++;
            }
            pst.close();
            return statements;
        }
        else {
            throw new FormatoFecha();
        }

    }

    Hashtable<Integer, User> getKindaFriends(String name) throws SQLException{
        if(con==null)
            conectar();
        Hashtable<Integer, User> friends = new Hashtable<Integer, User>();
        PreparedStatement pst = con.prepareStatement("SELECT * FROM upmsocial.Usuarios;");
        ResultSet rs = pst.executeQuery();
        int i = 1;
        while(rs.next()) {
            if(rs.getString("nombre").contains(name)) {
                User user = new User(rs.getString("n_usuario"),rs.getString("nombre"), rs.getString("apellido"), rs.getString("email"), rs.getString("fecha_usuario") );
                friends.put(i, user);
                i++;
            }
        }
        return friends;
    }

    public boolean noFriends(String username, String friendname) throws SQLException {
        if(con==null)
            conectar();

        PreparedStatement pst = con.prepareStatement("SELECT * FROM upmsocial.Amigos WHERE n_usuario = ? && n_amigo = ?;");
        pst.setString(1, username);
        pst.setString(2, friendname);
        ResultSet rs = pst.executeQuery();
        if(rs.next()) {
            pst.close();
            return false;
        }
        else {
            pst.close();
            return true;
        }

    }

    public Friend addFriend(String username, String friendname) throws SQLException, CampoVacio, AlreadyFriends {
        if(friendname.equals("")) {
            throw new CampoVacio();
        }
        if(con==null)
            conectar();
        if(noFriends(username, friendname)) {
            Friend friend = new Friend(0,username,friendname);
            PreparedStatement pst = con.prepareStatement("INSERT INTO `upmsocial`.`Amigos` (`n_usuario`, `n_amigo`) VALUES (?,?);", Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, username);
            pst.setString(2, friendname);
            pst.executeUpdate();
            ResultSet generatedID = pst.getGeneratedKeys();
            if(generatedID.next()) {
                friend.setId(generatedID.getInt(1));
            }
            pst.close();
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO `upmsocial`.`Amigos` (`n_usuario`, `n_amigo`) VALUES (?,?);");
            pst1.setString(1, friendname);
            pst1.setString(2, username);
            pst1.executeUpdate();
            pst1.close();
            return friend;
        }
        else {
            throw new AlreadyFriends();
        }

    }
    public int deleteFriend(String username, int friendid) throws SQLException{
        if(con==null)
            conectar();
        String friendname = "";
        PreparedStatement pst = con.prepareStatement("SELECT * FROM upmsocial.Amigos WHERE id_amigos = ?;");
        pst.setInt(1, friendid);
        ResultSet rs = pst.executeQuery();
        if(rs.next()) {
            friendname=rs.getString("n_amigo");
        }
        pst.close();
        PreparedStatement pst1 = con.prepareStatement("DELETE FROM `upmsocial`.`Amigos` WHERE id_amigos = ? && n_usuario = ?;");
        pst1.setInt(1, friendid);
        pst1.setString(2, username);
        int affectedRows = pst1.executeUpdate();
        pst1.close();
        PreparedStatement pst2 = con.prepareStatement("DELETE FROM `upmsocial`.`Amigos` WHERE n_usuario = ? && n_amigo = ?;");
        pst2.setString(1, friendname);
        pst2.setString(2, username);
        pst2.executeUpdate();
        pst2.close();
        return affectedRows;
    }


    public Hashtable<Integer, User>  getFriends(String username,String name,int start,int end) throws SQLException{
        Hashtable<Integer, String> friendnames = new Hashtable<Integer, String>();
        Hashtable<Integer, User> friends = new Hashtable<Integer, User>();
        PreparedStatement pst = con.prepareStatement("SELECT * FROM upmsocial.Amigos WHERE n_usuario = ? order by id_Amigos LIMIT ?,?");
        pst.setString(1, username);
        pst.setInt(2, start-1);
        pst.setInt(3, end);
        ResultSet rs = pst.executeQuery();
        int i=1;
        while(rs.next()) {
            if(rs.getString("n_amigo").contains(name)) {
                friendnames.put(i, rs.getString("n_amigo"));
                i++;
            }
        }
        pst.close();
        for(int j=1; j<=friendnames.size(); j++) {
            PreparedStatement pst1 = con.prepareStatement("SELECT * FROM upmsocial.Usuarios WHERE n_usuario = ?;");
            pst1.setString(1, friendnames.get(j));
            ResultSet rs1 = pst1.executeQuery();
            if(rs1.next()) {
                User user = new User(rs1.getString("n_usuario"), rs1.getString("nombre"), rs1.getString("apellido"), rs1.getString("email"), rs1.getString("fecha_usuario"));
                friends.put(j, user);
            }
            pst1.close();
        }
        return friends;
    }

    public User updateFname(String username, String fname) throws SQLException {
        PreparedStatement pst = con.prepareStatement("UPDATE `upmsocial`.`Usuarios` SET `nombre`=? WHERE `n_usuario`=?;");
        pst.setString(1, fname);
        pst.setString(2, username);
        pst.executeUpdate();
        pst.close();
        User user = new User();
        PreparedStatement pst1 = con.prepareStatement("SELECT * FROM upmsocial.Usuarios WHERE n_usuario = ?;");
        pst1.setString(1, username);
        ResultSet rs1 = pst1.executeQuery();
        if(rs1.next()) {
            user.setUsername(username);
            user.setFname(rs1.getString("nombre"));
            user.setLname(rs1.getString("apellido"));
            user.setEmail(rs1.getString("email"));
            user.setDate(rs1.getString("fecha_usuario"));
        }
        return user;
    }


    public User updateLname(String username, String lname) throws SQLException {
        PreparedStatement pst = con.prepareStatement("UPDATE `upmsocial`.`Usuarios` SET `apellido`=? WHERE `n_usuario`=?;");
        pst.setString(1, lname);
        pst.setString(2, username);
        pst.executeUpdate();
        pst.close();
        User user = new User();
        PreparedStatement pst1 = con.prepareStatement("SELECT * FROM upmsocial.Usuarios WHERE n_usuario = ?;");
        pst1.setString(1, username);
        ResultSet rs1 = pst1.executeQuery();
        if(rs1.next()) {
            user.setUsername(username);
            user.setFname(rs1.getString("nombre"));
            user.setLname(rs1.getString("apellido"));
            user.setEmail(rs1.getString("email"));
            user.setDate(rs1.getString("fecha_usuario"));
        }
        return user;
    }

    public User updateEmail(String username, String email) throws SQLException {
        PreparedStatement pst = con.prepareStatement("UPDATE `upmsocial`.`Usuarios` SET `email`=? WHERE `n_usuario`=?;");
        pst.setString(1, email);
        pst.setString(2, username);
        pst.executeUpdate();
        pst.close();
        User user = new User();
        PreparedStatement pst1 = con.prepareStatement("SELECT * FROM upmsocial.Usuarios WHERE n_usuario = ?;");
        pst1.setString(1, username);
        ResultSet rs1 = pst1.executeQuery();
        if(rs1.next()) {
            user.setUsername(username);
            user.setFname(rs1.getString("nombre"));
            user.setLname(rs1.getString("apellido"));
            user.setEmail(rs1.getString("email"));
            user.setDate(rs1.getString("fecha_usuario"));
        }
        return user;
    }

    public int deleteUser(String username) throws SQLException{
        if(con==null)
            conectar();
        PreparedStatement pst1 = con.prepareStatement("DELETE FROM `upmsocial`.`Amigos` WHERE n_usuario = ? || n_amigo = ?;");
        pst1.setString(1, username);
        pst1.setString(2, username);
        pst1.executeUpdate();
        pst1.close();

        PreparedStatement pst2 = con.prepareStatement("DELETE FROM `upmsocial`.`Estado` WHERE n_usuario = ?");
        pst2.setString(1, username);
        pst2.executeUpdate();
        pst2.close();

        PreparedStatement pst3 = con.prepareStatement("DELETE FROM `upmsocial`.`Usuarios` WHERE n_usuario = ?");
        pst3.setString(1, username);
        int affectedRows = pst3.executeUpdate();
        pst3.close();
        return affectedRows;

    }

    public Hashtable<Integer, Status>  getStatusFriends(String username) throws SQLException{
        if(con==null)
            conectar();
        Hashtable<Integer, Status> statements = new Hashtable<Integer, Status>();
        Hashtable<Integer, User> friends = getFriends(username, "", 1, 30);
        for(int i = 1; i<=friends.size(); i++) {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM upmsocial.Estado WHERE n_usuario = ? order by fecha_estado desc LIMIT 0,1; ");
            pst.setString(1, friends.get(i).getUsername());
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                Status status = new Status(rs.getInt("Id_estado"), rs.getString("n_usuario"), rs.getString("contenido"), rs.getString("fecha_estado"));
                statements.put(i, status);
            }
            pst.close();
        }
        return statements;
    }
    public Hashtable<Integer, Status>  getStatusContentFriends(String username, String content) throws SQLException{
        if(con==null)
            conectar();
        Hashtable<Integer, Status> statements = new Hashtable<Integer, Status>();
        Hashtable<Integer, User> friends = getFriends(username, "", 1, 30);
        for(int i = 1; i<=friends.size(); i++) {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM upmsocial.Estado WHERE n_usuario = ?;");
            pst.setString(1, friends.get(i).getUsername());
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                if(rs.getString("contenido").contains(content)) {
                    Status status = new Status(rs.getInt("Id_estado"), rs.getString("n_usuario"), rs.getString("contenido"), rs.getString("fecha_estado"));
                    statements.put(i, status);
                }
            }
            pst.close();
        }
        return statements;
    }

    public Profile  getProfile(String username) throws SQLException{
        if(con==null)
            conectar();
        Profile profile = new Profile();
        Status userStatus = new Status();
        int friendsNumber = 0;
        ArrayList<Status> friendStatus = new ArrayList<Status>();

        User user = getDataUser(username);

        PreparedStatement pst4 = con.prepareStatement("SELECT * FROM upmsocial.Estado WHERE n_usuario = ? order by fecha_estado desc LIMIT 0,1; ");
        pst4.setString(1, username);
        ResultSet rs = pst4.executeQuery();
        if(rs.next()) {
            userStatus.setId(rs.getInt("id_estado"));
            userStatus.setUsername(rs.getString("n_usuario"));
            userStatus.setContent(rs.getString("contenido"));
            userStatus.setDate(rs.getString("fecha_estado"));
        }
        pst4.close();

        PreparedStatement pst1 = con.prepareStatement("SELECT count(*) FROM upmsocial.Amigos WHERE n_usuario = ?;");
        pst1.setString(1, username);
        ResultSet rs1 = pst1.executeQuery();
        if(rs1.next())
            friendsNumber = rs1.getInt("count(*)");
        pst1.close();

        PreparedStatement pst2 = con.prepareStatement("SELECT * FROM upmsocial.Amigos WHERE n_usuario = ?");
        pst2.setString(1, username);
        ResultSet rs2 = pst2.executeQuery();
        Hashtable<Integer, String> friendNames = new Hashtable<Integer, String>();
        int j = 1;
        while(rs2.next()) {
            friendNames.put(j, rs2.getString("n_amigo"));
            j++;
        }
        pst2.close();


        String sql = "WHERE n_usuario = '"+ friendNames.get(1)+"'";
        for(int i = 2; i<=friendNames.size(); i++) {
            sql = sql + " || n_usuario = '"+ friendNames.get(i)+"'";
        }


        PreparedStatement pst3 = con.prepareStatement("SELECT * FROM upmsocial.Estado "+ sql +" order by fecha_estado desc LIMIT 0,10; ");
        ResultSet rs3 = pst3.executeQuery();
        while(rs3.next()) {
            Status status = new Status (rs3.getInt("id_estado"), rs3.getString("n_usuario"), rs3.getString("contenido"), rs3.getString("fecha_estado"));
            friendStatus.add(status);
        }
        pst3.close();

        profile.setUser(user);
        profile.setLastStatus(userStatus);
        profile.setFriendsNumber(friendsNumber);
        profile.setFriendStatus(friendStatus);

        return profile;


    }

    public static void main(String args[]) throws Exception{
        //System.out.println(new BDHandler().getKindaFriends(""));



    }
}