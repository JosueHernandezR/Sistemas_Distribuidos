import java.rmi.Naming;

class Servidor {
    public static void main(final String[] args) {

        String puerto = args[0];
        try {
            final RemoteMatrixInterface clase = new ClaseRMI();
            Naming.rebind("rmi://localhost:" + puerto + "/nodo1", clase);
            Naming.rebind("rmi://localhost:" + puerto + "/nodo2", clase);
            Naming.rebind("rmi://localhost:" + puerto + "/nodo3", clase);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}