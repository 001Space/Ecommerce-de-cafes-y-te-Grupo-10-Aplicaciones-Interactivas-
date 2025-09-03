package com.tpo.cafe.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpo.cafe.dominios.Categoria;
import com.tpo.cafe.dominios.ImagenProducto;
import com.tpo.cafe.dominios.Producto;
import com.tpo.cafe.dto.DtosProducto.CreateReq;
import com.tpo.cafe.dto.DtosProducto.UpdateReq;
import com.tpo.cafe.repo.CategoriaRepositorio;
import com.tpo.cafe.repo.ProductoRepositorio;
import com.tpo.cafe.usuario.Usuario;
import com.tpo.cafe.usuario.UsuarioRepositorio;

@Service
@Transactional
public class ServicioProducto {

  private final ProductoRepositorio productos;
  private final CategoriaRepositorio categorias;
  private final UsuarioRepositorio usuarios;

  public ServicioProducto(ProductoRepositorio productos, CategoriaRepositorio categorias, UsuarioRepositorio usuarios) {
    this.productos = productos;
    this.categorias = categorias;
    this.usuarios = usuarios;
  }

  public Page<Producto> searchPublic(Long categoryId, String q, Double minPrice, Double maxPrice, int page, int size) {
    return productos.searchPublic(categoryId, q, minPrice, maxPrice, PageRequest.of(page, size));
  }

  public Producto create(Long sellerId, CreateReq req) {
    Usuario seller = usuarios.findById(sellerId).orElseThrow();
    Categoria cat = categorias.findById(req.getCategoryId()).orElseThrow();

    Producto p = new Producto();
    p.setSeller(seller);
    p.setCategory(cat);
    p.setNombre(req.getNombre());
    p.setDescripcion(req.getDescripcion());
    p.setPrecio(req.getPrecio());
    p.setStock(req.getStock());
    if (req.getActivo() != null) p.setActivo(req.getActivo());

    if (req.getImageUrls() != null) {
      int i = 0;
      for (String url : req.getImageUrls()) {
        ImagenProducto img = new ImagenProducto();
        img.setProduct(p);
        img.setUrl(url);
        img.setEsPrincipal(i == 0);
        p.getImages().add(img);
        i++;
      }
    }
    return productos.save(p);
  }

  public Producto update(Long productId, Long sellerId, UpdateReq req) {
    Producto p = productos.findById(productId).orElseThrow();
    if (!p.getSeller().getId().equals(sellerId)) throw new RuntimeException("Not owner");

    if (req.getCategoryId() != null) p.setCategory(categorias.findById(req.getCategoryId()).orElseThrow());
    if (req.getNombre() != null)      p.setNombre(req.getNombre());
    if (req.getDescripcion() != null) p.setDescripcion(req.getDescripcion());
    if (req.getPrecio() != null)      p.setPrecio(req.getPrecio());
    if (req.getStock() != null)       p.setStock(req.getStock());
    if (req.getActivo() != null)      p.setActivo(req.getActivo());

    if (req.getImageUrls() != null) {
      p.getImages().clear();
      int i = 0;
      for (String url : req.getImageUrls()) {
        ImagenProducto img = new ImagenProducto();
        img.setProduct(p);
        img.setUrl(url);
        img.setEsPrincipal(i == 0);
        p.getImages().add(img);
        i++;
      }
    }
    return p; // @Transactional -> dirty-check
  }

  public void delete(Long productId, Long sellerId) {
    Producto p = productos.findById(productId).orElseThrow();
    if (!p.getSeller().getId().equals(sellerId)) throw new RuntimeException("Not owner");
    productos.delete(p);
  }

  public List<Producto> listBySeller(Long sellerId) {
    return productos.findBySellerId(sellerId);
  }
}
