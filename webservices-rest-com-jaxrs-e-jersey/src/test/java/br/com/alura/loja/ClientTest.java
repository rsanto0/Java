package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

public class ClientTest {

	private HttpServer server;
	private Client client;
	private WebTarget target;

	@Before
    public void startaServidor() {
        this.server = Servidor.inicializaServidor();
        
		/*
		 * Para poder ver o que meu cliente JAX-RS está enviando para o
		 * servidor e o que é que o servidor está devolvendo para o meu cliente
		 * configurando o meu cliente.
		 */
        ClientConfig config = new ClientConfig();
        config.register(new LoggingFilter());        
        this.client = ClientBuilder.newClient(config);
        
        this.target = client.target("http://localhost:8080");
        
    }

    @After
    public void mataServidor() {
    	this.server.stop();
    	System.out.println("Servidor parado");
    }

   
    @Test
    public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
       
        String conteudo = target.path("/carrinhos/1").request().get(String.class);
//        Carrinho carrinho2 = new Gson().fromJson(conteudo, Carrinho.class);
        Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
        Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
    }
    
    @Test
    public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperadoJAXB() {
       
        Carrinho carrinho = target.path("/carrinhos/jaxb/1").request().get(Carrinho.class);

        Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
    }
    
    @Test
    public void testeAdicionaCarrinho() {
    	
    	Carrinho carrinhoTeste = new Carrinho();
    	carrinhoTeste.adiciona(new Produto(321l, "Tablet", 5000, 1));
    	carrinhoTeste.setCidade("Belo Horizonte");
    	carrinhoTeste.setRua("Avenida Brasil 1234");
	
    	String xmlCarrinho = new XStream().toXML(carrinhoTeste);
		/*
		 * Agora que temos o XML e sabemos que o media type que enviaremos é
		 * application/xml, precisamos representar isso de alguma maneira. Utilizaremos
		 * a classe Entity do próprio JAX-RS, para criar tal representação - o conteúdo
		 * e o media type -
		 */
    	Entity<String> entity = Entity.entity(xmlCarrinho, MediaType.APPLICATION_XML);
    	
    	Response response = target.path("/carrinhos").request().post(entity);
    	
    	Assert.assertEquals(201, response.getStatus());
    	
    	String location = response.getHeaderString("location");
    	String conteudo = client.target(location).request().get(String.class);

    	Assert.assertTrue(conteudo.contains("Tablet"));
    }
    
    @Test
    public void testeAdicionaCarrinhoJAXB() {
    	
    	Carrinho carrinhoTeste = new Carrinho();
    	carrinhoTeste.adiciona(new Produto(321l, "Tablet", 5000, 1));
    	carrinhoTeste.setCidade("Belo Horizonte");
    	carrinhoTeste.setRua("Avenida Brasil 1234");
	
		/*
		 * Agora que temos o XML e sabemos que o media type que enviaremos é
		 * application/xml, precisamos representar isso de alguma maneira. Utilizaremos
		 * a classe Entity do próprio JAX-RS, para criar tal representação - o conteúdo
		 * e o media type -
		 */
    	Entity<Carrinho> entity = Entity.entity(carrinhoTeste, MediaType.APPLICATION_XML);
    	
    	Response response = target.path("/carrinhos/jaxb").request().post(entity);
    	
    	Assert.assertEquals(201, response.getStatus());
    	
    	String location = response.getHeaderString("location");
    	Carrinho conteudo = client.target(location).request().get(Carrinho.class);

    	Assert.assertEquals("Tablet", conteudo.getProdutos().get(0).getNome());
    }
    
}
