/*
  MultDist.java
  Carlos Pineda G. 2020

  Multiplica dos matrices cuadradas en forma distribuida

  Usando Octave se puede verificar (N=4):

  A=[0,1,2,3;2,3,4,5;4,5,6,7;6,7,8,9]
  B=[0,-1,-2,-3;2,1,0,-1;4,3,2,1;6,5,4,3]
  C=A*B

C = 28    22    16    10
    52    38    24    10
    76    54    32    10
   100    70    40    10
*/

import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.Thread;
import java.nio.ByteBuffer;

class MultDist {
    final static int N = 4; // tama?o de las matrices

    static double[][] A;
    static double[][] B;
    static double[][] C;
    static int n = 0; // contador de los nodos terminados
    static Object lock = new Object(); // objeto empleado para sincronizar la actualizacion de n
    static String nodo_0 = "localhost"; // la IP o nombre de dominio del nodo 0

    // lee del DataInputStream todos los bytes requeridos

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    static class Worker extends Thread // los threads ejecutan en el servidor
    {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                // recibe del cliente el numero de nodo

                int nodo = entrada.readInt();

                ByteBuffer b = ByteBuffer.allocate(8 * N * N);
                ByteBuffer b2;
                byte[] a;

                switch (nodo) {
                    case 1:
                        // envia al cliente las partes de las matrices A y B

                        for (int i = 0; i < N / 2; i++)
                            for (int j = 0; j < N; j++)
                                b.putDouble(A[i][j]);

                        for (int i = N / 2; i < N; i++)
                            for (int j = 0; j < N; j++)
                                b.putDouble(B[i][j]);

                        salida.write(b.array());
                        salida.flush();

                        // recibe del cliente la parte de la matriz C

                        a = new byte[8 * N / 2 * N / 2];
                        read(entrada, a, 0, 8 * N / 2 * N / 2);
                        b2 = ByteBuffer.wrap(a);

                        for (int i = 0; i < N / 2; i++)
                            for (int j = N / 2; j < N; j++)
                                C[i][j] = b2.getDouble();

                        break;

                    case 2:
                        // envia al cliente las partes de las matrices A y B

                        for (int i = N / 2; i < N; i++)
                            for (int j = 0; j < N; j++)
                                b.putDouble(A[i][j]);

                        for (int i = 0; i < N / 2; i++)
                            for (int j = 0; j < N; j++)
                                b.putDouble(B[i][j]);

                        salida.write(b.array());
                        salida.flush();

                        // recibe del cliente la parte de la matriz C

                        a = new byte[8 * N / 2 * N / 2];
                        read(entrada, a, 0, 8 * N / 2 * N / 2);
                        b2 = ByteBuffer.wrap(a);

                        for (int i = N / 2; i < N; i++)
                            for (int j = 0; j < N / 2; j++)
                                C[i][j] = b2.getDouble();
                        break;

                    case 3:
                        // envia al cliente las partes de las matrices A y B

                        for (int i = N / 2; i < N; i++)
                            for (int j = 0; j < N; j++)
                                b.putDouble(A[i][j]);

                        for (int i = N / 2; i < N; i++)
                            for (int j = 0; j < N; j++)
                                b.putDouble(B[i][j]);

                        salida.write(b.array());
                        salida.flush();

                        // recibe del cliente la parte de la matriz C

                        a = new byte[8 * N / 2 * N / 2];
                        read(entrada, a, 0, 8 * N / 2 * N / 2);
                        b2 = ByteBuffer.wrap(a);

                        for (int i = N / 2; i < N; i++)
                            for (int j = N / 2; j < N; j++)
                                C[i][j] = b2.getDouble();

                        break;

                    default:
                        System.err.println("Nodo incorrecto");
                        System.exit(1);
                }

                // incrementa el contador de nodos que han terminado

                synchronized (lock) {
                    n++;
                }

                // cierra la conexion y los streams de entrada y salida

                salida.close();
                entrada.close();
                conexion.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    static void despliega_matriz(double[][] m) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)
                System.out.print(m[i][j] + ",");

            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        // verifica que haya un argumento (el numero de nodo donde ejecuta el programa
        // actual)

