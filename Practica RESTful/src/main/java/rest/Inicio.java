package rest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.sql.SQLException;
import java.util.Hashtable;



@Path("/users")
public class Inicio {
    @Context
    private UriInfo uriInfo;

    private static BDHandler conn = new BDHandler();


    public Inicio() {

    }
    /*
     * 	Obtener una lista de todos los usuarios de la red.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsuarios() {
        try {

            return Response.status(Response.Status.OK).entity(conn.getUsers().values()).header("Content-Location", uriInfo.getAbsolutePath()).build();

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD" + e).build();
        }
    }


    /*
     * 	Añadir un usuario nuevo.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user) throws SQLException {

        try {
            String username = user.getUsername();
            String fname = user.getFname();
            String lname = user.getLname();
            String email = user.getEmail();
            User user2 = conn.addUser(username, fname, lname, email);
            String location = uriInfo.getAbsolutePath() + "/" + user2.getUsername();
            return Response.status(Response.Status.CREATED).entity(user2).header("Location", location).header("Content-Location", location).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario\n").build();
        }catch (CampoVacio e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No se puede llamar al metodo con campos vacios\n").build();
        }
    }


    /*
     * Ver los datos básicos de un usuario.
     */
    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDataUser(@PathParam("username") String username) {
        try {
            boolean exist = conn.existUser(username);
            if(exist) {
                User user = conn.getDataUser(username);
                return Response.status(Response.Status.OK).entity(user).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
        }

    }

    /*
     * 	Publicar un nuevo estado.
     */
    @POST
    @Path("{nuser}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postStatus(@PathParam("nuser") String nuser, Status status){

        try {
            String username = status.getUsername();
            String content = status.getContent();
            boolean exist = conn.existUser(nuser);
            if(exist && nuser.equals(username)) {
                Status status2 = conn.postStatus(nuser, content);
                String location = uriInfo.getAbsolutePath() + "/" + status.getId();
                return Response.status(Response.Status.CREATED).entity(status2).header("Location", location).header("Content-Location", location).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no valido").build();
            }

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario\n").build();
        }catch (CampoVacio e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No se puede llamar al metodo con campos vacios\n").build();
        }
    }

    /*
     * 	Eliminar un estado.
     */
    @DELETE
    @Path("{username}/status/{statusid}")
    public Response deleteStatus(@PathParam("username") String username, @PathParam("statusid") String statusid){
        try {
            int id = Integer.parseInt(statusid);
            int affectedRows = conn.deleteStatus(username, id);
            if(affectedRows == 1) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
            }

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el status\n").build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No se puede parsear a entero").build();
        }
    }

    /*
     * 	Lista con todos los estados de un usuario y filtrar por fecha y cantidad
     */

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus(@QueryParam("username") @DefaultValue("empty") String username, @QueryParam("offset") @DefaultValue("1") String offset, @QueryParam("count") @DefaultValue("20") String count, @QueryParam("dt") @DefaultValue("2010-01-01") String dt)  {
        try {
            boolean exist = conn.existUser(username);
            int offsetInt = Integer.parseInt(offset);
            int countInt = Integer.parseInt(count);
            if(exist) {
                Hashtable<Integer, Status> getStatus = conn.getStatus(username, offsetInt, countInt, dt);
                return Response.status(Response.Status.OK).entity(getStatus.values()).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }
        }catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudieron obtener los estados\n").build();
        }catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No se puede parsear a entero").build();
        }catch (FormatoFecha e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Formato fecha no valido (yyyy/mm/dd))").build();
        }
    }
    /*
     * Lista con los estados de un usuario en un determinado periodo
     */

    @GET
    @Path("{username}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus2(@PathParam("username") String username, @QueryParam("dtstart") @DefaultValue("2010-01-01") String dtstart, @QueryParam("dtend") @DefaultValue("2020-01-01") String dtend) {
        try {
            boolean exist = conn.existUser(username);
            if(exist) {
                Hashtable<Integer, Status> getStatus = conn.getStatus2(username, dtstart, dtend);
                return Response.status(Response.Status.OK).entity(getStatus.values()).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }
        }catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudieron obtener los estados\n").build();
        }catch (FormatoFecha e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Formato fecha no valido (yyyy/mm/dd))").build();
        }
    }

    /*
     * 	Buscar posibles amigos en la red por nombre (patron).
     */
    @GET
    @Path("friends")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKindaFriends(@QueryParam("name") @DefaultValue("") String name) {
        try {
            Hashtable<Integer, User> friends = conn.getKindaFriends(name);
            return Response.status(Response.Status.OK).entity(friends.values()).build();
        }catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudieron obtener los posibles amigos\n").build();
        }
    }

    /*
     * Añadir un nuevo amigo
     */
    @POST
    @Path("{nuser}/friends")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addFriend(@PathParam("nuser") String nuser, Friend friend){

        try {
            String fname = friend.getFriendname();
            boolean existUser = conn.existUser(nuser);
            boolean existFriend = conn.existUser(fname);
            if(existUser && existFriend) {
                Friend friend2 = conn.addFriend(nuser, fname);
                String location = uriInfo.getAbsolutePath() + "/" + friend2.getId();
                return Response.status(Response.Status.CREATED).entity(friend2).header("Location", location).header("Content-Location", location).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario\n").build();
        }catch (CampoVacio e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No se puede llamar al metodo con campos vacios\n").build();
        }catch (AlreadyFriends e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Ya son amigos\n").build();
        }
    }

    /*
     * 	Eliminar a un amigo
     */
    @DELETE
    @Path("{username}/friends/{friendid}")
    public Response deleteFriend(@PathParam("username") String username, @PathParam("friendid") String friendid){
        try {
            int id = Integer.parseInt(friendid);
            int affectedRows = conn.deleteFriend(username, id);
            if(affectedRows == 1) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
            }

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el amigo\n").build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No se puede parsear a entero").build();
        }
    }

    /*
     * 	Obtener nuestros amigos y filtrar por nombre y cantidad
     */
    @GET
    @Path("{username}/friends")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFriends(@PathParam("username") String username, @QueryParam("name") @DefaultValue("") String name, @QueryParam("offset") @DefaultValue("1") String offset, @QueryParam("count") @DefaultValue("20") String count) {
        try {
            boolean exist = conn.existUser(username);
            int offsetInt = Integer.parseInt(offset);
            int countInt = Integer.parseInt(count);
            if(exist) {
                Hashtable<Integer, User> getFriends = conn.getFriends(username, name, offsetInt, countInt);
                return Response.status(Response.Status.OK).entity(getFriends.values()).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }
        }catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudieron obtener los estados\n").build();
        }catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No se puede parsear a entero").build();
        }
    }

    /*
     * 	Cambiar datos de perfil excepto el nombre de usuario.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{username}")
    public Response updateProfile(@PathParam("username") String username,User user) {
        try {
            String fname = user.getFname();
            String lname = user.getLname();
            String email = user.getEmail();
            boolean exist = conn.existUser(username);
            User user2 = conn.getDataUser(username);
            if(exist) {
                if(!fname.equals("")) {
                    user2 = conn.updateFname(username, fname);
                }
                if(!lname.equals("")) {
                    user2 = conn.updateLname(username, lname);
                }
                if(!email.equals("")) {
                    user2 = conn.updateEmail(username, email);
                }
                String location = uriInfo.getBaseUri() + "users/" + username;
                return Response.status(Response.Status.OK).entity(user2).header("Content-Location", location).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }

        }catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo actualizar el usuario\n").build();
        }

    }

    /*
     * 	Borrar nuestro perfil
     */

    @DELETE
    @Path("{username}")
    public Response deleteUser(@PathParam("username") String username){
        try {
            int affectedRows = conn.deleteUser(username);
            if(affectedRows == 1) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
            }

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el usuario\n").build();
        }
    }

    /*
     * 	Obtener estados de nuestros amigos.
     */
    @GET
    @Path("{username}/friends/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatusFriends(@PathParam("username") String username) {
        try {
            boolean exist = conn.existUser(username);
            if(exist) {
                Hashtable<Integer, Status> getStatusFriends = conn.getStatusFriends(username);
                return Response.status(Response.Status.OK).entity(getStatusFriends.values()).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }
        }catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudieron obtener los estados\n").build();
        }
    }

    /*
     * Obtener estados de nuestros amigos filtrados por contenido
     */
    @GET
    @Path("{username}/friends/status/content")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatusFriends(@PathParam("username") String username, @QueryParam("content") @DefaultValue("") String content) {
        try {
            boolean exist = conn.existUser(username);
            if(exist) {
                Hashtable<Integer, Status> getStatusContentFriends = conn.getStatusContentFriends(username, content);
                return Response.status(Response.Status.OK).entity(getStatusContentFriends.values()).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }
        }catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudieron obtener los estados\n").build();
        }
    }

    @GET
    @Path("{username}/profile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile(@PathParam("username") String username) {
        try {
            boolean exist = conn.existUser(username);
            if(exist) {
                Profile profile = conn.getProfile(username);
                return Response.status(Response.Status.OK).entity(profile).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
            }
        }catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo obtener el perfil\n").build();
        }
    }
}
