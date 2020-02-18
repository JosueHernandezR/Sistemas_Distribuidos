class MultiplicaMatriz{

	static int N = 1000;
	static int[][] A = new int[N][N];
	static int[][] B = new int[N][N];
	static int[][] C = new int[N][N];
	public static void main(String[] args){
	long t1 = System.currentTimeMillis();
	// inicializa las matrices A y B
	for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++){
		A[i][j] = 2 * i - j;
		B[i][j] = i + 2 * j;
		}
// multiplica la matriz A y la matriz B, el resultado queda en la matriz C
	for (int i = 0; i < N; i++){
		for (int j = 0; j < N; j++){
			int suma = 0;
			for (int k = 0; k < N; k++)
				suma = suma + A[i][k] * B[k][j];
			C[i][j] = suma;
		}
	}

	long t2 = System.currentTimeMillis();
	System.out.println(t2 - t1);

	}
}