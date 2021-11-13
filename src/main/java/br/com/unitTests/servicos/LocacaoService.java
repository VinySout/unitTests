package br.com.unitTests.servicos;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.unitTests.daos.LocacaoDAO;
import br.com.unitTests.entidades.Filme;
import br.com.unitTests.entidades.Locacao;
import br.com.unitTests.entidades.Usuario;
import br.com.unitTests.exceptions.FilmeSemEstoqueException;
import br.com.unitTests.exceptions.LocadoraException;
import br.com.unitTests.utils.DataUtils;
import buildermaster.BuilderMaster;

public class LocacaoService {
	
	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws LocadoraException, FilmeSemEstoqueException {

		if(filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}
		
		if(usuario == null) {
			throw new LocadoraException("Usuario vazio");
		}
		
		boolean negativado;
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas com SPC, tente novamente");
		}
		
		if(negativado) {
			throw new LocadoraException("Usuario negativado");
		}
		
		for (Filme filme : filmes) {
			if(filme.getEstoque() == 0) 
				throw new FilmeSemEstoqueException();
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilme(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(obterData());
		
		locacao.setValor(calcularValorLocacao(filmes));
		

		//Entrega no dia seguinte
		Date dataEntrega = obterData();
		dataEntrega = DataUtils.adicionarDias(dataEntrega, 1);
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = DataUtils.adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}

	protected Date obterData() {
		return new Date();
	}

	private Double calcularValorLocacao(List<Filme> filmes) throws FilmeSemEstoqueException {
		Double valorTotal = 0d;
		for (int i = 0; i < filmes.size(); i++) {
			Filme filme = filmes.get(i);
			Double valorFilme = filme.getPrecoLocacao();
			
			switch(i) {
				case 2: valorFilme = valorFilme * 0.75;
				break;
				case 3: valorFilme = valorFilme * 0.5;
				break;
				case 4: valorFilme = valorFilme * 0.25;
				break;
				case 5: valorFilme = 0d;
				break;
			}
			
			valorTotal += valorFilme;
			
		}
		return valorTotal;
	}
	
	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for(Locacao locacao : locacoes) {
			if(locacao.getDataRetorno().before(obterData())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}
	
	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilme(locacao.getFilme());
		novaLocacao.setDataLocacao(obterData());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);
		
		dao.salvar(novaLocacao);
	}

}