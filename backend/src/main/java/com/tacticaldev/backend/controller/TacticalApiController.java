package com.tacticaldev.backend.controller;

import com.tacticaldev.backend.model.*;
import com.tacticaldev.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TacticalApiController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private VisitaRepository visitaRepository;

    @Autowired
    private AsistenciaRemotaRepository asistenciaRemotaRepository;

    @Autowired
    private MinimarketRepository minimarketRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    // --- 1. USUARIOS ---
    @GetMapping("/usuarios")
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Void> insertarUsuario(@RequestBody Usuario usuario) {
        usuarioRepository.save(usuario);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/usuarios")
    public ResponseEntity<Void> actualizarUsuario(@RequestBody Usuario usuario) {
        usuarioRepository.save(usuario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }

    // --- 2. INVENTARIO ---
    @GetMapping("/inventario")
    public List<InventarioRepuesto> obtenerInventario() {
        return inventarioRepository.findAll();
    }

    @PostMapping("/inventario")
    public ResponseEntity<Void> insertarInventario(@RequestBody InventarioRepuesto item) {
        inventarioRepository.save(item);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/inventario")
    public ResponseEntity<Void> actualizarInventario(@RequestBody InventarioRepuesto item) {
        inventarioRepository.save(item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/inventario/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable String id) {
        if (inventarioRepository.existsById(id)) {
            inventarioRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }

    // --- 3. VENTAS ---
    @GetMapping("/ventas")
    public List<Venta> obtenerVentas() {
        return ventaRepository.findAll();
    }

    @PostMapping("/ventas")
    public ResponseEntity<Void> insertarVenta(@RequestBody Venta venta) {
        ventaRepository.save(venta);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/ventas")
    public ResponseEntity<Void> actualizarVenta(@RequestBody Venta venta) {
        ventaRepository.save(venta);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/ventas/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable String id) {
        if (ventaRepository.existsById(id)) {
            ventaRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }

    // --- 4. VISITAS ---
    @GetMapping("/visitas")
    public List<Visita> obtenerVisitas() {
        return visitaRepository.findAll();
    }

    @PostMapping("/visitas")
    public ResponseEntity<Void> insertarVisita(@RequestBody Visita visita) {
        visitaRepository.save(visita);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/visitas")
    public ResponseEntity<Void> actualizarVisita(@RequestBody Visita visita) {
        visitaRepository.save(visita);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/visitas/{id}")
    public ResponseEntity<Void> eliminarVisita(@PathVariable String id) {
        if (visitaRepository.existsById(id)) {
            visitaRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }

    // --- 5. ASISTENCIAS ---
    @GetMapping("/asistencias")
    public List<AsistenciaRemota> obtenerAsistencias() {
        return asistenciaRemotaRepository.findAll();
    }

    @PostMapping("/asistencias")
    public ResponseEntity<Void> insertarAsistencia(@RequestBody AsistenciaRemota asistencia) {
        asistenciaRemotaRepository.save(asistencia);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/asistencias")
    public ResponseEntity<Void> actualizarAsistencia(@RequestBody AsistenciaRemota asistencia) {
        asistenciaRemotaRepository.save(asistencia);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/asistencias/{id}")
    public ResponseEntity<Void> eliminarAsistencia(@PathVariable String id) {
        if (asistenciaRemotaRepository.existsById(id)) {
            asistenciaRemotaRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }

    // --- 6. MINIMARKETS ---
    @GetMapping("/minimarkets")
    public List<Minimarket> obtenerMinimarkets() {
        return minimarketRepository.findAll();
    }

    @PostMapping("/minimarkets")
    public ResponseEntity<Void> insertarMinimarket(@RequestBody Minimarket minimarket) {
        minimarketRepository.save(minimarket);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/minimarkets")
    public ResponseEntity<Void> actualizarMinimarket(@RequestBody Minimarket minimarket) {
        minimarketRepository.save(minimarket);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/minimarkets/{id}")
    public ResponseEntity<Void> eliminarMinimarket(@PathVariable String id) {
        if (minimarketRepository.existsById(id)) {
            minimarketRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }

    // --- 7. EQUIPOS ---
    @GetMapping("/equipos")
    public List<EquipoRefrigeracion> obtenerEquipos() {
        return equipoRepository.findAll();
    }

    @PostMapping("/equipos")
    public ResponseEntity<Void> insertarEquipo(@RequestBody EquipoRefrigeracion equipo) {
        equipoRepository.save(equipo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/equipos")
    public ResponseEntity<Void> actualizarEquipo(@RequestBody EquipoRefrigeracion equipo) {
        equipoRepository.save(equipo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/equipos/{id}")
    public ResponseEntity<Void> eliminarEquipo(@PathVariable String id) {
        if (equipoRepository.existsById(id)) {
            equipoRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }

    // --- 8. ORDENES DE COMPRA ---
    @GetMapping("/ordenes-compra")
    public List<OrdenCompra> obtenerOrdenesCompra() {
        return ordenCompraRepository.findAll();
    }

    @PostMapping("/ordenes-compra")
    public ResponseEntity<Void> insertarOrdenCompra(@RequestBody OrdenCompra ordenCompra) {
        ordenCompraRepository.save(ordenCompra);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/ordenes-compra")
    public ResponseEntity<Void> actualizarOrdenCompra(@RequestBody OrdenCompra ordenCompra) {
        ordenCompraRepository.save(ordenCompra);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/ordenes-compra/{id}")
    public ResponseEntity<Void> eliminarOrdenCompra(@PathVariable String id) {
        if (ordenCompraRepository.existsById(id)) {
            ordenCompraRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }
}
