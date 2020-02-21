/*
  drun.java
  Ejecucion distribuida
  Carlos Pineda G. 2020
*/

import java.net.Socket;
import java.lang.Thread;
import java.nio.ByteBuffer;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileReader;

class Drun
{
  static class Worker extends Thread
  {
    int nodo;
    String host;
    String nombre_programa;
    byte[] buffer;

    Worker(int nodo,String host,String nombre_programa,byte[] buffer)
    {
      this.nodo = nodo;
      this.host = host;
      this.nombre_programa = nombre_programa;
      this.buffer = buffer;
    }

    public void run()
    {
      try
      {
        // conecta con el servidor
        Socket conexion = new Socket(host,20000 + nodo);

        // abre los streams de entrada y salida

        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

	// envia el numero de nodo
        salida.writeInt(nodo);

        // envia la longitud del nombre del programa
        salida.writeInt(nombre_programa.length());

        // envia el nombre del programa
        salida.write(nombre_programa.getBytes());

        // envia la longitud del programa
        salida.writeInt(buffer.length);

        // envia el programa
        salida.write(buffer);
        salida.flush();

        // cierra los streams de entrada y salida y la conexion

        entrada.close();
        salida.close();
        conexion.close();
      }
      catch (Exception e)
      {
        System.err.println(e.getMessage());
      }
    }
  }

  static byte[] lee_archivo(String archivo) throws Exception
  {
    FileInputStream f = new FileInputStream(archivo);
    byte[] buffer;
    try
    {
      buffer = new byte[f.available()];
      f.read(buffer);
    }
    finally
    {
      f.close();
    }
    return buffer;
  }

  public static void main(String[] args) throws Exception
  {
    // verifica que se haya pasado como parametro el programa a ejecutar, en otro caso despliega error y termina

    if (args.length != 1)
    {
      System.err.println("Se debe pasar como parametro el programa a ejecutar");
      System.exit(1);
    }

    // lee el programa del disco, si no se pudo leer despliega error y termina

    byte[] buffer = null;

    try
    {
      buffer = lee_archivo(args[0]);
    }
    catch (Exception e)
    {
      System.err.println("No se pudo leer el programa");
      System.exit(2);
    }

    // lee el archivo de nodos
    // el archivo "hosts" contiene las direcciones IP o nombres de dominio de los nodos desde el nodo 0 en adelante

    try
    {
      BufferedReader f = new BufferedReader(new FileReader("hosts"));

      try
      {
        int nodo = 0;
        String host;

        // para cada host crea un thread pasando como parametros: el numero de nodo, la direccion del host, el nombre del programa y el programa

        while ((host = f.readLine()) != null)
        {
          Worker w = new Worker(nodo,host,args[0],buffer);
          w.start();
          nodo++;
        }
      }
      finally
      {
        f.close();
      }
    }
    catch (Exception e)
    {
      System.err.println("No se pudo leer el archivo de hosts");
      System.exit(3);
    }
  }
}