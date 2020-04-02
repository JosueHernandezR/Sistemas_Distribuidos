import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteMatrixInterface extends Remote{
    public double[][] multiplicarMatriz(double[][]A, double[][]B) throws RemoteException;
    public void setTam(int tam) throws RemoteException;
}