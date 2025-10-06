import com.adatech.ecommerce.application.CustomerService;
import com.adatech.ecommerce.application.OrderService;
import com.adatech.ecommerce.application.ProductService;
import com.adatech.ecommerce.application.CouponService;
import com.adatech.ecommerce.application.DiscountRuleService;
//import com.adatech.ecommerce.application.CouponManagement;
//import com.adatech.ecommerce.application.DiscountRuleManagement;
import com.adatech.ecommerce.domain.base.InMemoryRepository;
import com.adatech.ecommerce.domain.customer.Customer;
import com.adatech.ecommerce.domain.notification.ConsoleNotifier;
import com.adatech.ecommerce.domain.notification.Notifier;
import com.adatech.ecommerce.domain.order.Order;
import com.adatech.ecommerce.domain.product.Product;
import com.adatech.ecommerce.domain.coupon.Coupon;
import com.adatech.ecommerce.domain.coupon.DiscountType;
import com.adatech.ecommerce.domain.discount.DiscountRule;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

         Scanner sc = new Scanner(System.in);


        // Repositorios
         var customerRepo = new InMemoryRepository<Customer, UUID>();
         var produtRepo = new InMemoryRepository<Product, UUID>();
         var orderRepo = new InMemoryRepository<Order, UUID>();
         var couponRepo = new InMemoryRepository<Coupon, UUID>();
         var ruleRepo = new InMemoryRepository<DiscountRule, UUID>();

         //Servicios
        CustomerService customerService = new CustomerService(customerRepo);
        ProductService productService = new ProductService(produtRepo);
        Notifier notifier = new ConsoleNotifier();
        CouponService couponService = new CouponService(couponRepo);
        DiscountRuleService discountRuleService = new DiscountRuleService(ruleRepo);
        OrderService orderService = new OrderService(orderRepo, produtRepo, customerRepo, notifier, couponService, discountRuleService);

        while (true){

            System.out.println("======MEMU E-COMMERCE======");
            System.out.println("1-  Cadastrar Cliente");
            System.out.println("2-  Listar Clientes");
            System.out.println("3-  Cadastar Produto");
            System.out.println("4-  listar Produtos");
            System.out.println("5-  Criar Pedido");
            System.out.println("6-  Adicionar Item ao Pedido");
            System.out.println("7-  Remover Item do Pedido");
            System.out.println("8-  Alterar quantidade do Item do pedido");
            System.out.println("9-  Finalizar Pedido");
            System.out.println("10- Pagar Pedido");
            System.out.println("11- Entregar Pedido");
            System.out.println("12- Listar Pedidos");
            System.out.println("13- Aplicar Cupón de Descuento");
            System.out.println("14- Remover Cupón de Descuento");
            System.out.println("15- Criar Cupons de desconto");
            System.out.println("16- Listar Cupones Disponibles");
            System.out.println("17- Atualizar Cupón de Desconto");
            System.out.println("0- Sair");
            System.out.print(  "==== Escolha uma opcao: ====");

            int opcao = sc.nextInt();

            try {
                switch (opcao){
                    case 1 -> cadastrarCliente(customerService, sc);
                    case 2 -> listarClientes(customerService, sc);
                    case 3 -> cadastrarProduto(productService, sc);
                    case 4 -> listarProdutos(productService, sc);
                    case 5 -> criarPedido(orderService, sc);
                    case 6 -> adicionarItemPedido(orderService, sc);
                    case 7 -> removerItemPedido(orderService, sc);
                    case 8 -> alterarQtdItemPedido(orderService, sc);
                    case 9 -> finalizarPedido(orderService, sc);
                    case 10 -> pagarPedido(orderService, sc);
                    case 11 -> entregarPedido(orderService, sc);
                    case 12 -> listarPedidos(orderService);
                    case 13 -> aplicarCupon(orderService, couponService, sc);
                    case 14 -> removerCupon(orderService, sc);
                    case 15 -> criarCouponsDeDesconto(couponService, sc);
                    case 16 -> listarCuponesDisponibles(couponService);
                    case 17 -> atualizarCuponDeDesconto(couponService, sc);
                    case 0 -> {
                        System.out.println("Saindo...");
                        System.exit(0);
                    }


                }



            ;
        }catch (Exception e){
                System.out.println("Erro: " + e.getMessage());
            }

        }


    }
    public static void  cadastrarCliente(CustomerService customerService,Scanner sc){
        sc.nextLine();
        System.out.println("Digite o nome: ");
        String nome = sc.nextLine();
        System.out.println("Digite documento");
        String documento = sc.nextLine();
        System.out.println("Digite email");
        String email = sc.nextLine();
        customerService.register(nome, documento, email);
    }

    public static void listarClientes(CustomerService customerService, Scanner sc){
        List<Customer> clientes = customerService.listAll();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado");
        }else {
            System.out.println("Clientes cadastrados: ");
            clientes.forEach(c -> System.out.println("ID: " + c.getId() + " Nome: " + c.getName() + " Documento: " + c.getDocumentId() + " Email: " + c.getEmail()));
        }
    }

    public static void cadastrarProduto(ProductService productService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o nombre do producto:");
         String nomeProduto = sc.nextLine();
        System.out.println("Digite o precio do producto: ");
        BigDecimal precoProduto = new BigDecimal(sc.nextLine());
        var p = productService.registrer(nomeProduto, precoProduto);
        System.out.println("Producto cadastrado com sucesso! ID: " + p.getId() + " Nome: " + p.getName() + " Preco: " + p.getBasePrice());
    }

    public  static  void listarProdutos(ProductService productService, Scanner sc){
        List<Product> products = productService.listAll();
        if (products.isEmpty()){
            System.out.println("Nenhum produto cadastrado");
            }else {
            products.forEach(p-> System.out.println("ID: " + p.getId() + " Nome: " + p.getName() + " Preco: " + p.getBasePrice()));
        }
    }

    public static void criarPedido(OrderService orderService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o ID do cliente: ");
        String idCliente = sc.nextLine();
        var o = orderService.createOrder(UUID.fromString(idCliente));
        System.out.println("Pedido criado com sucesso! ID do pedido: " + o.getId() + " ID do cliente: " + o.getCustomerId() + " Status: " + o.getStatus());
    }

    public static void adicionarItemPedido(OrderService orderService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        System.out.println("Digite o ID do produto: ");
        String idProduto = sc.nextLine();
        System.out.println("Digite a quantidade: ");
        int quantidade = sc.nextInt();
        sc.nextLine();

        System.out.println("Digite o preco de venda: ");
        BigDecimal precoVenda = new BigDecimal(sc.nextLine());
        var o = orderService.addItem(UUID.fromString(idPedido), UUID.fromString(idProduto), quantidade, precoVenda);
        System.out.println("Item adicionado com sucesso! ID do pedido: " + o.getId() + " Total $ " + o.getTotal());
    }

    public static void removerItemPedido(OrderService orderService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        System.out.println("Digite o ID do produto: ");
        String idProduto = sc.nextLine();
        var o = orderService.removeItem(UUID.fromString(idPedido), UUID.fromString(idProduto));
        System.out.println("Item removido com sucesso! ID do pedido: " + o.getId() + " Total $: " + o.getTotal());
    }

    public static void alterarQtdItemPedido(OrderService orderService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        System.out.println("Digite o ID do produto: ");
        String idProduto = sc.nextLine();
        System.out.println("Digite a nova quantidade: ");
        int novaQuantidade = sc.nextInt();
        var o = orderService.changeQuantity(UUID.fromString(idPedido), UUID.fromString(idProduto), novaQuantidade);
        System.out.println("Quantidade alterada com sucesso! ID do pedido: " + o.getId() + " Total $: " + o.getTotal());
    }

    public static void finalizarPedido(OrderService orderService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        var o = orderService.checkout(UUID.fromString(idPedido));
        System.out.println("Pedido finalizado com sucesso! ID do pedido: " + o.getId() + " Total $: " + o.getTotal() + " Status: " + o.getStatus() + " Payment Status: " + o.getPaymentStatus());
    }

    public static void pagarPedido(OrderService orderService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        var o = orderService.pay(UUID.fromString(idPedido));
        System.out.println("Pedido pago com sucesso! ID do pedido: " + o.getId() + " Total $: " + o.getTotal() + " Status: " + o.getStatus() + " Payment Status: " + o.getPaymentStatus());
    }

    public static void entregarPedido(OrderService orderService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        var o = orderService.deliver(UUID.fromString(idPedido));
        System.out.println("Pedido entregue com sucesso! ID do pedido: " + o.getId() + " Total $: " + o.getTotal() + " Status: " + o.getStatus() + " Payment Status: " + o.getPaymentStatus());
    }


    private static void listarPedidos(OrderService orderService) {
        orderService.getOrderRepo().findAll().forEach(System.out::println);
    }

    public static void aplicarCupon(OrderService orderService, CouponService couponService, Scanner sc) {
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        System.out.println("Digite o código do cupón: ");
        String codigoCupon = sc.nextLine();
        // Validación correcta de expiración del cupón antes de aplicar
        try {
            Coupon cupon = couponService.findCouponByCode(codigoCupon);
            if (cupon.getValidUntil() != null && cupon.getValidUntil().isBefore(LocalDateTime.now())) {
                System.out.println("Cupon expirado");
                return;
            }
        } catch (Exception notFound) {
            System.out.println("Cupon nao encontrado");
            return;
        }

        try {
            var o = orderService.applyCoupon(UUID.fromString(idPedido), codigoCupon);
            System.out.println("Cupón aplicado com sucesso! ID do pedido: " + o.getId() + 
                              " Subtotal: " + o.getSubtotal() + " Descuento: " + o.getCouponDiscount() + 
                              " Total: " + o.getTotal());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: ID del pedido inválido. " + e.getMessage());
        } catch (com.adatech.ecommerce.domain.base.DomainException e) {
            System.out.println("Error: " + e.getMessage());}

    }

    public static void removerCupon(OrderService orderService, Scanner sc) {
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        var o = orderService.removeCoupon(UUID.fromString(idPedido));
        System.out.println("Cupón removido com sucesso! ID do pedido: " + o.getId() + " Total: " + o.getTotal());
    }



    public static void criarCouponsDeDesconto(CouponService couponService, Scanner sc){
        sc.nextLine();
        
        System.out.println("=== CREAR CUPÓN DE DESCUENTO ===");
        
        try {
            // Solicitar código del cupón
            System.out.println("Digite el código del cupón: ");
            String codigo = sc.nextLine();
            
            // Solicitar descripción
            System.out.println("Digite la descripción del cupón: ");
            String descripcion = sc.nextLine();
            
            // Solicitar tipo de descuento
            System.out.println("Seleccione el tipo de descuento:");
            System.out.println("1- Descuento fijo");
            System.out.println("2- Descuento porcentual");
            System.out.println("3- Descuento progresivo");
            System.out.print("Opción: ");
            int tipoDescuento = sc.nextInt();
            sc.nextLine();
            
            DiscountType discountType;
            if (tipoDescuento == 1) {
                discountType = DiscountType.FIXED_AMOUNT;
            } else if (tipoDescuento == 2) {
                discountType = DiscountType.PERCENTAGE;
            }else if (tipoDescuento == 3) {
                discountType = DiscountType.PROGRESSIVE;
            
            } else {
                System.out.println("Opción inválida. Se usará descuento fijo por defecto.");
                discountType = DiscountType.FIXED_AMOUNT;
            }
            
            // Solicitar valor del descuento
            System.out.println("Digite el valor del descuento: ");
            BigDecimal valorDescuento = new BigDecimal(sc.nextLine());
            
            // Solicitar fecha de inicio de validez
            System.out.println("Digite la fecha de inicio de validez (formato: yyyy-MM-dd HH:mm): ");
            String fechaInicioStr = sc.nextLine();
            LocalDateTime fechaInicio = parseDateTime(fechaInicioStr);
            
            // Solicitar fecha de fin de validez
            System.out.println("Digite la fecha de fin de validez (formato: yyyy-MM-dd HH:mm): ");
            String fechaFinStr = sc.nextLine();
            LocalDateTime fechaFin = parseDateTime(fechaFinStr);
            
            // Solicitar máximo de usos
            System.out.println("Digite el número máximo de usos (0 para ilimitado): ");
            int maxUsos = sc.nextInt();
            sc.nextLine();
            
            // Solicitar valor mínimo del pedido
            System.out.println("Digite el valor mínimo del pedido para aplicar el cupón: ");
            BigDecimal valorMinimoPedido = new BigDecimal(sc.nextLine());
            
            // Crear el cupón
            Coupon cupon = couponService.createCoupon(
                codigo,
                descripcion,
                valorDescuento,
                discountType,
                fechaInicio,
                fechaFin,
                maxUsos,
                valorMinimoPedido
            );
            
            System.out.println("¡Cupón creado con éxito!");
            System.out.println("ID: " + cupon.getId());
            System.out.println("Código: " + cupon.getCode());
            System.out.println("Descripción: " + cupon.getDescription());
            System.out.println("Tipo de descuento: " + cupon.getDiscountType());
            System.out.println("Valor del descuento: " + cupon.getDiscountValue());
            System.out.println("Válido desde: " + cupon.getValidFrom());
            System.out.println("Válido hasta: " + cupon.getValidUntil());
            System.out.println("Máximo de usos: " + (cupon.getMaxUses() == 0 ? "Ilimitado" : cupon.getMaxUses()));
            System.out.println("Valor mínimo del pedido: " + cupon.getMinimumOrderValue());
            
        } catch (Exception e) {
            System.out.println("Error al crear el cupón: " + e.getMessage());
        }
    }

    public static void listarCuponesDisponibles(CouponService couponService) {
        System.out.println("=== CUPONES DISPONIBLES ===");
        
        try {
            List<Coupon> cupones = couponService.listAvailableCoupons();
            
            if (cupones.isEmpty()) {
                System.out.println("No hay cupones disponibles en este momento.");
            } else {
                System.out.println("----------------------------------------");
                
                for (Coupon cupon : cupones) {
                    System.out.println("Código: " + cupon.getCode());
                    System.out.println("Descripción: " + cupon.getDescription());
                    System.out.println("Tipo: " + cupon.getDiscountType());
                    System.out.println("Valor: " + cupon.getDiscountValue());
                    System.out.println("Válido desde: " + cupon.getValidFrom());
                    System.out.println("Válido hasta: " + cupon.getValidUntil());
                    System.out.println("Usos restantes: " + (cupon.getMaxUses() - cupon.getCurrentUses()));
                    System.out.println("Valor mínimo del pedido: " + cupon.getMinimumOrderValue());
                    System.out.println("----------------------------------------");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar cupones: " + e.getMessage());
        }
    }



    private static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            // Intentar con formato yyyy-MM-dd HH:mm
            return LocalDateTime.parse(dateTimeStr.replace(" ", "T"));
        } catch (Exception e) {
            System.out.println("Formato de fecha inválido. Usando fecha actual como fallback.");
            return LocalDateTime.now();
        }
    }

    public static void atualizarCuponDeDesconto(CouponService couponService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o codigo do cupon: ");
        String codigo = sc.nextLine();
        try {
            Coupon coupon = couponService.findCouponByCode(codigo);
            System.out.println("ID : " + coupon.getCode());
            System.out.println("Descripcion : " + coupon.getDescription());
            System.out.println("Valor de descuento : " + coupon.getDiscountValue());
            System.out.println("Tipo de descuento : "+ coupon.getDiscountType());
            System.out.println("Fecha de inicio : " + coupon.getValidFrom() );
            System.out.println("Fecha de finalizacion : " + coupon.getValidUntil());
            System.out.println("Usos disponibles : " + coupon.getMaxUses());
            System.out.println("Valor minimo do pedido : " + coupon.getMinimumOrderValue());

            System.out.println("\n=== Actualizar datos del cupón (precione Enter para mantener) ===");

            // Descripción
            System.out.print("Nueva descripción [" + coupon.getDescription() + "]: ");
            String nuevaDescripcion = sc.nextLine();
            if (nuevaDescripcion.isBlank()) nuevaDescripcion = coupon.getDescription();

            // Tipo de descuento
            System.out.print("Tipo de descuento (1=FIXED_AMOUNT, 2=PERCENTAGE) [" + coupon.getDiscountType() + "]: ");
            String tipoStr = sc.nextLine();
            DiscountType nuevoTipo;
            if (tipoStr.isBlank()) {
                nuevoTipo = coupon.getDiscountType();
            } else if ("1".equals(tipoStr.trim())) {
                nuevoTipo = DiscountType.FIXED_AMOUNT;
            } else if ("2".equals(tipoStr.trim())) {
                nuevoTipo = DiscountType.PERCENTAGE;
            } else {
                System.out.println("Opción inválida. Manteniendo tipo actual.");
                nuevoTipo = coupon.getDiscountType();
            }

            // Valor de descuento
            System.out.print("Valor de descuento [" + coupon.getDiscountValue() + "]: ");
            String valorStr = sc.nextLine();
            java.math.BigDecimal nuevoValor = valorStr.isBlank() ? coupon.getDiscountValue() : new java.math.BigDecimal(valorStr.trim());

            // Fecha inicio
            System.out.print("Fecha de inicio (yyyy-MM-dd HH:mm) [" + coupon.getValidFrom() + "]: ");
            String desdeStr = sc.nextLine();
            java.time.LocalDateTime nuevaFechaInicio = desdeStr.isBlank() ? coupon.getValidFrom() : parseDateTime(desdeStr.trim());

            // Fecha fin
            System.out.print("Fecha de finalización (yyyy-MM-dd HH:mm) [" + coupon.getValidUntil() + "]: ");
            String hastaStr = sc.nextLine();
            java.time.LocalDateTime nuevaFechaFin = hastaStr.isBlank() ? coupon.getValidUntil() : parseDateTime(hastaStr.trim());

            // Máximo de usos
            System.out.print("Máximo de usos (0 = ilimitado) [" + coupon.getMaxUses() + "]: ");
            String maxUsosStr = sc.nextLine();
            int nuevoMaxUsos = maxUsosStr.isBlank() ? coupon.getMaxUses() : Integer.parseInt(maxUsosStr.trim());

            // Valor mínimo del pedido
            System.out.print("Valor mínimo del pedido [" + coupon.getMinimumOrderValue() + "]: ");
            String minValorStr = sc.nextLine();
            java.math.BigDecimal nuevoMinValor = minValorStr.isBlank() ? coupon.getMinimumOrderValue() : new java.math.BigDecimal(minValorStr.trim());

            // Guardar actualización
            Coupon actualizado = couponService.updateCoupon(
                    coupon.getId(),
                    nuevaDescripcion,
                    nuevoValor,
                    nuevoTipo,
                    nuevaFechaInicio,
                    nuevaFechaFin,
                    nuevoMaxUsos,
                    nuevoMinValor
            );

            System.out.println("\nCupón actualizado con éxito:");
            System.out.println("Código: " + actualizado.getCode());
            System.out.println("Descripción: " + actualizado.getDescription());
            System.out.println("Tipo: " + actualizado.getDiscountType());
            System.out.println("Valor: " + actualizado.getDiscountValue());
            System.out.println("Válido desde: " + actualizado.getValidFrom());
            System.out.println("Válido hasta: " + actualizado.getValidUntil());
            System.out.println("Máximo de usos: " + actualizado.getMaxUses());
            System.out.println("Valor mínimo: " + actualizado.getMinimumOrderValue());
        } catch (Exception e) {
            System.out.println("Cupon nao encontrado");
        }
        }


    }

