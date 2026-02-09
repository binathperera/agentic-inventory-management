package com.inventory.management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.inventory.management.model.Supplier;
import com.inventory.management.model.Product;
import com.inventory.management.model.Tenant;
import com.inventory.management.model.Invoice;
import com.inventory.management.model.ProductBatch;
import com.inventory.management.model.Transaction;
import com.inventory.management.model.TransactionItem;
import com.inventory.management.model.User;
import com.inventory.management.dto.Role;
import com.inventory.management.repository.ProductRepository;
import com.inventory.management.repository.SupplierRepository;
import com.inventory.management.repository.TenantRepository;
import com.inventory.management.repository.InvoiceRepository;
import com.inventory.management.repository.ProductBatchRepository;
import com.inventory.management.repository.TransactionRepository;
import com.inventory.management.repository.TransactionItemRepository;
import com.inventory.management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@Profile("!test")
public class SampleDataLoader implements CommandLineRunner {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProductBatchRepository productBatchRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Checking if data already exists
            Tenant tenant1;
            if (tenantRepository.count() == 0) {
                tenant1 = tenantRepository.save(new Tenant("ABC Corporation", "abc"));
                tenantRepository.save(new Tenant("XYZ Enterprises", "xyz"));
                System.out.println("✓ Tenants created");
            } else {
                tenant1 = tenantRepository.findAll().get(0);
                System.out.println("✓ Using existing tenant: " + tenant1.getName());
            }
            if (tenant1 == null) {
                System.err.println("✗ Tenant1 is null, skipping sample data load");
                return;
            }

