/*
  servidor_drun.java
  Ejecucion distribuida
  Carlos Pineda G. 2020
*/

import java.net.Socket;
import java.net.ServerSocket;
import java.lang.Thread;
import java.nio.ByteBuffer;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

class Servidor_drun
{
  static class Worker extends Thread
  {
    Socket conexion;

    Worker(Socket conexion)
    {
      this.conexion = conexion;
    }

    static void escribe_archivo(String nombre_archivo,byte[] buffer) throws Exception
    {
      FileOutputStream f = new FileOutputStream(nombre_archivo);
      try
      {
        f.write(buffer);
      }
      finally
      {
        f.close();
      }
    }

    static void ejecuta_jar(String nombre_archivo,int nodo) throws Exception
    {
      String[] cmd = new String[4];
      cmd[0] = "java";
      cmd[1] = "-jar";
      cmd[2] = nombre_archivo;
      cmd[3] = Integer.toString(nodo);

      // inicia la ejecucion del subproceso

      Process p = Runtime.getRuntime().exec(cmd);

      // stdInput y stdError permiten obtener la salida estandar y la salida de errores del subproceso
      
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      String s = null;

      // readLine bloquea mientras el subproceso esta ejecutando
      // cuando termina el subproceso readLine regresa null
     
      try
      { 
        while ((s = stdInput.readLine()) != null)
          System.out.println(s);

        while ((s = stdError.readLine()) != null)
          System.err.println(s);
      }
      catch (Exception e)
      {
	// si el thread es interrumpido entonces se destruye el subproceso
        p.destroy();
      }
    }

    // lee del DataInputStream todos los bytes requeridos

    static void read(DataInputStream f,byte[] b,int posicion,int longitud) throws Exception
    {
      while (longitud > 0)
      {
        int n = f.read(b,posicion,longitud);
        posicion += n;
        longitud -= n;
      }
    }

    public void run()
    {
      try
      {
        // abre los streams de entrada y salida

        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

        // recibe el numero de nodo
        int nodo = entrada.readInt();

        // recibe la longitud del nombre del archivo
        int longitud_nombre = entrada.readInt();

	// recibe el nombre del archivo
        byte[] buffer_1 = new byte[longitud_nombre];
        read(entrada,buffer_1,0,longitud_nombre);
        String nombre_archivo = new String(buffer_1,"UTF-8");

        // recibe la longitud del archivo
        int longitud_archivo = entrada.readInt();

        // recibe el archivo
        byte[] buffer_2 = new byte[longitud_archivo];
        read(entrada,buffer_2,0,longitud_archivo);

        // cierra los streams de entrada y salida y la conexion

        entrada.close();
        salida.close();
        conexion.close();

        escribe_archivo(nombre_archivo,buffer_2);
        ejecuta_jar(nombre_archivo,nodo);
      }
      catch (Exception e)
      {
        System.err.println(e.getMessage());
      }
    }
  }
  public static void main(String[] args) throws Exception
  {
    if (args.length != 1)
    {
      System.out.println("Se debe pasar el numero de nodo como parametro");
      System.exit(1);
    }

    int nodo = Integer.valueOf(args[0]);
    ServerSocket servidor = new ServerSocket(20000 + nodo);

    for (;;)
    {
      Socket conexion = servidor.accept();
      Worker w = new Worker(conexion);
      w.start();
    }
  }
}