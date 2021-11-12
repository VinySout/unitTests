package br.com.unitTests.builders;

import java.util.Arrays;
import br.com.unitTests.entidades.Usuario;
import br.com.unitTests.utils.DataUtils;

import static br.com.unitTests.builders.FilmeBuilder.umFilme;
import static br.com.unitTests.builders.UsuarioBuilder.umUsuario;
import static br.com.unitTests.utils.DataUtils.obterDataComDiferencaDias;

import java.lang.Double;
import java.util.Date;

import br.com.unitTests.entidades.Filme;
import br.com.unitTests.entidades.Locacao;


public class LocacaoBuilder {
	private Locacao elemento;
	private LocacaoBuilder(){}

	public static LocacaoBuilder umLocacao() {
		LocacaoBuilder builder = new LocacaoBuilder();
		inicializarDadosPadroes(builder);
		return builder;
	}

	public static void inicializarDadosPadroes(LocacaoBuilder builder) {
		builder.elemento = new Locacao();
		Locacao elemento = builder.elemento;

		
		elemento.setUsuario(umUsuario().agora());
		elemento.setFilme(Arrays.asList(umFilme().agora()));
		elemento.setDataLocacao(new Date());
		elemento.setDataRetorno(DataUtils.obterDataComDiferencaDias(1));
		elemento.setValor(4.0);
	}

	public LocacaoBuilder comUsuario(Usuario param) {
		elemento.setUsuario(param);
		return this;
	}

	public LocacaoBuilder comListaFilme(Filme... params) {
		elemento.setFilme(Arrays.asList(params));
		return this;
	}

	public LocacaoBuilder comDataLocacao(Date param) {
		elemento.setDataLocacao(param);
		return this;
	}

	public LocacaoBuilder comDataRetorno(Date param) {
		elemento.setDataRetorno(param);
		return this;
	}

	public LocacaoBuilder comValor(Double param) {
		elemento.setValor(param);
		return this;
	}

	public Locacao agora() {
		return elemento;
	}
	
	public LocacaoBuilder atrasado() {
		elemento.setDataLocacao(obterDataComDiferencaDias(-4));
		elemento.setDataRetorno(obterDataComDiferencaDias(-2));
		return this;
	}
}
