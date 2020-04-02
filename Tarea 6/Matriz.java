public class Matriz {
    private int tam;
    public double mat[][];
    private String name = "A";

    public Matriz(int tam, boolean isA) {
        this.tam = tam;
        this.mat = new double[tam][tam];
        for (int i = 0; i < tam; i++)
            for (int j = 0; j < tam; j++) {
                this.mat[i][j] = isA ? 2 * i + j : 2 * i - j;
            }
    }

    public void imprimirMatriz() {
        System.out.println("Matriz" + this.name + ":\n\n");

        for (int i = 0; i < tam; i++) {
            System.out.print("| ");
            for (int j = 0; j < tam; j++) {
                System.out.print(mat[i][j]);
                if (j != tam - 1) {
                    System.out.print("\t");
                }
            }
            System.out.println(" |");
        }
        System.out.println("-------------------------------------------------------------\n\n");
    }

    public long getChecksum() {
        long check = 0L;
        for (int i = 0; i < this.tam; i++)
            for (int j = 0; j < this.tam; j++)
                check += (long) this.mat[i][j];
        return check;
    }

    public Matriz(int tam) {
        this.tam = tam;
        this.mat = new double[tam][tam];
    }

    public void setName(String name) {
        this.name = name;
    }

    public void transpuesta() {
        for (int i = 0; i < tam; i++)
            for (int j = 0; j < i; j++) {
                double x = this.mat[i][j];
                this.mat[i][j] = this.mat[j][i];
                this.mat[j][i] = x;
            }
    }

    public double[][] copiaMatriz(int inicio) {
        double[][] M = new double[tam / 2][tam];
        for (int i = 0; i < tam / 2; i++)
            for (int j = 0; j < tam; j++) {
                M[i][j] = this.mat[i + inicio][j];
            }
        return M;
    }
}