import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;

class Cliente {

    static int N = 1000;
    static int[][] A = new int[N/2][N];
    static int[][] B = new int[N/2][N];
    static int[][] BT = new int[N][N/2];
    static int[][] C = new int[N/2][N/2];
    // lee del DataInputStream todos los bytes requeridos

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    public static void main(String[] args) throws Exception {
        Socket conexion = conexion = new Socket("localhost", 5000);

        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        // env�a un entero de 32 bits
        salida.writeInt(123);

        // envia un n�mero punto flotante
        salida.writeDouble(1234567890.1234567890);

        // envia una cadena
        salida.write("hola".getBytes());

        // recibe una cadena
        byte[] buffer = new byte[4];
        read(entrada, buffer, 0, 4);
        System.out.println(new String(buffer, "UTF-8"));

        // envia 5 n�meros punto flotante
        ByteBuffer b = ByteBuffer.allocate(5 * 8);
        b.putDouble(1.1);
        b.putDouble(1.2);
        b.putDouble(1.3);
        b.putDouble(1.4);
        b.putDouble(1.5);
        byte[] a = b.array();
        salida.write(a);
        salida.flush();

        salida.close();
        entrada.close();
        conexion.close();
    }
}