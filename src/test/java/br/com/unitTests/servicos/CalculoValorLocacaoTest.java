package br.com.unitTests.servicos;

import static br.com.unitTests.builders.FilmeBuilder.umFilme;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.unitTests.daos.LocacaoDAO;
import br.com.unitTests.entidades.Filme;
import br.com.unitTests.entidades.Locacao;
import br.com.unitTests.entidades.Usuario;
import br.com.unitTests.exceptions.FilmeSemEstoqueException;
import br.com.unitTests.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	@InjectMocks
	private LocacaoService service;
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value = 1)
	public Double valorLocacao;
	
	@Parameter(value = 2)
	public String desc;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	public static Filme filme1 = umFilme().agora();
	public static Filme filme2 = umFilme().agora();
	public static Filme filme3 = umFilme().agora();
	public static Filme filme4 = umFilme().agora();
	public static Filme filme5 = umFilme().agora();
	public static Filme filme6 = umFilme().agora();

	@Parameters(name = "Test: {index} = {2}")
	public static Collection<Object[]> getParametros() {
		return Arrays.asList(new Object[][] {
			{ Arrays.asList(filme1, filme2, filme3), 11.0, "3 filmes 25%" },
			{ Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 filmes 50%"},
			{ Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 filmes 75%" }, 
			{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 filmes 100%" }
		});
	}
	
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws LocadoraException, FilmeSemEstoqueException {
		Usuario usuario = new Usuario("Usuario 1");
		
		Locacao resultado = service.alugarFilme(usuario, filmes);
		assertThat(resultado.getValor(), is(valorLocacao));
		
	}
	
}
