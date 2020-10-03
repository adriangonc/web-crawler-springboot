package com.ecommerce.webcrawler.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.webcrawler.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{

}
