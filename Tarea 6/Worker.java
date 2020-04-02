import java.rmi.Naming;

public class Worker extends Thread {

    RemoteMatrixInterface rmi;
    Matriz A,B,C;
    int row;
    int col;
    int nodo;
    int tam;
    String puerto;

    public Worker(int row, int col, int nodo, int tam, Matriz A, Matriz B, Matriz C, String puerto){
        this.row = row;
        this.col = col;
        this.nodo = nodo;
        this.A = A;
        this.B = B;
        this.C = C;
        this.tam = tam;
        this.puerto = puerto;
    }

    public double[][] multiplicarMatriz(double[][]A, double[][]B){
        double[][] C = new double[this.tam/2][this.tam/2];
        for(int i = 0; i < tam; i++)
            for(int j = 0; j < tam/2; j++)
                for(int k = 0; k < tam; k++){
                    C[i][j] += A[i][k] * B[j][k];
                }
        return C;
    }

    public void acomoda(double[][]C, double[][]R, int row, int col, int tam){
        for(int i = 0; i < tam/2; i++)
            for(int j = 0; j < tam/2; j++){
                C[i+row][j+col] = R[i][j];
            }
    }
    public void run(){
        try {
            double[][] Cn;
            if(nodo != 0){
                rmi = (RemoteMatrixInterface)Naming.lookup("rmi://localhost:"+puerto+"/nodo"+Integer.valueOf(nodo));
                Cn = multiplicarMatriz(A.copiaMatriz(row), B.copiaMatriz(col));
            } else{
                Cn = new double[tam/2][tam/2];
                Cn = multiplicarMatriz(A.copiaMatriz(row), B.copiaMatriz(col));
            }
            acomoda(C.mat, Cn, row, col, tam);
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }
}