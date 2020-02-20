
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;


class Token {

    // Bandera para que no se bloqueen los nodos al momento de escribir
    static boolean primeravez = true;
    static DataInputStream entrada = null; // Inicia en el nodo 0
    static DataOutputStream salida = null; // 0 al 1
    static long token = 0; // Token para incrementar
    static int nodo; // Por el momento 4 nodos

    static class Worker extends Thread {
        @Override
        public void run() {
            try {
                ServerSocket servidor = new ServerSocket(4000 + nodo);
                Socket conexion = servidor.accept(); // Aqui se bloquea el thread hasta que recibe una conexión
                // System.out.println("Aqui debe de iniciar");
                entrada = new DataInputStream(conexion.getInputStream());
                // System.out.println("Lalalalal");
            } catch (Exception e) {
                System.err.println("Error del worker: " + e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            if (args.length != 1) {
                System.err.println("Se debe pasar el número de nodo");
                System.exit(-1);
            } else {

                nodo = Integer.valueOf(args[0]);
                Worker w = new Worker();
                w.start();
                Socket conexion = null;

                // Se queda bloqueado hasta que se pueda conectar
                // Se conecta cuando el siguiente nodo hace un acept y el ciclo se termina
                for (;;) {
                    // Si no falla entra y cierra.
                    try {
                        conexion = new Socket("localhost", 4000 + ((nodo + 1) % 4));
                        break;
                    }
                    // Si falla la conexion espera
                    catch (Exception e) {
                        Thread.sleep(100);
                    }
                }
                // Y por ende se obtiene el stream y espera a que el hilo local se termine y ya
                // esta conectado.
                // Ya se tiene el Stream de salida pero no se sabe si el thread se haya
                // terminado.
                salida = new DataOutputStream(conexion.getOutputStream());
                // Esperar a que el hilo termine
                // System.out.println("Llega a 1");
                w.join();
                // System.out.println("Pasó el Join");

                for (;;) {
                    if (nodo == 0) {
                        // Si es la primera vez no se lee se escribe para no bloquear la función
                        if (primeravez) {
                            primeravez = false;
                            // System.out.println("Primera vez");
                        } else
                            token = entrada.readLong();
                    } else
                        token = entrada.readLong();
                    token++;

                    // Imprimira cada modulo de 1000 para el nodo 0
                    // 1001 para el nodo 1
                    // 1002 para el nodo 2...
                    if ((token % (1000001)) == 0) {
                        System.out.println("Valor: " + token);
                    }
                    salida.writeLong(token);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en main: " + e);
        }
    }
}