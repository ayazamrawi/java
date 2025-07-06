import java.util.*;
import java.time.LocalDate;
import java.util.Scanner;



interface Product {
    String getName();
    double getPrice();
    int getQuantity();
    void setQuantity(int quantity);
    boolean isExpired();
    boolean isShippable();
    LocalDate getExpirationDate();
}

interface Shippable {
    String getName();
    double getWeight();
}

// Base product
abstract class BaseProduct implements Product {
    protected String name;
    protected double price;
    protected int quantity;

    public BaseProduct(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String getName() { return name; }

    @Override
    public double getPrice() { return price; }

    @Override
    public int getQuantity() { return quantity; }

    @Override
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public LocalDate getExpirationDate() {
        return null; 
    }
}

// Expirable
abstract class ExpirableProduct extends BaseProduct {
    protected LocalDate expirationDate;

    public ExpirableProduct(String name, double price, int quantity, LocalDate expirationDate) {
        super(name, price, quantity);
        this.expirationDate = expirationDate;
    }

    @Override
    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

    @Override
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
}


// Cheese: Expirable & Shippable
class Cheese extends ExpirableProduct implements Shippable {
    private double weight;

    public Cheese(String name, double price, int quantity, LocalDate expirationDate, double weight) {
        super(name, price, quantity, expirationDate);
        this.weight = weight;
    }

    @Override
    public boolean isShippable() { return true; }

    @Override
    public double getWeight() { return weight; }
}

// Biscuits: Expirable & Not Shippable
class Biscuits extends ExpirableProduct implements Shippable {
    private double weight;

    public Biscuits(String name, double price, int quantity, LocalDate expirationDate, double weight) {
        super(name, price, quantity, expirationDate);
        this.weight = weight;
    }

    @Override
    public boolean isShippable() { return true; } 

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public String getName() {
        return name; 
    }
}

class Milk extends ExpirableProduct implements Shippable {
    private double weight;

    public Milk(String name, double price, int quantity, LocalDate expirationDate, double weight) {
        super(name, price, quantity, expirationDate);
        this.weight = weight;
    }

    @Override
    public boolean isShippable() {
        return true;  
    }

    @Override
    public double getWeight() {
        return weight; 
    }

    @Override
    public String getName() {
        return name;
    }
}




// TV: Shippable & Not Expirable
class TV extends BaseProduct implements Shippable {
    private double weight;

    public TV(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.weight = weight;
    }

    @Override
    public boolean isExpired() { return false; }

    @Override
    public boolean isShippable() { return true; }

    @Override
    public double getWeight() { return weight; }
}

// Mobile: Not Shippable & Not Expirable
class Mobile extends BaseProduct {
    public Mobile(String name, double price, int quantity) {
        super(name, price, quantity);
    }

    @Override
    public boolean isExpired() { return false; }

    @Override
    public boolean isShippable() { return false; }
}

// Mobile Scratch Card: Not Shippable & Not Expirable
class ScratchCard extends BaseProduct {
    public ScratchCard(String name, double price, int quantity) {
        super(name, price, quantity);
    }

    @Override
    public boolean isExpired() { return false; }

    @Override
    public boolean isShippable() { return false; }
}

// Cart Item
class CartItem {
    Product product;
    int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}

// Cart
class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void addProduct(Product product, int quantity) throws Exception {
        if (quantity > product.getQuantity()) {
            throw new Exception("Requested quantity exceeds stock.");
        }
        items.add(new CartItem(product, quantity));
    }

    public List<CartItem> getItems() { return items; }

    public boolean isEmpty() { return items.isEmpty(); }
}

// ShippingService
class ShippingService {
    public void shipItems(List<Shippable> items) {
        System.out.println("Shipping the following items:");
        for (Shippable item : items) {
            System.out.println("- " + item.getName() + " (" + item.getWeight() + " kg)");
        }
    }
}

// Customer
class Customer {
    private double balance;
    private Cart cart;

    public Customer( double balance) {
        this.balance = balance;
        this.cart = new Cart();
    }

