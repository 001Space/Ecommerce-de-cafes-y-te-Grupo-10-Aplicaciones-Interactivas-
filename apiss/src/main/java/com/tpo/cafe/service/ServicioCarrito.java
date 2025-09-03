package com.tpo.cafe.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpo.cafe.dominios.Carrito;
import com.tpo.cafe.dominios.EstadoCarrito;
import com.tpo.cafe.dominios.ItemCarrito;
import com.tpo.cafe.dominios.ItemPedido;
import com.tpo.cafe.dominios.Pedido;
import com.tpo.cafe.dominios.Producto;
import com.tpo.cafe.dto.DtosCarrito.AddItemReq;
import com.tpo.cafe.dto.DtosCarrito.UpdateItemReq;
import com.tpo.cafe.repo.CarritoRepositorio;
import com.tpo.cafe.repo.PedidoRepositorio;
import com.tpo.cafe.repo.ProductoRepositorio;
import com.tpo.cafe.usuario.Usuario;

@Service
@Transactional
public class ServicioCarrito {

  private final CarritoRepositorio carritos;
  private final ProductoRepositorio productos;
  private final PedidoRepositorio pedidos;

  public ServicioCarrito(CarritoRepositorio carritos, ProductoRepositorio productos, PedidoRepositorio pedidos) {
    this.carritos = carritos;
    this.productos = productos;
    this.pedidos = pedidos;
  }

  private Carrito getCarritoAbierto(Usuario usuario) {
    return carritos.findByUsuarioAndEstado(usuario, EstadoCarrito.OPEN).orElseGet(() -> {
      Carrito c = new Carrito();
      c.setUsuario(usuario);
      c.setEstado(EstadoCarrito.OPEN);
      return carritos.save(c);
    });
  }

  public Carrito addItem(Usuario usuario, AddItemReq req) {
    Carrito carrito = getCarritoAbierto(usuario);
    Producto prod = productos.findById(req.getProductId()).orElseThrow();

    if (!Boolean.TRUE.equals(prod.getActivo()) || prod.getStock() <= 0) {
      throw new RuntimeException("Producto no disponible");
    }

    Optional<ItemCarrito> existente = carrito.getItems().stream()
        .filter(ci -> ci.getProducto().getId().equals(prod.getId()))
        .findFirst();

    int nuevaCant = req.getCantidad();
    if (existente.isPresent()) {
      nuevaCant += existente.get().getCantidad();
      carrito.getItems().remove(existente.get());
    }
    if (nuevaCant > prod.getStock()) throw new RuntimeException("Stock insuficiente");

    ItemCarrito item = new ItemCarrito();
    item.setCarrito(carrito);
    item.setProducto(prod);
    item.setCantidad(nuevaCant);

    double descuento = 0.0; // si luego agregás Descuento en Producto, calculalo aquí

    item.setPorcentajeDescuento(descuento);
    item.setPrecioUnitario(prod.getPrecio());
    carrito.getItems().add(item);

    return carritos.save(carrito);
  }

  public Carrito updateItem(Usuario usuario, Long itemId, UpdateItemReq req) {
    Carrito carrito = getCarritoAbierto(usuario);
    ItemCarrito item = carrito.getItems().stream()
        .filter(ci -> ci.getId().equals(itemId))
        .findFirst()
        .orElseThrow();

    if (req.getCantidad() <= 0) throw new RuntimeException("La cantidad debe ser > 0");
    if (req.getCantidad() > item.getProducto().getStock()) throw new RuntimeException("Stock insuficiente");

    item.setCantidad(req.getCantidad());
    return carritos.save(carrito);
  }

  public Carrito removeItem(Usuario usuario, Long itemId) {
    Carrito carrito = getCarritoAbierto(usuario);
    carrito.getItems().removeIf(ci -> ci.getId().equals(itemId));
    return carritos.save(carrito);
  }

  public Pedido checkout(Usuario usuario) {
    Carrito carrito = getCarritoAbierto(usuario);
    if (carrito.getItems().isEmpty()) throw new RuntimeException("El carrito está vacío");

    double total = 0.0;

    for (ItemCarrito ci : carrito.getItems()) {
      Producto p = ci.getProducto();
      if (ci.getCantidad() > p.getStock())
        throw new RuntimeException("Stock insuficiente para " + p.getNombre());

      p.setStock(p.getStock() - ci.getCantidad());

      double precioConDesc = ci.getPrecioUnitario() * (1 - ci.getPorcentajeDescuento() / 100.0);
      total += precioConDesc * ci.getCantidad();
    }

    Pedido pedido = new Pedido();
    pedido.setUsuario(usuario);
    pedido.setTotal(Math.round(total * 100.0) / 100.0);
    pedidos.save(pedido);

    for (ItemCarrito ci : carrito.getItems()) {
      ItemPedido ip = new ItemPedido();
      ip.setPedido(pedido);
      ip.setProducto(ci.getProducto());
      ip.setCantidad(ci.getCantidad());
      ip.setPrecioUnitario(ci.getPrecioUnitario());
      ip.setPorcentajeDescuento(ci.getPorcentajeDescuento());
      pedido.getItems().add(ip);
    }

    carrito.setEstado(EstadoCarrito.CHECKED_OUT);
    carritos.save(carrito);

    return pedido;
  }

  public Carrito getOpen(Usuario usuario) {
    return getCarritoAbierto(usuario);
  }
}