        if (args.length != 1) {
            System.err.println("Uso:\njava MultDist <nodo>");
            System.exit(0);
        }

        // obtiene el numero de nodo

        int nodo = Integer.valueOf(args[0]);

        if (nodo == 0) {
            // crea las matrices y las asigna a las variables globales

            A = new double[N][N];
            B = new double[N][N];
            C = new double[N][N];

            // inicializa las matrices

            for (int i = 0; i < N; i++)
                for (int j = 0; j < N; j++) {
                    A[i][j] = 2 * i + j;
                    B[i][j] = 2 * i - j;
                }

            despliega_matriz(A);
            despliega_matriz(B);

            // transpone la matriz B, la transpuesta queda en la misma matriz B

            for (int i = 0; i < N; i++)
                for (int j = 0; j < i; j++) {
                    double x = B[i][j];
                    B[i][j] = B[j][i];
                    B[j][i] = x;
                }

            // crea el socket servidor

            ServerSocket servidor = new ServerSocket(50000);

            // espera la conexion de 3 nodos

            for (int i = 1; i <= 3; i++) {
                Socket conexion = servidor.accept();
                Worker w = new Worker(conexion);
                w.start();
            }

            // calcula la parte de la matriz C que corresponde al nodo 0

            for (int i = 0; i < N / 2; i++)
                for (int j = 0; j < N / 2; j++)
                    for (int k = 0; k < N; k++)
                        C[i][j] += A[i][k] * B[j][k];

            // incrementa el contador de los nodos que han terminado

            synchronized (lock) {
                n++;
            }

            // espera que terminen los 4 nodos

            for (;;) {
                synchronized (lock) {
                    if (n == 4)
                        break;
                }

                Thread.sleep(100);
            }

            despliega_matriz(C);
            // calcula el checksum de la matriz C

            long checksum = 0;

            for (int i = 0; i < N; i++)
                for (int j = 0; j < N; j++)
                    checksum += C[i][j];

            System.out.println("checksum=" + checksum);
        } else {
            // crea las matrices y las asigna a las variables globales

            A = new double[N / 2][N];
            B = new double[N / 2][N];
            C = new double[N / 2][N / 2];

            // conecta con el servidor
            // si el servidor no esta ejecutando, reintenta la conexion
            Socket conexion = null;

            for (;;)
                try {
                    //
                    conexion = new Socket(nodo_0, 50000);
                    break;
                } catch (Exception e) {
                    Thread.sleep(500);
                }

            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());

            // envia al servidor el numero de nodo

            salida.writeInt(nodo);

            // recibe del servidor las partes de las matrices A y B
            // notar que las partes siempre quedan en la parte superior de las matrices

            byte[] a = new byte[8 * N * N];
            read(entrada, a, 0, 8 * N * N);
            ByteBuffer b = ByteBuffer.wrap(a);

            for (int i = 0; i < N / 2; i++)
                for (int j = 0; j < N; j++)
                    A[i][j] = b.getDouble();

            for (int i = 0; i < N / 2; i++)
                for (int j = 0; j < N; j++)
                    B[i][j] = b.getDouble();

            // calcula la parte de la matriz C que corresponde al nodo

            for (int i = 0; i < N / 2; i++)
                for (int j = 0; j < N / 2; j++)
                    for (int k = 0; k < N; k++)
                        C[i][j] += A[i][k] * B[j][k];

            // envia al servidor la parte de la matriz C

            ByteBuffer b2 = ByteBuffer.allocate(8 * N / 2 * N / 2);

            for (int i = 0; i < N / 2; i++)
                for (int j = 0; j < N / 2; j++)
                    b2.putDouble(C[i][j]);

            salida.write(b2.array());
            salida.flush();

            // cierra la conexion y los streams de entrada y salida

            salida.close();
            entrada.close();
            conexion.close();
        }
    }
}