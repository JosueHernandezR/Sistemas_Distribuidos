import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;

class Cliente2
{
  //Declaracion de variables globales
  static int N = 4;
  static double[][] A = new double[N/2][N];
  static double[][] B = new double[N/2][N];
  static double[][] C = new double[N/2][N/2];
  public static void main(String[] args) throws Exception
  {
    if (args.length != 1) {
      System.err.println("Se espera el numero de nodos");
      System.exit(-1);
    }
    //Recibe como argumento el numero de nodo que es
    int nodo = Integer.valueOf(args[0]);
    Socket conexion = new Socket("localhost",50000);
    DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
    DataInputStream entrada = new DataInputStream(conexion.getInputStream());
    //Manda al servidor cual es el nodo
    salida.writeInt(nodo);
    //Se reciben los datos
    System.out.println("Esperando datos");
    for(int i=0; i<N/2; i++){
      for (int j = 0; j < N; j++) {
        A[i][j]=entrada.readDouble();}}
    for(int i=0; i<N/2; i++){
      for (int j = 0; j < N; j++){
        B[i][j]=entrada.readDouble();}}
    System.out.println("Recibi datos");
    //Se multiplica la matriz
    for (int i = 0; i < N/2; i++) {
      for (int j = 0; j < N/2; j++) {
          double suma = 0;
          for (int k = 0; k < N; k++)
              suma = suma + A[i][k] * B[j][k];
          C[i][j] = suma;
      }
    }
    //Se envian los datos al servidor
    for(int i=0; i<N/2; i++){
      for (int j = 0; j < N/2; j++){
        salida.writeDouble(C[i][j]);
      }
    }
    System.out.println("Regrese datos al servidor");
    salida.close();
    entrada.close();
    conexion.close();    
  }
}