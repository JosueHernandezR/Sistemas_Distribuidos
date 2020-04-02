import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClaseRMI extends UnicastRemoteObject implements RemoteMatrixInterface{
    private static final long serialVersionUID = 1L;
    int tam = 4;

    public ClaseRMI() throws RemoteException{
        super();
    }

    public double[][] multiplicarMatriz(double[][]A, double[][]B) throws RemoteException{
        double[][] C = new double[tam/2][tam/2];
        for(int i = 0; i < tam/2; i++)
            for(int j = 0; j < tam/2; j++)
                for(int k = 0; k < tam; k++){
                    C[i][j] = A[i][k] * B[j][k];
                }
        return C;
    } 
    public void setTam(int tam){
        this.tam = tam;
    }
}

