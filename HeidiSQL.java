package com.yxy.order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @name HeidiSQL Password Recovery
 * @version 1.0
 * @author adwind
 * @url http://adwind.com.mx
 * @support Todas las versiones de HeidiSQL
 * @date 27-03-2013
 * @Option Deben Usar su tablita de codigos ASCII para que vean todo el proceso XD
 */
public class HeidiSQL {

    /**
     * Este metodo recibe 3 parametros; 1.- Es la ruta completa de registro del
     * server 2.- Es el valor que estamos buscando. Ejemplo: Host 3.- Es el
     * delimitador que se usa para separar el resulatado (REG_SZ). ejemplo: Host
     * REG_SZ 127.0.0.1
     */
    public static String buscaClave(String server, String valor, String delimitador) {
        String tmp = "";
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"reg", "query", server, "/v", valor});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String t;
            while ((t = br.readLine()) != null) {
                //Si viene una linea que incia con HKEY o esta vacia continuamos, ya que
                //no nos interesa.
                if (t.isEmpty() || t.contains("HKEY")) {
                    continue;
                }
                //La linea es asi,  Host    REG_SZ    127.0.0.1
                String datos[] = t.split(delimitador);
                //Aqui obtenemos el dato más importante 127.0.0.1
                tmp = datos[1].trim();
            }
            //Destruimos el proceso creado
            p.destroy();
        } catch (IOException ex) {
            System.out.println("Error ejecutando el proceso!");
        }
        return tmp;
    }

    public static String HexToString(String t) {
        /*En el caso de HeidiSQL guarda las contraseñas en hexadecimal con el algoritmo cesar.
         El formato es asi.    PasswordEnHex:UltimoNumeroEsElNumeroDeSalto
         * Ejemplo1: 68686868687    la contraseña es 6868686868  y el salto es de 7
         * Ejemplo2: 696A6B8        la contraseña es 696A6B  y el salto es 8
         * Entonces para obtener la pass en texto normal, hay que seguir los siguientes pasos:
         * 1.- Separamos la pass en hexadecimal del salto
         * 2.- Convertir los caracteres hexadecimales a decimal
         * 3.- Al valor decimal que se obtuvo se le resta el numero de salto
         * 4.- Y luego obtenemos el caracter del numero que resulto.
         * Nota: Todo se puede hacer directo sin hacer ninguna conversión pero creo asi
         * queda mas explicado.
         */
        String tmp = "";
        int salto = Integer.parseInt(t.substring(t.length() - 1));
        for (int i = 0; i < t.length() - 1; i = i + 2) {
            int decimal = Integer.parseInt(t.charAt(i) + "" + t.charAt(i + 1), 16);
            decimal = decimal - salto;
            tmp += (char) decimal;
        }
        return tmp;
    }

    public static void main(String[] args) {
        try {
            //Creamos el proceso donde listamos todas los servers
            Process p = Runtime.getRuntime().exec(new String[]{"reg", "query", "HKEY_CURRENT_USER\\Software\\HeidiSQL\\Servers"});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String t;
            //Vamos letendo linea por linea
            System.out.println("Heidi SQL Recovery v1.0\n"
                    + "Author: Adwind\n\n");
            while ((t = br.readLine()) != null) {
                //Si regresa una linea vacia continuamos.
                if (t.isEmpty()) {
                    continue;
                }
                //La linea que nos regresa es así: HKEY_CURRENT_USER\Software\HeidiSQL\Servers\adwind
                //Se entonces enviamos la ruta completa al metodo BuscaClave.
                String host = buscaClave(t, "Host", "REG_SZ");
                String User = buscaClave(t, "User", "REG_SZ");
                String pass = HexToString(buscaClave(t, "Password", "REG_SZ"));
                String puerto = buscaClave(t, "Port", "REG_SZ");
                System.out.println(host);
                System.out.println(User);
                System.out.println(pass);
                System.out.println(puerto);
                System.out.println("\n");
                String []datos=new String[]{host,User,pass,puerto};
                System.out.println(datos.length);
            }
            p.destroy();
        } catch (IOException ex) {
            System.out.println("Error ejecutando el proceso!");
        }
    }
}