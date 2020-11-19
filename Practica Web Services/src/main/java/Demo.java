import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;

import java.io.IOException;
import java.util.Scanner;

public class Demo {

    public static void main(String[] args) throws IOException{
        Model datosLocal = RDFDataMgr.loadModel("data.nt");

        Scanner miTeclado = new Scanner(System.in);
        String opciones = "";
        String fallo = "Disculpe, su peticion no ha podido ser procesada. Int�ntelo de nuevo m�s tarde.";

        System.out.println("\n\nSingapore Environment Indicators\n");

        while (!opciones.equals("x") && !opciones.equals("X")) {
            System.out.println("Please select an option:");
            System.out.println("[1] Lista de fuentes de agua potable de Madrid\n[2] Realizar una b�squeda por distrito\n[3] Realizar una b�squeda por el nombre de la calle\n[x] Salir de la aplicaci�n");
            opciones = miTeclado.nextLine();

            if (opciones.equals("1")) {
                System.out.println("Para obtener la lista de FUENTES seleccione una opci�n:");
                System.out.println("[1] Mostrar las 10 primeras fuentes");
                System.out.println("[2] Mostrar todas las fuentes de la aplicaci�n");
                System.out.println("[v] Volver al men� principal");
                System.out.println("[x] Salir de la aplicaci�n");
                opciones = miTeclado.nextLine();

                if (opciones.equals("1")) {
                    System.out.println("Lista de las 10 primeras fuentes:");
                    //Aqui se mostrar� la lista con las 10 primeras fuentes
                    diezFuentes(datosLocal);
                } else if (opciones.equals("2")) {
                    System.out.println("Lista completa:");
                    //Aqui se mostrar� la lista completa de las fuentes
                    todasLasFuentes(datosLocal);
                } else if (opciones.equals("v")) {
                    // Volver al menu
                } else if (opciones.equals("x")) {
                    // salir
                    break;
                } else {
                    System.out.println(fallo);
                }
            } else if (opciones.equals("2")) {
                System.out.println("�Qu� DISTRITO le gustar�a buscar? Escriba el nombre del distrito o parte del nombre, o seleccione una opci�n:");
                System.out.println("[v] Volver al men� principal");
                System.out.println("[x] Salir de la aplicaci�n");
                opciones = miTeclado.nextLine();

                if (opciones.equals("v")) {
                    // Volver al menu
                } else if (opciones.equals("x")) {
                    // salir
                    break;
                } else {
                    //Aqui hace la consulta por distrito
                    System.out.println("Fuentes con el distrito " + opciones + ":");
                    fuentesDistrito(datosLocal,opciones);
                }
            } else if (opciones.equals("3")) {
                System.out.println("�Qu� DIRECCI�N le gustar�a buscar? Escriba el nombre de la direcci�n o parte de ella, o seleccione una opci�n:");
                System.out.println("[v] Volver al men� principal");
                System.out.println("[x] Salir de la aplicaci�n");
                opciones = miTeclado.nextLine();

                if (opciones.equals("v")) {
                    // Volver al menu
                } else if (opciones.equals("x")) {
                    // salir
                    break;
                } else {
                    //Aqui hace la consulta por calle
                    System.out.println("Fuentes con la direcci�n " + opciones + ":");
                    fuentesCalle(datosLocal,opciones);
                }
            } else if (opciones.equals("x")) {
                // salir
            } else {
                System.out.println(fallo);
            }
            System.out.println("\n\n");
        }
        System.out.println("\nGracias por usar nuestra aplicaci�n.");
    }

