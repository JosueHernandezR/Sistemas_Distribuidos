import java.net.ServerSocket;

public class Matriz{
    // Se declara las matrices
    static double[][] A;
    static double[][] B;
    static double[][] C;
    static int n = 0;

    static Object lock = new Object();
    public static void main(String []args){
        // Servidor Socket.servidor = new.ServerSocket(50000);
        // for(;;){
        //     Socket conexxion = servidor.accept();
            
        // }
        //checar que args.lenght=1
        if (args.length==0) {
            System.out.println("");
        }
        else{
            System.err.println("");
        }



        //7.8
            for(;;){
                synchronized(lock){
                    if(n==4)
                        break;
                }
                //Se usa para que otros hilos ocupen el CPU
                Thread.sleep(100);
            }
            //Calcular el checksum de la matriz C

        //8.0


        //Llenado de matrices
        for(int i = 0; i<3; i++){
            for(int j = 0; j<3; j++){
                A[i][j] = (int) (Math.random()*9+1);
            }
        }

        for(int i = 0; i<3; i++){
            for(int j = 0; j<3; j++){
                B[i][j] = (int) (Math.random()*9+1);
            }
        }

        //Matriz Resultante
        for(int i = 0; i<3; i++)
            for(int j = 0; j<3; j++)
                for(int k = 0; k < 3; k++)
                    C[i][j] += A[i][k]*B[k][j];
    }
    


}


//Programar dividiendo los datos(Haciendo que varias máquinas tengan varias partes).