            // Load sample users (ADMIN, CASHIER, and USER)
            if (userRepository.count() == 0) {
                System.out.println("Loading sample users...");
                try {
                    // Create ADMIN user
                    User adminUser = new User(tenant1.getId(), "admin1", "admin@example.com", passwordEncoder.encode("admin123"));
                    Set<Role> adminRoles = new HashSet<>();
                    adminRoles.add(new Role("ADMIN"));
                    adminUser.setRoles(adminRoles);
                    adminUser.setEnabled(true);
                    userRepository.save(adminUser);
                    System.out.println("✓ Sample ADMIN user created: username=admin1, password=admin123");

                    // Create CASHIER user
                    User cashierUser = new User(tenant1.getId(), "cashier", "cashier@example.com", passwordEncoder.encode("cashier123"));
                    Set<Role> cashierRoles = new HashSet<>();
                    cashierRoles.add(new Role("CASHIER"));
                    cashierUser.setRoles(cashierRoles);
                    cashierUser.setEnabled(true);
                    userRepository.save(cashierUser);
                    System.out.println("✓ Sample CASHIER user created: username=cashier, password=cashier123");

                    // Create USER user (read-only)
                    User regularUser = new User(tenant1.getId(), "viewer", "viewer@example.com", passwordEncoder.encode("viewer123"));
                    Set<Role> userRoles = new HashSet<>();
                    userRoles.add(new Role("USER"));
                    regularUser.setRoles(userRoles);
                    regularUser.setEnabled(true);
                    userRepository.save(regularUser);
                    System.out.println("✓ Sample USER user created: username=viewer, password=viewer123");
                } catch (Exception e) {
                    System.err.println("✗ Error creating sample users: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("✓ Users already exist, skipping user creation. Found: " + userRepository.count() + " users");
            }

            if (supplierRepository.count() == 0) {
                System.out.println("Loading sample suppliers...");
                try {
                    supplierRepository.save(
                            new Supplier(tenant1.getId(), "Supplier A", "supplierA@example.com", "0771234567", "Colombo"));
                    supplierRepository
                            .save(new Supplier(tenant1.getId(), "Supplier B", "supplierB@example.com", "0772345678", "Galle"));
                    supplierRepository
                            .save(new Supplier(tenant1.getId(), "Supplier C", "supplierC@example.com", "0773456789", "Kandy"));
                    System.out.println("✓ Sample suppliers created");
                } catch (Exception e) {
                    System.err.println("✗ Error creating suppliers: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (productRepository.count() == 0) {
                System.out.println("Loading sample products...");
                try {
                    // Add sample products if needed
                    productRepository.save(new Product(tenant1.getId(), "P001", "Product 1", "BATCH001", 100, 10.5f));
                    productRepository.save(new Product(tenant1.getId(), "P002", "Product 2", "BATCH002", 200, 20.0f));
                    productRepository.save(new Product(tenant1.getId(), "P003", "Product 3", "BATCH003", 150, 15.75f));
                    System.out.println("✓ Sample products created");
                } catch (Exception e) {
                    System.err.println("✗ Error creating products: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Add sample invoices
            if (invoiceRepository.count() == 0) {
                System.out.println("Loading sample invoices...");
                try {
                    Supplier supplier = supplierRepository.findAll().stream()
                            .filter(s -> tenant1.getId().equals(s.getTenantId()))
                            .findFirst()
                            .orElse(null);

                    if (supplier != null) {
                        invoiceRepository.save(
                                new Invoice(tenant1.getId(), "INV-2025-001", supplier.getId(), LocalDate.of(2025, 1, 15)));
                        invoiceRepository.save(
                                new Invoice(tenant1.getId(), "INV-2025-002", supplier.getId(), LocalDate.of(2025, 2, 10)));
                        invoiceRepository
                                .save(new Invoice(tenant1.getId(), "INV-2025-003", supplier.getId(), LocalDate.of(2025, 3, 5)));
                        System.out.println("✓ Sample invoices created");
                    }
                } catch (Exception e) {
                    System.err.println("✗ Error creating invoices: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Add sample product batches for each invoice
            if (productBatchRepository.count() == 0) {
                System.out.println("Loading sample product batches...");
                try {
                    // Batches for INV-2025-001
                    productBatchRepository.save(new ProductBatch(
                            tenant1.getId(), "P001", "INV-2025-001", "BATCH001", 50, 8.0f, 10.5f, LocalDate.of(2026, 1, 15)));
                    productBatchRepository.save(new ProductBatch(
                            tenant1.getId(), "P002", "INV-2025-001", "BATCH002", 100, 15.0f, 20.0f, LocalDate.of(2026, 2, 15)));

                    // Batches for INV-2025-002
                    productBatchRepository.save(new ProductBatch(
                            tenant1.getId(), "P001", "INV-2025-002", "BATCH004", 75, 8.5f, 10.5f, LocalDate.of(2026, 3, 10)));
                    productBatchRepository.save(new ProductBatch(
                            tenant1.getId(), "P003", "INV-2025-002", "BATCH003", 80, 12.0f, 15.75f, LocalDate.of(2026, 4, 10)));

                    // Batches for INV-2025-003
                    productBatchRepository.save(new ProductBatch(
                            tenant1.getId(), "P002", "INV-2025-003", "BATCH005", 120, 16.0f, 20.0f, LocalDate.of(2026, 5, 5)));
                    productBatchRepository.save(new ProductBatch(
                            tenant1.getId(), "P003", "INV-2025-003", "BATCH006", 60, 13.0f, 15.75f, LocalDate.of(2026, 6, 5)));
                    System.out.println("✓ Sample product batches created");
                } catch (Exception e) {
                    System.err.println("✗ Error creating product batches: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Add sample transactions
            if (transactionRepository.count() == 0) {
                System.out.println("Loading sample transactions...");
                try {
                    Transaction txn1 = transactionRepository.save(new Transaction(
                            tenant1.getId(), "TXN-001", "CASH", 315.0, 15.0, 300.0, 300.0, 0.0));
                    Transaction txn2 = transactionRepository.save(new Transaction(
                            tenant1.getId(), "TXN-002", "CREDIT_CARD", 525.0, 25.0, 500.0, 300.0, 200.0));
                    Transaction txn3 = transactionRepository.save(new Transaction(
                            tenant1.getId(), "TXN-003", "BANK_TRANSFER", 210.0, 10.0, 200.0, 200.0, 0.0));

                    // Add sample transaction items
                    if (transactionItemRepository.count() == 0) {
                        Product product1 = productRepository.findAll().stream()
                                .filter(p -> "P001".equals(p.getId()))
                                .findFirst()
                                .orElse(null);
                        Product product2 = productRepository.findAll().stream()
                                .filter(p -> "P002".equals(p.getId()))
                                .findFirst()
                                .orElse(null);
                        Product product3 = productRepository.findAll().stream()
                                .filter(p -> "P003".equals(p.getId()))
                                .findFirst()
                                .orElse(null);

                        if (product1 != null && product2 != null && product3 != null) {
                            // Transaction 1 items
                            transactionItemRepository.save(new TransactionItem(
                                    tenant1.getId(), "TXN-001", "P001", 10, 10.5));
                            transactionItemRepository.save(new TransactionItem(
                                    tenant1.getId(), "TXN-001", "P002", 10, 20.0));

                            // Transaction 2 items
                            transactionItemRepository.save(new TransactionItem(
                                    tenant1.getId(), "TXN-002", "P001", 20, 10.5));
                            transactionItemRepository.save(new TransactionItem(
                                    tenant1.getId(), "TXN-002", "P003", 20, 15.75));

                            // Transaction 3 items
                            transactionItemRepository.save(new TransactionItem(
                                    tenant1.getId(), "TXN-003", "P002", 10, 20.0));
                        }
                    }
                    System.out.println("✓ Sample transactions created");
                } catch (Exception e) {
                    System.err.println("✗ Error creating transactions: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("\n========== Sample Data Loading Complete ==========");
            System.out.println("Users: " + userRepository.count());
            System.out.println("Suppliers: " + supplierRepository.count());
            System.out.println("Products: " + productRepository.count());
            System.out.println("Invoices: " + invoiceRepository.count());
            System.out.println("Transactions: " + transactionRepository.count());
            System.out.println("==================================================\n");

        } catch (Exception e) {
            System.err.println("✗ Fatal error in SampleDataLoader: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
