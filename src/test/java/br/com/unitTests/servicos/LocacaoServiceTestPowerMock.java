package br.com.unitTests.servicos;


import static br.com.unitTests.builders.FilmeBuilder.umFilme;
import static br.com.unitTests.builders.UsuarioBuilder.umUsuario;
import static br.com.unitTests.matchers.MatchersProprios.caiNumaSegunda;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.com.unitTests.daos.LocacaoDAO;
import br.com.unitTests.entidades.Filme;
import br.com.unitTests.entidades.Locacao;
import br.com.unitTests.entidades.Usuario;
import br.com.unitTests.exceptions.LocadoraException;
import br.com.unitTests.utils.DataUtils;


/**
 * @author souto
 * 
 * Fast
 * Independent
 * Repeatable
 * Self-Verifying
 * Timely
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
public class LocacaoServiceTestPowerMock {
	
	@InjectMocks
	private LocacaoService service = null;
	
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService emailService;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = PowerMockito.spy(service);
		CalculadoraTest.ordem.append("4");
	}
	
	@After
	public void tearDown() {
		System.out.println("Finalizando 4...");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
//		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(05, 11, 2021));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH, 5);
//		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
//		calendar.set(Calendar.YEAR, 2021);
//		
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
//		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(05, 11, 2021)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(06, 11, 2021)), is(true));
	
	}
	
	
	@Test
	public void deveEntregarNaSegundaAoAlugarNoSabado() throws Exception {
//		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(06, 11, 2021));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH, 6);
//		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
//		calendar.set(Calendar.YEAR, 2021);
//		
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getDataRetorno(), caiNumaSegunda());
		
//		PowerMockito.verifyStatic(Mockito.times(2));
//		Calendar.getInstance();
		
//		boolean ehSegunda = DataUtils.verificarDiaSemana(resultado.getDataRetorno(), Calendar.MONDAY);
//		Assert.assertTrue(ehSegunda);
//		assertThat(resultado.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//		assertThat(resultado.getDataRetorno(), caiEm(Calendar.MONDAY));
//		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
	}
	
	@Test
	public void deveTratarErronoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		//acao
		service.alugarFilme(usuario, filmes);
		
		//verificacao
	}
	
	
	@Test
	public void deveAlugarFilmeSemCalcularValor() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		Assert.assertThat(locacao.getValor(), is(1.0));
		PowerMockito.verifyPrivate(service).invoke( "calcularValorLocacao", filmes);
		
	}
	
	@Test
	public void deveCalcularValorlocacao() throws Exception {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		//verificacao
		
		Assert.assertThat(valor, is(4.0));
	}
}
