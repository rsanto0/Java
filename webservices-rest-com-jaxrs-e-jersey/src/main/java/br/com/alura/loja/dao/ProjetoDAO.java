package br.com.alura.loja.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import br.com.alura.loja.modelo.Projeto;

public class ProjetoDAO {

	private static Map<Long, Projeto> banco = new HashMap<Long, Projeto>();
	private static AtomicLong contador = new AtomicLong(1l);

	static {
		banco.put(1l, new Projeto(1l, "Minha loja", 2014));
		banco.put(2l, new Projeto(2l, "Alura", 2012));
	}

	public void adiciona(Projeto projeto) {
		long id = contador.incrementAndGet();
		projeto.setId(id);
		banco.put(id, projeto);
	}

	public Projeto busca(Long id) {
		return banco.get(id);
	}
	
	public List<Projeto> busca() {
		List<Projeto> projetos = new ArrayList<Projeto>();
		
		for (Entry<Long, Projeto> entry : banco.entrySet()) {
			Projeto projeto = (Projeto) entry.getValue();
			projetos.add(projeto);			
		}
		
		return projetos ;
	}
	
	public Projeto remove(long id) {
		return banco.remove(id);
	}
}
