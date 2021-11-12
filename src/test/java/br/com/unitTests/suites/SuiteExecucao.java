package br.com.unitTests.suites;

import org.junit.runners.Suite.SuiteClasses;

import br.com.unitTests.servicos.CalculadoraTest;
import br.com.unitTests.servicos.CalculoValorLocacaoTest;
import br.com.unitTests.servicos.LocacaoServiceTest;

//@RunWith(Suite.class)
@SuiteClasses({
	CalculadoraTest.class,
	CalculoValorLocacaoTest.class,
	LocacaoServiceTest.class
})
public class SuiteExecucao {

}