    public static void todasLasFuentes(Model datosLocal){

        String sparql = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX  base: <ns:>\n"
                + "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>\n"
                + "SELECT  ?CalleBien ?Fuente \n"
                + "WHERE\n"
                + "  {  ?Fuente base:hasIndicator ?Indicator.\n"
                + "    ?Indicator  base:indicatorName ?CalleBien.\n"
                + "  }";

                /*+ "SELECT  DISTINCT ?o \n"
                + "WHERE\n"
                + "  { ?o rdf:type base:Indicator .\n"
                + "  }";*/

        System.out.println("El programa aqui llega \n");

        Query consulta = QueryFactory.create(sparql);
        QueryExecution ejecucion = QueryExecutionFactory.create(consulta, datosLocal);
        ResultSet resultados = ejecucion.execSelect();

        while(resultados.hasNext()) {
            QuerySolution solucion = resultados.nextSolution();
            RDFNode pepe = solucion.get("?CalleBien");
            RDFNode paco = solucion.get("?Fuente");


            System.out.println("IndicatorName: " + pepe.toString());


        }
        // muestraDatos("o",sparql,datosLocal);
    }

    public static void diezFuentes(Model datosLocal){

        String sparql = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX  base: <http://www.semanticweb.org/ontologies/group37/ontology-4#>\n"
                + "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>\n"
                + "SELECT DISTINCT  ?o \n"
                + "WHERE\n"
                + "  { ?o rdf:type base:Fuente .\n"
                + "  }"
                + "LIMIT 5";

        muestraDatos("o",sparql,datosLocal);
    }
    public static void fuentesDistrito(Model datosLocal,String distritoDado){

        String distritoString = distritoDado;
        if (distritoString.length() >= 1)
            distritoString = distritoString.substring(1);

        String sparql = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX  base: <http://www.semanticweb.org/ontologies/group37/ontology-4#>\n"
                + "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>\n"
                + "SELECT  ?DistritoBien ?Fuente \n"
                + "WHERE\n"
                + "  { ?Distrito rdf:type base:Distrito .\n"
                + "    ?Distrito base:alberga ?Fuente.\n"
                + "    ?Distrito  rdfs:label ?DistritoBien.\n"
                + " FILTER regex(str(?DistritoBien), \""+ distritoString +"\")"
                + "  }";

        Query consulta = QueryFactory.create(sparql);
        QueryExecution ejecucion = QueryExecutionFactory.create(consulta, datosLocal);
        ResultSet resultados = ejecucion.execSelect();

        while(resultados.hasNext()) {
            QuerySolution solucion = resultados.nextSolution();
            RDFNode distrito = solucion.get("?DistritoBien");
            RDFNode fuente = solucion.get("?Fuente");


            System.out.println("Distrito: " + distrito.toString() + ", Fuente: " + fuente.toString());


        }
    }

    public static void fuentesCalle(Model datosLocal,String calleDada){

        String calleString = calleDada;
        calleString = calleString.toUpperCase();


        String sparql = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX  base: <http://www.semanticweb.org/ontologies/group37/ontology-4#>\n"
                + "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>\n"
                + "SELECT  ?CalleBien ?Fuente \n"
                + "WHERE\n"
                + "  {  ?Fuente base:seEncuentraEn ?Localizacion.\n"
                + "    ?Localizacion  base:direccion ?CalleBien.\n"
                + " FILTER regex(str(?CalleBien), \""+ calleString +"\")"
                + "  }";

        Query consulta = QueryFactory.create(sparql);
        QueryExecution ejecucion = QueryExecutionFactory.create(consulta, datosLocal);
        ResultSet resultados = ejecucion.execSelect();

        while(resultados.hasNext()) {
            QuerySolution solucion = resultados.nextSolution();
            RDFNode direccion = solucion.get("?CalleBien");
            RDFNode fuente = solucion.get("?Fuente");


            System.out.println("Direccion: " + direccion.toString() + ", Fuente: " + fuente.toString());


        }
    }

    public static void muestraDatos(String parametro, String sparql,Model datosLocal){

        Query consulta = QueryFactory.create(sparql);
        QueryExecution ejecucion = QueryExecutionFactory.create(consulta, datosLocal);
        ResultSet resultados = ejecucion.execSelect();

        System.out.println("Estoy en la función muestraDatos");

        while(resultados.hasNext()) {

            QuerySolution solucion = resultados.nextSolution();
            RDFNode countryName = solucion.get("?" + parametro);

            System.out.println(countryName.toString());


        }
    }
}
