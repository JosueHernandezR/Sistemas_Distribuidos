
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
}