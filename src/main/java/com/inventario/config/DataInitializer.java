package com.inventario.config;

import com.inventario.model.*;
import com.inventario.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private CategoriaRepository categoriaRepo;

    @Autowired
    private ProveedorRepository proveedorRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private AlmacenRepository almacenRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepo.count() == 0) {
            System.out.println("=== Inicializando datos de prueba ===");

            // 1. Crear roles
            Rol rolAdmin = new Rol("ADMIN", "Administrador del sistema");
            Rol rolGerente = new Rol("GERENTE", "Gerente de inventario");
            Rol rolOperador = new Rol("OPERADOR", "Operador de almacén");

            rolAdmin = rolRepo.save(rolAdmin);
            rolGerente = rolRepo.save(rolGerente);
            rolOperador = rolRepo.save(rolOperador);

            // 2. Crear usuarios
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombreCompleto("Administrador del Sistema");
            admin.setEmail("admin@inventario.com");
            admin.setTelefono("3001234567");
            admin.setRol(rolAdmin);
            admin.setActivo(true);
            usuarioRepo.save(admin);

            Usuario gerente = new Usuario();
            gerente.setUsername("gerente");
            gerente.setPassword(passwordEncoder.encode("gerente123"));
            gerente.setNombreCompleto("Juan Pérez");
            gerente.setEmail("gerente@inventario.com");
            gerente.setTelefono("3007654321");
            gerente.setRol(rolGerente);
            gerente.setActivo(true);
            usuarioRepo.save(gerente);

            // 3. Crear categorías
            Categoria catElectronicos = new Categoria("ELEC", "Electrónicos");
            catElectronicos.setDescripcion("Productos electrónicos y tecnología");
            catElectronicos.setIcono("bi-laptop");
            catElectronicos.setColor("#3b82f6");
            categoriaRepo.save(catElectronicos);

            Categoria catAlimentos = new Categoria("ALIM", "Alimentos");
            catAlimentos.setDescripcion("Productos alimenticios");
            catAlimentos.setIcono("bi-basket");
            catAlimentos.setColor("#10b981");
            categoriaRepo.save(catAlimentos);

            Categoria catRopa = new Categoria("ROPA", "Ropa y Textiles");
            catRopa.setDescripcion("Prendas de vestir");
            catRopa.setIcono("bi-bag");
            catRopa.setColor("#f59e0b");
            categoriaRepo.save(catRopa);

            // 4. Crear proveedores - CORREGIDO
            Proveedor prov1 = new Proveedor("PROV001", "Tech Solutions S.A.");
            prov1.setRazonSocial("Tech Solutions S.A.");
            prov1.setRfc("900123456-7");
            prov1.setTelefono("3201234567");
            prov1.setEmail("ventas@techsol.com");
            prov1.setDireccion("Calle 100 #15-20");
            prov1.setCiudad("Bogotá");
            prov1.setPais("Colombia");
            prov1.setDiasCredito(30);
            prov1.setLimiteCredito(50000000.0);
            proveedorRepo.save(prov1);

            Proveedor prov2 = new Proveedor("PROV002", "Distribuidora Nacional");
            prov2.setRazonSocial("Distribuidora Nacional S.A.S");
            prov2.setRfc("900987654-3");
            prov2.setTelefono("3109876543");
            prov2.setEmail("info@disnac.com");
            prov2.setDireccion("Carrera 50 #30-45");
            prov2.setCiudad("Medellín");
            prov2.setPais("Colombia");
            prov2.setDiasCredito(45);
            prov2.setLimiteCredito(30000000.0);
            proveedorRepo.save(prov2);

            // 5. Crear almacenes
            Almacen almPrincipal = new Almacen("ALM001", "Almacén Principal");
            almPrincipal.setDescripcion("Almacén central de distribución");
            almPrincipal.setDireccion("Zona Industrial Calle 80");
            almPrincipal.setCiudad("Bogotá");
            almPrincipal.setPais("Colombia");
            almPrincipal.setCapacidadMaxima(10000.0);
            almPrincipal.setUsuarioResponsable(gerente);
            almacenRepo.save(almPrincipal);

            Almacen almSecundario = new Almacen("ALM002", "Almacén Secundario");
            almSecundario.setDescripcion("Almacén de respaldo");
            almSecundario.setDireccion("Autopista Norte Km 5");
            almSecundario.setCiudad("Bogotá");
            almSecundario.setPais("Colombia");
            almSecundario.setCapacidadMaxima(5000.0);
            almacenRepo.save(almSecundario);

            // 6. Crear clientes - CORREGIDO
            Cliente cliente1 = new Cliente("CLI001", "Empresa ABC S.A.S");
            cliente1.setTelefono("3151234567");
            cliente1.setEmail("compras@abc.com");
            cliente1.setRfc("800123456-9");
            cliente1.setDireccion("Carrera 7 #32-16");
            cliente1.setCiudad("Bogotá");
            cliente1.setPais("Colombia");
            cliente1.setDiasCredito(30);
            cliente1.setLimiteCredito(20000000.0);
            cliente1.setSaldoActual(0.0);
            cliente1.setDescuentoGeneral(0.0);
            clienteRepo.save(cliente1);

            // 7. Crear productos de prueba
            crearProductosPrueba(catElectronicos, catAlimentos, catRopa, prov1, prov2);

            System.out.println("=== Datos de prueba creados exitosamente ===");
            System.out.println("Usuario Admin: admin / admin123");
            System.out.println("Usuario Gerente: gerente / gerente123");
        }
    }

    private void crearProductosPrueba(Categoria catElec, Categoria catAlim,
            Categoria catRopa, Proveedor prov1, Proveedor prov2) {
        // Productos electrónicos
        Producto laptop = new Producto("PROD001", "Laptop Dell Inspiron 15", 2500000.0);
        laptop.setDescripcion("Laptop Intel Core i5, 8GB RAM, 256GB SSD");
        laptop.setCategoria(catElec);
        laptop.setProveedor(prov1);
        laptop.setCodigoBarras("7891234567890");
        laptop.setPrecioCompra(2000000.0);
        laptop.setStockActual(15);
        laptop.setStockMinimo(5);
        laptop.setStockMaximo(50);
        laptop.setUnidadMedida("UND");
        laptop.setMarca("Dell");
        laptop.setModelo("Inspiron 15");
        productoRepo.save(laptop);

        Producto mouse = new Producto("PROD002", "Mouse Inalámbrico Logitech", 45000.0);
        mouse.setDescripcion("Mouse ergonómico inalámbrico");
        mouse.setCategoria(catElec);
        mouse.setProveedor(prov1);
        mouse.setCodigoBarras("7891234567891");
        mouse.setPrecioCompra(30000.0);
        mouse.setStockActual(3);
        mouse.setStockMinimo(10);
        mouse.setStockMaximo(100);
        mouse.setMarca("Logitech");
        productoRepo.save(mouse);

        Producto teclado = new Producto("PROD003", "Teclado Mecánico RGB", 180000.0);
        teclado.setDescripcion("Teclado mecánico con iluminación RGB");
        teclado.setCategoria(catElec);
        teclado.setProveedor(prov1);
        teclado.setPrecioCompra(120000.0);
        teclado.setStockActual(0);
        teclado.setStockMinimo(5);
        teclado.setStockMaximo(30);
        productoRepo.save(teclado);

        // Productos de alimentos
        Producto arroz = new Producto("PROD004", "Arroz Diana x 5kg", 15000.0);
        arroz.setDescripcion("Arroz blanco de primera calidad");
        arroz.setCategoria(catAlim);
        arroz.setProveedor(prov2);
        arroz.setPrecioCompra(12000.0);
        arroz.setStockActual(100);
        arroz.setStockMinimo(20);
        arroz.setStockMaximo(200);
        arroz.setEsPerecedero(false);
        arroz.setUnidadMedida("KG");
        productoRepo.save(arroz);

        Producto aceite = new Producto("PROD005", "Aceite Vegetal 1L", 8500.0);
        aceite.setDescripcion("Aceite vegetal comestible");
        aceite.setCategoria(catAlim);
        aceite.setProveedor(prov2);
        aceite.setPrecioCompra(6500.0);
        aceite.setStockActual(25);
        aceite.setStockMinimo(30);
        aceite.setStockMaximo(150);
        aceite.setEsPerecedero(false);
        aceite.setUnidadMedida("LT");
        aceite.setFechaVencimiento(LocalDate.now().plusMonths(6));
        productoRepo.save(aceite);

        // Productos de ropa
        Producto camisa = new Producto("PROD006", "Camisa Casual Hombre", 65000.0);
        camisa.setDescripcion("Camisa manga larga 100% algodón");
        camisa.setCategoria(catRopa);
        camisa.setProveedor(prov2);
        camisa.setPrecioCompra(40000.0);
        camisa.setStockActual(20);
        camisa.setStockMinimo(10);
        camisa.setStockMaximo(80);
        camisa.setMarca("Generic");
        camisa.setColor("Azul");
        camisa.setTalla("M");
        productoRepo.save(camisa);

        Producto zapatos = new Producto("PROD007", "Zapatos Deportivos", 120000.0);
        zapatos.setDescripcion("Zapatos deportivos para running");
        zapatos.setCategoria(catRopa);
        zapatos.setProveedor(prov2);
        zapatos.setPrecioCompra(80000.0);
        zapatos.setStockActual(2);
        zapatos.setStockMinimo(8);
        zapatos.setStockMaximo(40);
        zapatos.setMarca("Generic Sport");
        zapatos.setTalla("42");
        productoRepo.save(zapatos);

        System.out.println("✓ Productos de prueba creados");
    }
}
