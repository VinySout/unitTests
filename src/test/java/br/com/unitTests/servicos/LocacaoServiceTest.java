package br.com.unitTests.servicos;


import static br.com.unitTests.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.com.unitTests.builders.UsuarioBuilder.umUsuario;
import static br.com.unitTests.matchers.MatchersProprios.caiEm;
import static br.com.unitTests.matchers.MatchersProprios.caiNumaSegunda;
import static br.com.unitTests.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
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

import br.com.unitTests.builders.FilmeBuilder;
import br.com.unitTests.builders.UsuarioBuilder;
import br.com.unitTests.entidades.Filme;
import br.com.unitTests.entidades.Locacao;
import br.com.unitTests.entidades.Usuario;
import br.com.unitTests.exceptions.FilmeSemEstoqueException;
import br.com.unitTests.exceptions.LocadoraException;
import br.com.unitTests.matchers.DiaSemanaMatcher;
import br.com.unitTests.matchers.MatchersProprios;
import br.com.unitTests.utils.DataUtils;
import sun.util.resources.LocaleData;


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
	
	private LocacaoService service = null;
	private List<Filme> filmes = null;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		service = new LocacaoService();
		filmes = new ArrayList<Filme>();
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = umUsuario().agora();
		filmes.add(FilmeBuilder.umFilme().comValor(5.0).agora());
		
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
		
		Filme filme = umFilmeSemEstoque().agora();
		
		filmes.add(filme);
		
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
		filmes.add(FilmeBuilder.umFilme().agora());
		
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
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(11.0));
		
	}
	
	@Test
	public void devePagar50PctNoFilme4() throws LocadoraException, FilmeSemEstoqueException {
		//cenario
		Usuario usuario = umUsuario().agora();
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(13.0));
		
	}
	
	@Test
	public void devePagar25PctNoFilme5() throws LocadoraException, FilmeSemEstoqueException {
		//cenario
		Usuario usuario = umUsuario().agora();
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(14.0));
		
	}
	
	@Test
	public void devePagar0PctNoFilme6() throws LocadoraException, FilmeSemEstoqueException {
		//cenario
		Usuario usuario = umUsuario().agora();
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		filmes.add(FilmeBuilder.umFilme().agora());
		
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
		filmes.add(FilmeBuilder.umFilme().agora());
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
//		boolean ehSegunda = DataUtils.verificarDiaSemana(resultado.getDataRetorno(), Calendar.MONDAY);
//		Assert.assertTrue(ehSegunda);
		
//		assertThat(resultado.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//		assertThat(resultado.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(resultado.getDataRetorno(), caiNumaSegunda());
		
	}
}