    public void addToCart(Product product, int quantity) throws Exception {
        cart.addProduct(product, quantity);
    }

    public void checkout() throws Exception {
        if (cart.isEmpty()) throw new Exception("Cart is empty.");

        double subtotal = 0;
        double shippingFee = 0;

        List<Shippable> toShip = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            Product product = item.product;

            if (product.isExpired()) {
                throw new Exception("Product " + product.getName() + " is expired.");
            }

            if (item.quantity > product.getQuantity()) {
                throw new Exception("Product " + product.getName() + " out of stock.");
            }

            subtotal += product.getPrice() * item.quantity;

            if (product.isShippable()) {
                toShip.add((Shippable) product);
                shippingFee += ((Shippable) product).getWeight() * 2; 
            }

            product.setQuantity(product.getQuantity() - item.quantity);
        }

        double total = subtotal + shippingFee;

        if (total > balance) {
            throw new Exception("Insufficient balance.");
        }

        balance -= total;

        // Print details
        System.out.println("\n** Shipment notice **");
for (CartItem item : cart.getItems()) {
    Product product = item.product;
    if (product.isShippable()) {
        double weightPerItemG = ((Shippable) product).getWeight() * 1000; 
        double totalItemWeightG = weightPerItemG * item.quantity;
        System.out.printf("%dx %s %.0fg%n", item.quantity, product.getName(), totalItemWeightG);
    }
}
for (CartItem item : cart.getItems()) {
    Product product = item.product;

    if (product.isShippable()) {
        double weightPerItemKg = ((Shippable) product).getWeight();
        double totalItemWeightKg = weightPerItemKg * item.quantity;

        
            System.out.printf("Total package weight: %.1f kg%n", totalItemWeightKg);
            toShip.add((Shippable) product);
    }
}

    System.out.println("\n** Checkout receipt **");
    for (CartItem item : cart.getItems()) {
        double lineTotal = item.quantity * item.product.getPrice();
        System.out.printf("%dx %s %.0f%n", item.quantity, item.product.getName(), lineTotal);
    }
    System.out.println("----------------------");
    System.out.printf("Subtotal %.0f%n", subtotal);
    System.out.printf("Shipping %.0f%n", shippingFee);
    System.out.printf("Amount %.0f%n", total);

        
    }
}


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Define products
        Cheese cheese = new Cheese("Cheddar Cheese", 5.0, 10, LocalDate.now().plusDays(5), 1.0);
        TV tv = new TV("Smart TV", 300.0, 2, 10.0);
        Mobile mobile = new Mobile("iPhone", 800.0, 5);
        ScratchCard scratchCard = new ScratchCard("Mobile Scratch Card", 10.0, 20);
        Biscuits biscuits = new Biscuits("Chocolate Biscuits", 2.0, 15, LocalDate.now().plusDays(3), 0.7);
        Milk milk = new Milk( "Fresh Milk",3.0,20,LocalDate.now().plusDays(7),1.0);


        
        List<Product> products = Arrays.asList(cheese, tv, mobile, scratchCard, biscuits, milk);

        
        Customer customer = new Customer( 1000.0);

        boolean shopping = true;

        while (shopping) {
            System.out.println("\nAvailable Products:");
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                String info = (i + 1) + ". " + p.getName() + " ($" + p.getPrice() + ") | Stock: " + p.getQuantity();
                if (p.getExpirationDate() != null) {
                    info += " | Expires: " + p.getExpirationDate();
                }
                System.out.println(info);
            }

            System.out.print("Select product number to add to cart (or 0 to checkout): ");
            int choice = scanner.nextInt();

            if (choice == 0) {
                shopping = false;
                break;
            }

            if (choice < 1 || choice > products.size()) {
                System.out.println("Invalid product number.");
                continue;
            }

            Product selectedProduct = products.get(choice - 1);

            System.out.print("Enter quantity: ");
            int qty = scanner.nextInt();

            try {
                customer.addToCart(selectedProduct, qty);
                System.out.println(qty + " x " + selectedProduct.getName() + " added to cart.");
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        // Checkout
        try {
            customer.checkout();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        scanner.close();
    }
}