
public class Cliente {
    static Matriz A;
    static Matriz B;
    static Matriz C;
    static int tam;
    static long checksum;

    public static void main(String[] args) throws Exception{

        int row = 0;
        int col = 0;
        int tam = Integer.parseInt(args[0]);
        String puerto = args[1];

        A = new Matriz(tam, true);
        B = new Matriz(tam, false);
        C = new Matriz(tam);

        A.setName("A");
        B.setName("B");
        C.setName("C");

        if(tam <= 30){
            A.imprimirMatriz();
            B.imprimirMatriz();
        }

        B.transpuesta();
        
        Worker[] w = new Worker[4];
        for(int nodo = 0; nodo < 4; nodo++){
            switch(nodo){
                case 0:
                    row = 0;
                    col = 0;
                    break;
                case 1:
                    row = 0;
                    col = tam/2;
                    break;
                case 2:
                    row = tam/2;
                    col = 0;
                    break;
                case 3:
                    row = tam/2;
                    col = tam/2;
                    break;
            }
            w[nodo] = new Worker(row, col, nodo, tam, A, B, C, puerto);
            w[nodo].start();
        }

        for(int j = 0; j < w.length; j++)
            w[j].join();
        
        if(tam <= 30)
            C.imprimirMatriz();

            System.out.println("Checksum: "+ String.valueOf(C.getChecksum()));
    }

}