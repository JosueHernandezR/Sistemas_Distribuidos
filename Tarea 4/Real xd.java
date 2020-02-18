import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Token {
    static DataInputStream entrada = null;  // Inicia en el nodo 0
    static DataOutputStream salida = null;  // 0 al 1
    static long token = 0;                  // Token para incrementar
    static int nodo;                        // Por el momento 4 nodos
    //Bandera para que no se bloqueen los nodos al momento de escribir
    static boolean primera_vez = true;

    static class Worker extends Thread {
        @Override
        public void run() {
            try {
                ServerSocket servidor = new ServerSocket(5000+nodo);
                Socket conexion = servidor.accept(); //Aqui se bloquea el thread hasta que recibe una conexión
                entrada = new DataInputStream(conexion.getInputStream());
            } catch (Exception e) {
                System.err.println("Error en el Worker: " + e);
            }
        }
    }

    public static void main(String[] args) {
        try {
            if(args.length != 1) {
                System.err.println("Se necesita el numero de nodo");
                System.exit(-1);
            }
            else {
                nodo = Integer.valueOf(args[0]);
                Worker w = new Worker();
                w.start();
                Socket conexion = null;
                for(;;) {
                    try {
                        conexion = new Socket("localhost",5000+((nodo+1)%4));
                        break;

                    } catch (Exception e) {
                        Thread.sleep(1000);
                    }
                }
                salida = new DataOutputStream(conexion.getOutputStream());
                w.join();
                for(;;) {
                    if(nodo == 0) {
                        if(primera_vez) {
                            primera_vez = false;
                        } else {
                            token = entrada.readLong();
                        }
                    } else {
                        token = entrada.readLong();
                    }
                    token++;

                    //Imprimira cada modulo de 1000 para el nodo 0 
                    //1001 para el nodo 1
                    //1002 para el nodo 2...
                    if ((token % (1000001)) == 0) {
                        System.out.println("Valor =" + token);
                    }
                    salida.writeLong(token);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en Main: " + e);
        }
    }
}