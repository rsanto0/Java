package br.com.alura.loja.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.dao.CarrinhoDAO;
import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

@Path("carrinhos")
public class CarrinhoResource {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	/*
	 * curl -v http://localhost:8080/carrinhos
	 */	
	public String busca() {
		List<Carrinho> carrinhosLista = new CarrinhoDAO().busca();
		String carrinhos = new XStream().toXML(carrinhosLista);
		return carrinhos != null ? carrinhos : "";
	}

	/* curl -v http://localhost:8080/carrinhos/1 */	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public String busca(@PathParam("id") long id) {
		Carrinho carrinho = new CarrinhoDAO().busca(id);
		return carrinho.toXML();
	}
	
	/* curl -v http://localhost:8080/carrinhos/1 */	
	@GET
	@Path("/jaxb/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Carrinho buscaCarrinhoJAXB(@PathParam("id") long id) {
		Carrinho carrinho = new CarrinhoDAO().busca(id);
		return carrinho;
	}

	/* curl -v -H "Content-Type: application/xml" -d "<br.com.alura.loja.modelo.Carrinho><produtos><br.com.alura.loja.modelo.Produto><preco>4000.0</preco><id>6237</id><nome>PS5</nome><quantidade>1</quantidade></br.com.alura.loja.modelo.Produto></produtos><rua>Rua Vergueiro 3185, 8 andar</rua><cidade>São Paulo</cidade><id>1</id></br.com.alura.loja.modelo.Carrinho>" http://localhost:8080/carrinhos */	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response adicionaCarrinho(String carrinho) {
		Carrinho novoCarrinho = (Carrinho) new XStream().fromXML(carrinho);
		new CarrinhoDAO().adiciona(novoCarrinho);
		URI uri = URI.create("/carrinhos/" + novoCarrinho.getId());
		return Response.created(uri).build();
	}
	
	@POST
	@Path("/jaxb")
	@Consumes(MediaType.APPLICATION_XML)
	public Response adicionaCarrinhoJAXB(Carrinho carrinho) {
		new CarrinhoDAO().adiciona(carrinho);
		URI uri = URI.create("/carrinhos/jaxb/" + carrinho.getId());
		return Response.created(uri).build();
	}

	/* curl -v -X DELETE http://localhost:8080/carrinhos/1/produtos/6237 */	
	@DELETE
	@Path("/{id}/produtos/{produtoId}")
	public Response remove(@PathParam("id") long idCarrinho, @PathParam("produtoId") long produtoId) {
		Carrinho carrinho = new CarrinhoDAO().busca(idCarrinho);
		carrinho.remove(produtoId);
		return Response.ok().build();
	}
	
	/* curl -v -X PUT -H "Content-Type: application/xml" -d "<br.com.alura.loja.modelo.Produto> <id>3467</id> <quantidade>1</quantidade>    </br.com.alura.loja.modelo.Produto>" http://localhost:8080/carrinhos/1/produtos/3467/quantidade */	
	@PUT
	@Path("/{id}/produtos/{produtoId}/quantidade")
	@Consumes(MediaType.APPLICATION_XML)
	public Response alterarProduto(@PathParam("id") long idCarrinho, @PathParam("produtoId") long produtoId, String conteudo) {
		Carrinho carrinho = new CarrinhoDAO().busca(idCarrinho);
	    List<Produto> produtos = carrinho.getProdutos();
	    
	    for (Produto produto : produtos) {
			if (produto.getId() == produtoId) {
				Produto produtoRequest = (Produto) new XStream().fromXML(conteudo);
				produto.setQuantidade(produtoRequest.getQuantidade());
				break;
			}
		}
	    return Response.ok().build();
	}

}
