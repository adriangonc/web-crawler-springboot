package com.ecommerce.webcrawler.service.imple;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ecommerce.webcrawler.entity.Produto;
import com.ecommerce.webcrawler.repository.ProdutoRepository;
import com.ecommerce.webcrawler.service.MercadoLivreCrawlerService;

public class MercadoLivreCrawlerImple implements MercadoLivreCrawlerService {

	private static final String SITE = "https://www.mercadolivre.com.br/ofertas#c_id=/home/promotions-recommendations/element&c_uid=73c1a44d-8f9e-40e6-83ef-18528426b814";
	private static Map<String, String> itens;
	private static ProdutoRepository repository;
	private static long id;
	private static List<Produto> listaProduto = new ArrayList<Produto>();

	public static void main(String[] args) {
		itens = new HashMap<>();
		rastrearPagina(SITE);
	}

	/* Busca todo o html da página */
	private static void rastrearPagina(String urlSite) {
		Document pagina;
		try {
			pagina = Jsoup.connect(urlSite).userAgent("Jsoup Crawler").get();
		} catch (IOException e) {
			System.out.println("Não foi possível rastrear o site");
			e.printStackTrace();
			return;
		}

		/* Busca todos os links disponíveis utilizando a classe CSS */
		String classProduto = "promotion-item__link-container";
		Elements elementProdutos = pagina.getElementsByClass(classProduto);

		/*
		 * Itera sobre os links encontrados e envia para o método responsável por buscar
		 * as informações detalhadas do produto
		 */
		for (Element e : elementProdutos) {
			System.out.println(e.attributes().get("href"));
			buscarInformacoesProduto(e.attributes().get("href"));
		}
		
		buscarProdutoComMaiorDesconto();
		buscarProdutoMaisBarato();
		buscarProdutoMaisVendido();
	}

	private static void buscarInformacoesProduto(String link) {
		Document paginaProduto = null;
		Produto produto = new Produto();
		ArrayList<Produto> produtos = new ArrayList<Produto>();
		/* Busca o html da página do produto */
		try {
			paginaProduto = Jsoup.connect(link).get();

		} catch (IOException e) {
			System.out.println("Não foi possível buscar informações do produto");
			e.printStackTrace();
		}
		produto = new Produto();
		/* Insere informações coletadas no objeto Produto */
		produto.setUrl(link);
		produto.setPreco(adicionarPrecoProduto(paginaProduto));
		produto.setNome(adicionarNomeProduto(paginaProduto));
		produto.setCategoria(adicionarCategoriaProduto(paginaProduto));
		produto.setCor(adicionarCorProduto(paginaProduto));
		produto.setPorcentagemDesconto(adicionarPorcentagemDescontoProduto(paginaProduto));
		produto.setClassificacao(adicionarClassificacaoProduto(paginaProduto));

		salvarProduto(produto);
	}

	private static Double adicionarPrecoProduto(Document paginaProduto) {
		Elements preco = paginaProduto
				.getElementsByClass("price-tag ui-pdp-price__part ui-pdp-price__original-value price-tag__disabled");

		try {
			for (Element e : preco) {
				System.out.println("Preço: " + e.text().substring(e.text().lastIndexOf("$") + 1));
				return Double.valueOf(e.text().substring(e.text().lastIndexOf("$") + 1).replace(".", "").replace(",", "."));
			}
			return (double) 0;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return (double) 0;
		}
	}

	private static String adicionarNomeProduto(Document paginaProduto) {
		Elements nome = paginaProduto.getElementsByClass("ui-pdp-title");

		try {
			for (Element e : nome) {
				System.out.println("Nome: " + e.text());
				return e.text();
			}
			return "";
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return "";
		}
	}

	private static String adicionarCategoriaProduto(Document paginaProduto) {
		Elements categoria = paginaProduto.getElementsByClass("andes-breadcrumb__link");

		try {
			for (Element e : categoria) {
				System.out.println("Categoria: " + e.text());
				return e.text();
			}
			return "";
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return "";
		}
	}

	private static String adicionarCorProduto(Document paginaProduto) {
		Elements cor = paginaProduto.getElementsByClass("ui-pdp-variations__selected-label ui-pdp-color--BLACK");

		try {
			for (Element e : cor) {
				System.out.println("Cor: " + e.text());
				return e.text();
			}
			return "";
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return "";
		}
	}

	private static double adicionarPorcentagemDescontoProduto(Document paginaProduto) {
		Elements desconto = paginaProduto.getElementsByClass("ui-pdp-price__second-line__label ui-pdp-color--GREEN");

		try {
			for (Element e : desconto) {
				System.out.println("% Desconto : " + e.text().substring(0, e.text().lastIndexOf("%")));
				return Double.valueOf(e.text().substring(0, e.text().lastIndexOf("%")));
			}
			return 0;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static int adicionarClassificacaoProduto(Document paginaProduto) {
		Elements vendas = paginaProduto.getElementsByClass("ui-pdp-seller__sales-description");

		try {
			for (Element e : vendas) {
				System.out.println("Classificação vendas: " + e.text());
				return Integer.parseInt(e.text());
			}
			return 0;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static Produto buscarProdutoComMaiorDesconto() {
		Produto produtoComMaiorDesconto = new Produto();
		produtoComMaiorDesconto = listaProduto.get(0);
		for (Produto produto : listaProduto) {
			if (produtoComMaiorDesconto.getPorcentagemDesconto() < produto.getPorcentagemDesconto()) {
				produtoComMaiorDesconto = produto;
			}
		}
		System.out.println("\n********************** PRODUTO COM MAIOR DESCONTO **********************\n");
		imprimirProduto(produtoComMaiorDesconto);
		return produtoComMaiorDesconto;
	}

	public static Produto buscarProdutoMaisBarato() {
		Produto produtoMaisBarato = new Produto();
		produtoMaisBarato = listaProduto.get(0);
		for (Produto produto : listaProduto) {
			if (produtoMaisBarato.getPreco() > produto.getPreco() && produto.getPreco() != 0) {
				produtoMaisBarato = produto;
			}
		}
		System.out.println("\n********************** PRODUTO MAIS BARATO **********************\n");
		imprimirProduto(produtoMaisBarato);
		return produtoMaisBarato;
	}

	private static void imprimirProduto(Produto produtoMaisBarato) {
		System.out.println("Nome: " + produtoMaisBarato.getNome());
		System.out.println("Link: " + produtoMaisBarato.getUrl());
		System.out.println("Desconto: " + produtoMaisBarato.getPorcentagemDesconto());
		System.out.println("Preço: " + produtoMaisBarato.getPreco());
		System.out.println("Quantidade de vendas: " + produtoMaisBarato.getClassificacao());
	}

	public static Produto buscarProdutoMaisVendido() {
		Produto produtoMaisPopular = new Produto();
		produtoMaisPopular = listaProduto.get(0);
		for (Produto produto : listaProduto) {
			if (produtoMaisPopular.getClassificacao() < produto.getClassificacao()) {
				produtoMaisPopular = produto;
			}
		}
		System.out.println("\n********************** PRODUTO MAIS POPULAR **********************\n");
		imprimirProduto(produtoMaisPopular);
		return produtoMaisPopular;
	}

	public static void salvarProduto(Produto produto) {
		/* Salva o produto no banco - Simulando com lista */
		id += 1;
		produto.setId(id);
		try {
			listaProduto.add(produto);
			// repository.save(produto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
