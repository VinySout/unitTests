package br.com.unitTests.daos;

import java.util.List;

import br.com.unitTests.entidades.Locacao;

public interface LocacaoDAO {
	public void salvar(Locacao locacao);

	public List<Locacao> obterLocacoesPendentes();
}
