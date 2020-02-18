import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Pi {

    static int n = 0;
    //El objeto lock sirve para sincronizar y evita que dos nodos quieran tomar el recuroso al mismo tiempo
    static Object lock = new Object();
    static double pi = 0;
    static final int tam = 35000;

    /**
     * La Clase Worker extiende de Thread para recibir mas de un parámetro
     */
    static class Worker extends Thread {

        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        /**
         * Esta es la función que permite ejecutar el thread cuando este inica
         */
        public void run() {
            try {
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                double x = entrada.readDouble();
                // Inicializa y sincroniza los datos para calcular Pi
                synchronized (lock) {
                    pi += x;
                    n++;
                }
                // Esto faltaba para funcionar
                entrada.close();
                salida.close();
                conexion.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
    /**
     * La función Main ejecuta el programa, se ejecuta al inicio.
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Se espera el número de nodo");
            System.exit(-1);
        }
        // Obtiene el valor del nodo al momento de ejecutar el programa
        int nodo = Integer.valueOf(args[0]);
        // Si el nodo inicia con 0 se ejecuta el servidor y empieza a enviar y recibir los datos de 
        // Los siguientes nodos para hacer la suma al final del valor de Pi
        if (nodo == 0) {
            ServerSocket servidor = new ServerSocket(5000);
            double suma = 0;

            for (int i = 0; i < 3; i++) {
                Socket conexion = servidor.accept();
                Worker W = new Worker(conexion);
                // Echar a andar el thread
                W.start();
            }

            for (int i = 0; i < tam; i++) {
                double data = 8 * i + 1;
                suma += 1 / data;
            }
            //Al momento de recibir los datos, los sincroniza y los suma a la variable Pi
            synchronized (lock) {
                pi += suma;
                n++;
            }
            //Al momento en que se conectan los 4 nodos termina la espera.
            for (;;) {
                synchronized (lock) {
                    if (n == 4)
                        break;
                }
                Thread.sleep(100);
            }

            System.out.println("El valor aproximado de Pi es: " + 4 * pi);
        } 
        // Si el nodo es mayor a cero, se ejecuta el siguiente código para calcular Pi de acuerdo al
        // numero de nodo.
        else {
            Socket conexion = new Socket("localhost", 5000);
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            double suma = 0;
            int i = 0;
            /**
             * Función para calcular Pi de acuerdo al numéro de nodo
             * Para después sumarlo.
             */
            for (i = 0; i < tam; i++) {
                double data = 8 * (double) i + (2 * (double) nodo + 1);
                suma += 1 / data;
            }
            
            // Cómo se usa una serie trigonometríca para calcular pi.
            // Es necesario invertir el signo si el numero de nodo es modulo de 2
            // Esto inicia desde 1 hasta 4

            if (nodo % 2 == 1) {
                suma = -suma;
            }
            // Envía el valor
            salida.writeDouble(suma);
            // Termina la conexión al enviar el su parte del trabajo.
            // No es necesario mantenerlo conectado siempre.
            entrada.close();
            salida.close();
            conexion.close();
        }
    }
}
