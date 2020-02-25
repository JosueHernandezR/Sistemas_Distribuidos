import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.Thread;
import java.nio.ByteBuffer;

class Servidor {


    //Variables globales
    static int N = 4;
    static double[][] A = new double[N][N];
    static double[][] B = new double[N][N];
    static double[][] BT = new double[N][N];
    static double[][] C = new double[N][N];
    static int n = 0;
    static Object lock = new Object();
    //Igualar a 0 para evitar errores.
    static int checksum = 0;

    // lee del DataInputStream todos los bytes requeridos
    static class Worker extends Thread {

        Socket conexion;

        Worker(Socket conexion){
            this.conexion = conexion;
        }

        public void run() {
            try {
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

                //Se recibe el numero del nodo
                int nodo = entrada.readInt();
                //Dependiendo el numero del nodo, se envia la parte de la matriz
                
                if(nodo==1){
                    for(int i=0; i<N/2; i++){
                        for (int j = 0; j < N; j++) {
                          salida.writeDouble(A[i][j]);
                        }
                    }
                    for(int i=N/2; i<N; i++){
                        for (int j = 0; j < N; j++) {
                        salida.writeDouble(BT[i][j]);
                        }
                    }
                    //Recibe los datos del cliente y los ingresa en la matriz C
                    for(int i=0; i<N/2; i++){
                        for(int j=N/2; j<N; j++){
                            C[i][j]=entrada.readDouble();
                        }
                    }
                }

                if(nodo==2){
                    for(int i=N/2; i<N; i++){
                        for (int j = 0; j < N; j++) {
                            salida.writeDouble(A[i][j]);
                        }
                    }
                    for(int i=0; i<N/2; i++){
                        for (int j = 0; j < N; j++) {
                            salida.writeDouble(BT[i][j]);
                        }
                    }
                    //Recibe los datos del cliente y los ingresa en la matriz C
                    for(int i=N/2; i<N; i++){
                        for(int j=0; j<N/2; j++){
                            C[i][j]=entrada.readDouble();
                        }
                    }
                }
                
                if(nodo==3){
                for(int i=N/2; i<N; i++){
                    for (int j = 0; j < N; j++) {
                    salida.writeDouble(A[i][j]);
                    }
                }
                    for(int i=N/2; i<N; i++){
                        for (int j = 0; j < N; j++) {
                            salida.writeDouble(BT[i][j]);
                        }
                    }
                    //Recibe los datos del cliente y los ingresa en la matriz C
                    for(int i=N/2; i<N; i++){
                        for(int j=N/2; j<N; j++){
                            C[i][j]=entrada.readDouble();
                        }
                    }
                } 
                
                salida.close();
                entrada.close();
                conexion.close();
            }

                // ServerSocket servidor = new ServerSocket();
                // Socket conexion = servidor.accept();
                // // Mandar las matrices a los clientes
                // // inicializa las matrices A y B
                // // Inserte código aquí
                // for (int i = 0; i < N; i++)
                //     for (int j = 0; j < N; j++) {
                //         A[i][j] = 2 * i - j;
                //         B[i][j] = i + 2 * j;
                //     }
                // // transpone la matriz B, la matriz traspuesta es BT
                // for (int i = 0; i < N; i++)
                //     for (int j = 0; j < N; j++)
                //         BT[i][j] = B[j][i];

            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }


    // public static void main(String[] args) throws Exception {
    //     try {
    //         // Verifica que haya un argumento (el numero de nodo dnde ejecuta)
    //         if (args.length != 1) {
    //             System.err.println("Se necesita el número de nodo.");
    //             System.exit(-1);
    //         }


    //         //Si el servidor no esta ejecutando, reintenta la conexion
    //         Socket conexion = null;
    //         // Esperando a los 4 nodos 0 - 3
    //         for (;;) {
    //             try {
    //                 conexion = new Socket(nodo_0, 6000);
    //                 break;
    //             } catch (Exception e) {
    //                 Thread.sleep(100);
    //             }
    //         }

    //         DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
    //         DataInputStream entrada = new DataInputStream(conexion.getInputStream());

    //         //Envia al servidor el numero de nodo
    //         salida.write(nodo);

    //         //Recibe el servidor las partes de las matrices A y B
    //         // Notar partes siempre quedan en la parte superior de las matrices

    //         byte[] a = b

    //     } catch (Exception e) {
    //         // TODO: handle exception
    //     }
    //     ServerSocket servidor = new ServerSocket(5000);

    //     for (;;) {
    //         Socket conexion = servidor.accept();
    //         Worker w = new Worker(conexion);
    //         w.start();
    //     }

    //     if (nodo == 1) {

    //     }
    // }


    public static void main(String[] args) throws Exception{
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                A[i][j] = 2 * i + j;
                B[i][j] = i * 2 - j;
            }
    
        //Calculo de la matriz transpuesta de B
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                BT[i][j] = B[j][i];

        //Se crea el listener
        ServerSocket servidor = new ServerSocket(50000);
        
        //Se espera la conexion de los nodos
        for (int i=0; i<3; i++){
            Socket conexion = servidor.accept();
            Worker w = new Worker(conexion);
            w.start();
        }

        //Cacula el primer cuadrante 0,0
        for (int i = 0; i < N/2; i++) {
            for (int j = 0; j < N/2; j++) {
                double suma = 0;
                for (int k = 0; k < N; k++)
                    suma = suma + A[i][k] * BT[j][k];
                C[i][j] = suma;
            }
        }
        synchronized (lock) {
            n++;
        }
        for(;;){
            //Se espera a que se sincronize
            synchronized (lock) {
                n++;
                //Si la longitud de la matriz es menor o igual a 4 se imprimen las matrices
                if(n==4&&N<=4){
                    //Matriz A
                    System.out.println("Matriz A");
                    for (int i = 0; i < N; i++) {         
                        for (int j = 0; j < N; j++) {   
                            System.out.print(" | "+A[i][j] + " | ");
                        }
                        System.out.println(); 
                    }

                    System.out.println("Matriz B");
                    for (int i = 0; i < N; i++) {         
                        for (int j = 0; j < N; j++) {   
                            System.out.print(" | "+B[i][j] + " | ");
                        }
                        System.out.println(); 
                    }

                    System.out.println("Matriz C=AxB");
                    for (int i = 0; i < N; i++) {         
                        for (int j = 0; j < N; j++) {   
                            System.out.print(" | "+C[i][j] + " | ");
                        }
                    System.out.println(); 
                    }
                    //Se sale del ciclo infinito
                    break;
                    //Si no se calcula el checksum y se imprime
                }
                else if(n==4&&N==500){
                    for (int i = 0; i < N; i++) {
                        for (int j = 0; j < N; j++) {
                            checksum += C[i][j]; 
                        }
                    }
                    System.out.println(checksum);
                    break;
                }
            }
        }
    }
}
