import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.Thread;
import java.nio.ByteBuffer;

class Servidor {

    static int N = 4;
    static int[][] A = new int[N][N];
    static int[][] B = new int[N][N];
    static int[][] BT = new int[N][N];
    static int[][] C = new int[N][N];
    static int nodo;
    static Object lock;
    static int checksum;
    
    
    
    
    
    
    // lee del DataInputStream todos los bytes requeridos
    static class Worker extends Thread {
        public Worker(Socket conexion) {

        }

        @Override
        public void run() {
            try {
                ServerSocket servidor = new ServerSocket();
                Socket conexion = servidor.accept();
                // Mandar las matrices a los clientes
                // inicializa las matrices A y B
                //Inserte código aquí
                for (int i = 0; i < N; i++)
                    for (int j = 0; j < N; j++) {
                        A[i][j] = 2 * i - j;
                        B[i][j] = i + 2 * j;
                    }
                // transpone la matriz B, la matriz traspuesta es BT
                for (int i = 0; i < N; i++)
                    for (int j = 0; j < N; j++)
                        BT[i][j] = B[j][i];

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    static void despluega_matriz(double[][] m){
        for(int i = 0; i < N; i++){
            for( int j=0; j < N; j++){
                System.out.println(m[i][j] + ",");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) throws Exception{
        try {
            // Verifica que haya un argumento (el numero de nodo dnde ejecuta)
            if (args.length != 1) {
                System.err.println("Se necesita el número de nodo.");
                System.exit(-1);
            } 
            else {
                nodo = Integer.valueOf(args[0]);
                Worker w = new Worker();
                w.start();
                Socket conexion = null;
                //Esperando a los 4 nodos 0 - 3
                for (;;) {
                    try {
                        conexion = new Socket("localhost",6000);
                        break;
                    } catch (Exception e) {
                        Thread.sleep(100);
                    }
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        ServerSocket servidor = new ServerSocket(5000);

        for (;;) {
            Socket conexion = servidor.accept();
            Worker w = new Worker(conexion);
            w.start();
        }

        if(nodo == 1){
            
        }
    }
}