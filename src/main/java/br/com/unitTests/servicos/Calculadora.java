package br.com.unitTests.servicos;

import br.com.unitTests.exceptions.NaoPodeDividirPorZeroException;

public class Calculadora {

	public int somar(int a, int b) {
		System.out.println("Estou executando o método Somar");
		return a + b;
	}

	public int subtrair(int a, int b) {
		return a - b;
	}

	public double dividir(double a, double b) throws NaoPodeDividirPorZeroException {
		if(b == 0)
			throw new NaoPodeDividirPorZeroException();
		return a / b;
	}
	
	public int divide(String a, String b) {
		return Integer.valueOf(a) / Integer.valueOf(b);
	}
	
	public void imprime () {
		System.out.println("Passei aqui");
	}
	
	public static void main(String[] args) {
		new Calculadora().divide("5", "2");
	}

}
