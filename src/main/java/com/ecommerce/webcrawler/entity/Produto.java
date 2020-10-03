package com.ecommerce.webcrawler.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "produto", schema = "crawler")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nome")
	private String nome;
	
	@Column(name = "url")
	private String url;
	
	@Column(name = "categoria")
	private String categoria;
	
	@Column(name = "peso")
	private double peso;
	
	@Column(name = "cor")
	private String cor;
	
	@Column(name = "porcentagemDesconto")
	private double porcentagemDesconto;
	
	@Column(name = "classificacao")
	private int classificacao;
	
	@Column(name = "preco")
	private double preco;
}
