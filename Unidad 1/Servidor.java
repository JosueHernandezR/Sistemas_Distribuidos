import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;

class Servidor {
    public static void main(String[] args) throws Exception {
        System.out.println(args.length);
        System.out.println(args[0]);

        ServerSocket servidor = new ServerSocket(50000);

        Socket conexion = servidor.accept();

        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        // recibe un entero de 32 bits
        int n = entrada.readInt();
        System.out.println(n);

        // recibe un número punto flotante
        double x = entrada.readDouble();
        System.out.println(x);

        // recibe una cadena
        byte[] buffer = new byte[4];
        entrada.read(buffer, 0, 4);
        System.out.println(new String(buffer, "UTF-8"));

        // envía una cadena
        salida.write("HOLA".getBytes());
        salida.flush();

        // recibe 5 números punto flotante
        byte[] a = new byte[5 * 8];
        entrada.read(a, 0, 5 * 8);
        ByteBuffer b = ByteBuffer.wrap(a);
        for (int i = 0; i < 5; i++)
            System.out.println(b.getDouble());

        salida.close();
        entrada.close();
        conexion.close();
    }
}