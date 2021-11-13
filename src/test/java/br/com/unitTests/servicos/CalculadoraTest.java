package br.com.unitTests.servicos;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import br.com.unitTests.exceptions.NaoPodeDividirPorZeroException;


/**
 * RED, GREEN, REFACTOR
 * TDD
 * 
 * */
//@RunWith(ParallelRunner.class)
@RunWith(BlockJUnit4ClassRunner.class)
public class CalculadoraTest {
	
	public static StringBuffer ordem = new StringBuffer();
	
	private Calculadora calc = null;
	
	@Before
	public void setup() {
		calc = new Calculadora();
		System.out.println("Iniciando");
		ordem.append("1");
	}
	
	@After
	public void tearDown() {
		System.out.println("Finalizando...");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println(ordem.toString());
	}
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Test
	public void deveSomarDoisValores() {
		//cenario
		int a = 5;
		int b = 2;
		
		//acao
		int resultado = calc.somar(a, b);
		
		//verificacao
		Assert.assertEquals(7, resultado);
		//error.checkThat(resultado, is(equalTo(4)));
	}
	
	@Test
	public void deveSubtrairDoisValores() {
		//cenario
		int a = 5;
		int b = 2;
		
		//acao
		int resultado = calc.subtrair(a, b);
		
		//verificacao
		Assert.assertEquals(3, resultado);
		//error.checkThat(resultado, is(equalTo(4)));
	}
	
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
		//cenario
		double a = 5;
		double b = 2;
		
		//acao
		double resultado = calc.dividir(a, b);
		
		//verificacao
		Assert.assertEquals(2.5, resultado, 0.01);
		//error.checkThat(resultado, is(equalTo(4)));
	}
	
	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		//cenario
		double a = 10;
		double b = 0;
		
		//acao
		calc.dividir(a, b);
	}
	
	@Test
	public void deveDividir() {
		String a = "6";
		String b = "3";
		
		int resultado = calc.divide(a, b);
		
		Assert.assertEquals(2, resultado);
	}
}
