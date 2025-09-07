import com.adatech.ecommerce.application.CustomerService;
import com.adatech.ecommerce.application.OrderService;
import com.adatech.ecommerce.application.ProductService;
import com.adatech.ecommerce.domain.base.InMemoryRepository;
import com.adatech.ecommerce.domain.customer.Customer;
import com.adatech.ecommerce.domain.notification.ConsoleNotifier;
import com.adatech.ecommerce.domain.notification.Notifier;
import com.adatech.ecommerce.domain.order.Order;
import com.adatech.ecommerce.domain.product.Product;

import java.math.BigDecimal;
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



         //Servicios
        CustomerService customerService = new CustomerService(customerRepo);
        ProductService productService = new ProductService(produtRepo);
        Notifier notifier = new ConsoleNotifier();
        OrderService orderService = new OrderService(orderRepo, produtRepo, customerRepo, notifier);

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
            System.out.println("0- Sair");
            System.out.print(  "==== Escolha uma opcao: ====");

            int opcao = sc.nextInt();

            try {
                switch (opcao){
                    case 1 -> cadastrarCliente(customerService, sc);
                    case 2 -> listarClientes(customerService, sc);
                    case 3 -> cadastrarProduto(productService, sc);
                    case 4 -> listarProdutos(productService, sc);
                    case 5 -> criarPedido(orderService);
                    case 6 -> adicionarItemPedido(orderService, sc);
                    case 7 -> removerItemPedido(orderService, sc);
                    case 8 -> alterarQtdItemPedido(orderService, sc);
                    case 9 -> finalizarPedido(orderService, sc);
                    case 10 -> pagarPedido(orderService, sc);
                    case 11 -> entregarPedido(orderService, sc);
                    case 12 -> listarPedidos(orderService);
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
        var c = customerService.register(nome, documento, email);
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
        System.out.println("Digite o nome do produto:");
         String nomeProduto = sc.nextLine();
        System.out.println("Digite o preco do produto: ");
        BigDecimal precoProduto = new BigDecimal(sc.nextLine());
        var p = productService.registrer(nomeProduto, precoProduto);
        System.out.println("Produto cadastrado com sucesso! ID: " + p.getId() + " Nome: " + p.getName() + " Preco: " + p.getBasePrice());
    }

    public  static  void listarProdutos(ProductService productService, Scanner sc){
        List<Product> products = productService.listAll();
        if (products.isEmpty()){
            System.out.println("Nenhum produto cadastrado");
            }else {
            products.forEach(p-> System.out.println("ID: " + p.getId() + " Nome: " + p.getName() + " Preco: " + p.getBasePrice()));
        }
    }

    public static void criarPedido(OrderService orderService){
        System.out.println("Digite o ID do cliente: ");
        Scanner sc = new Scanner(System.in);
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
        System.out.println("Item adicionado com sucesso! ID do pedido: " + o.getId() + " Total: " + o.getTotal());
    }

    public static void removerItemPedido(OrderService orderService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        System.out.println("Digite o ID do produto: ");
        String idProduto = sc.nextLine();
        var o = orderService.removeItem(UUID.fromString(idPedido), UUID.fromString(idProduto));
        System.out.println("Item removido com sucesso! ID do pedido: " + o.getId() + " Total: " + o.getTotal());
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
        System.out.println("Quantidade alterada com sucesso! ID do pedido: " + o.getId() + " Total: " + o.getTotal());
    }

    public static void finalizarPedido(OrderService orderService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        var o = orderService.checkout(UUID.fromString(idPedido));
        System.out.println("Pedido finalizado com sucesso! ID do pedido: " + o.getId() + " Total: " + o.getTotal() + " Status: " + o.getStatus() + " Payment Status: " + o.getPaymentStatus());
    }

    public static void pagarPedido(OrderService orderService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        var o = orderService.pay(UUID.fromString(idPedido));
        System.out.println("Pedido pago com sucesso! ID do pedido: " + o.getId() + " Total: " + o.getTotal() + " Status: " + o.getStatus() + " Payment Status: " + o.getPaymentStatus());
    }

    public static void entregarPedido(OrderService orderService, Scanner sc){
        sc.nextLine();
        System.out.println("Digite o ID do pedido: ");
        String idPedido = sc.nextLine();
        var o = orderService.deliver(UUID.fromString(idPedido));
        System.out.println("Pedido entregue com sucesso! ID do pedido: " + o.getId() + " Total: " + o.getTotal() + " Status: " + o.getStatus() + " Payment Status: " + o.getPaymentStatus());
    }


    private static void listarPedidos(OrderService orderService) {
        orderService.getOrderRepo().findAll().forEach(System.out::println);
    }




}