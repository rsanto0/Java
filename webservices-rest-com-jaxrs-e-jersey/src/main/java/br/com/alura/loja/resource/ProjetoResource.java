package br.com.alura.loja.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.dao.ProjetoDAO;
import br.com.alura.loja.modelo.Projeto;

@Path("/projetos")
public class ProjetoResource {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String busca(@PathParam("id") long id) {
		Projeto projeto = new ProjetoDAO().busca(id);
		return projeto.toJson();
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public String busca() {
		List<Projeto> busca = new ProjetoDAO().busca();
		String xml = new XStream().toXML(busca);
		return xml;
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response adiciona(String projeto) {
		Projeto novoProjeto = (Projeto) new XStream().fromXML(projeto);
		new ProjetoDAO().adiciona(novoProjeto);

		URI uri = URI.create("/projeto/" + novoProjeto.getId());
		return Response.created(uri).build();
	}

	@DELETE
	@Path("/{id}")
	public Response remove(@PathParam("id") long idProjeto) {
		new ProjetoDAO().remove(idProjeto);
		return Response.ok().build();
	}

}
