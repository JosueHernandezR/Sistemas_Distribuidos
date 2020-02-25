import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.Thread;
import java.nio.ByteBuffer;

class Servidor2{
  /* VARIABLES GLOBALES */
  //N tamaño de matrices
  static int N = 4;
  static double[][] A = new double[N][N];
  static double[][] B = new double[N][N];
  static double[][] BT = new double[N][N];
  static double[][] C = new double[N][N];
  static Object lock = new Object();
  static double checksum=0;
  //Contador de nodos
  static int n = 0;
  static class Worker extends Thread
  {
    Socket conexion;

    Worker(Socket conexion)
    {
      this.conexion = conexion;
    }
    public void run()
    {
      try
      {
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        //Recibe el numero del nodo del cliente
        //Dependiendo del cliente se envia una parte de la matriz
        int nodo = entrada.readInt();
        if(nodo==1){
          for(int i=0; i<N/2; i++){
            for (int j = 0; j < N; j++) {
              salida.writeDouble(A[i][j]);
            }}
            for(int i=N/2; i<N; i++){
            for (int j = 0; j < N; j++) {
              salida.writeDouble(BT[i][j]);
            }}
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
            }}
            for(int i=0; i<N/2; i++){
            for (int j = 0; j < N; j++) {
              salida.writeDouble(BT[i][j]);
            }}
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
            }}
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
      catch (Exception e)
      {
        System.err.println(e.getMessage());
      }
    }
  }

  public static void main(String[] args) throws Exception
  {
    //Se inicializan las variables
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
    //Calculamos el primer cuadrante de la matriz C
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
        }else if(n==4&&N==500){
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