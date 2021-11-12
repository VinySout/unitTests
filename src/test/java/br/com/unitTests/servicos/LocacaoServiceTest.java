package br.com.unitTests.servicos;


import static br.com.unitTests.builders.FilmeBuilder.umFilme;
import static br.com.unitTests.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.com.unitTests.builders.LocacaoBuilder.umLocacao;
import static br.com.unitTests.builders.UsuarioBuilder.umUsuario;
import static br.com.unitTests.matchers.MatchersProprios.caiNumaSegunda;
import static br.com.unitTests.utils.DataUtils.isMesmaData;
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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
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
public class LocacaoServiceTest {
	
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
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
	
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
	public void deveEntregarNaSegundaAoAlugarNoSabado() throws LocadoraException, FilmeSemEstoqueException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
//		boolean ehSegunda = DataUtils.verificarDiaSemana(resultado.getDataRetorno(), Calendar.MONDAY);
//		Assert.assertTrue(ehSegunda);
		
//		assertThat(resultado.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//		assertThat(resultado.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(resultado.getDataRetorno(), caiNumaSegunda());
		
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
			Assert.assertThat(e.getMessage(), is("Usuário negativado"));
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
}
