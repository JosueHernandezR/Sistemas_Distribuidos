import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;

class Cliente {
    public static void main(String[] args) throws Exception {
        Socket conexion = new Socket("localhost", 50000);

        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        // envía un entero de 32 bits
        salida.writeInt(123);

        // envia un número punto flotante
        salida.writeDouble(1234567890.1234567890);

        // envia una cadena
        salida.write("hola".getBytes());

        // recibe una cadena
        byte[] buffer = new byte[4];
        entrada.read(buffer, 0, 4);
        System.out.println(new String(buffer, "UTF-8"));

        // envia 5 números punto flotante
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