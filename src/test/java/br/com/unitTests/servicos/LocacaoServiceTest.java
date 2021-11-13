package br.com.unitTests.servicos;


import static br.com.unitTests.builders.FilmeBuilder.umFilme;
import static br.com.unitTests.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.com.unitTests.builders.LocacaoBuilder.umLocacao;
import static br.com.unitTests.builders.UsuarioBuilder.umUsuario;
import static br.com.unitTests.matchers.MatchersProprios.caiNumaSegunda;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.com.unitTests.builders.FilmeBuilder;
import br.com.unitTests.daos.LocacaoDAO;
import br.com.unitTests.entidades.Filme;
import br.com.unitTests.entidades.Locacao;
import br.com.unitTests.entidades.Usuario;
import br.com.unitTests.exceptions.FilmeSemEstoqueException;
import br.com.unitTests.exceptions.LocadoraException;
import br.com.unitTests.matchers.MatchersProprios;
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
//@RunWith(ParallelRunner.class)
public class LocacaoServiceTest {
	
	@InjectMocks
	@Spy
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
		System.out.println("Inicializando 2....");
		CalculadoraTest.ordem.append("2");
	}
	
	@After
	public void tearDown() {
		System.out.println("Finalizando 2...");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		//cenario
//		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
//		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(05, 11, 2021));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH, 5);
//		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
//		calendar.set(Calendar.YEAR, 2021);
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		Mockito.doReturn(DataUtils.obterData(05, 11, 2021)).when(service).obterData();
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
//		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(05, 11, 2021)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(06, 11, 2021)), is(true));
	
	}
	
	/**
	 * Forma Elegante
	 * 
	 * */
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
		
		//acao
		service.alugarFilme(usuario, filmes);
		
	}
	
	/**
	 * Forma Robusta
	 * @throws FilmeSemEstoqueException 
	 * 
	 * */
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		try {
			service.alugarFilme(null, filmes);
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		} 
	}
	
	/**
	 * Forma Nova
	 * @throws FilmeSemEstoqueException 
	 * @throws LocadoraException 
	 * 
	 * */
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		exception.expect(Exception.class);
		exception.expectMessage("Filme vazio");
		 
		//acao
		service.alugarFilme(new Usuario("Usuario 1"), null); 
	}
	
	@Test
	public void devePagar75PctNoFilme3() throws LocadoraException, FilmeSemEstoqueException {
		//cenario
		Usuario usuario = umUsuario().agora();
		
		List<Filme> filmes = Arrays.asList(umFilme().agora(), umFilme().agora(), umFilme().agora());
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(11.0));
		
	}
	
	@Test
	public void devePagar50PctNoFilme4() throws LocadoraException, FilmeSemEstoqueException {
		//cenario
		Usuario usuario = umUsuario().agora();
		
		List<Filme> filmes = Arrays.asList(umFilme().agora(), umFilme().agora(), umFilme().agora(), umFilme().agora());
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(13.0));
		
	}
	
	@Test
	public void devePagar25PctNoFilme5() throws LocadoraException, FilmeSemEstoqueException {
		//cenario
		Usuario usuario = umUsuario().agora();
		
		List<Filme> filmes = Arrays.asList(umFilme().agora(), umFilme().agora(), umFilme().agora(), umFilme().agora(), umFilme().agora());
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(14.0));
		
	}
	
	@Test
	public void devePagar0PctNoFilme6() throws LocadoraException, FilmeSemEstoqueException {
		//cenario
		Usuario usuario = umUsuario().agora();
		
		List<Filme> filmes = Arrays.asList(
				umFilme().agora(), 
				umFilme().agora(), 
				umFilme().agora(), 
				umFilme().agora(), 
				umFilme().agora(), 
				umFilme().agora());
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(14.0));
		
	}
	
	@Test
	public void deveEntregarNaSegundaAoAlugarNoSabado() throws Exception {
		//cenario
//		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
//		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(06, 11, 2021));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH, 6);
//		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
//		calendar.set(Calendar.YEAR, 2021);
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.doReturn(DataUtils.obterData(06, 11, 2021)).when(service).obterData();
		
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
	public void naoDeveAlugarFilmeParaNegativadSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		//acao
		try {
			service.alugarFilme(usuario, filmes);

		//verificacao
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario negativado"));
		}
		
		verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtratadas() {
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().usuarioComNome("Usuario 2").agora();
		Usuario usuario3 = umUsuario().usuarioComNome("Usuario 3").agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao().atrasado().comUsuario(usuario).agora(),
				umLocacao().comUsuario(usuario2).agora(),
				umLocacao().atrasado().comUsuario(usuario3).agora(),
				umLocacao().atrasado().comUsuario(usuario3).agora());
		
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
		
		//verificar
		verify(emailService, times(3)).notificarAtraso(Mockito.any(Usuario.class));
		verify(emailService).notificarAtraso(usuario);
		verify(emailService, atLeastOnce()).notificarAtraso(usuario3);
		verify(emailService, never()).notificarAtraso(usuario2);
		verifyNoMoreInteractions(emailService);
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
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = umLocacao().agora();
		
		//acao
		service.prorrogarLocacao(locacao, 3);
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		
		Locacao locacaoREtornada = argCapt.getValue();
		
		error.checkThat(locacaoREtornada.getValor(), is(12.0));
		error.checkThat(locacaoREtornada.getDataLocacao(), MatchersProprios.ehHoje());
		error.checkThat(locacaoREtornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(3));
	}
	
//	@Test
//	public void deveAlugarFilmeSemCalcularValor() throws Exception {
//		//cenario
//		Usuario usuario = umUsuario().agora();
//		List<Filme> filmes = Arrays.asList(umFilme().agora());
//		
//		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
//		
//		//acao
//		Locacao locacao = service.alugarFilme(usuario, filmes);
//		
//		//verificacao
//		Assert.assertThat(locacao.getValor(), is(1.0));
//		PowerMockito.verifyPrivate(service).invoke( "calcularValorLocacao", filmes);
//		
//	}
	
	@Test
	public void deveCalcularValorlocacao() throws Exception {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);
		
//		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		
		
		//verificacao
		Assert.assertThat(valor, is(4.0));
	}
}
