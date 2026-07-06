-- ============================================================
-- TechMart Database — Full Schema + Seed Data (High Inventory)
-- Database: techmart
-- Target: MySQL 8.x (InnoDB)
-- Note: inventory quantities boosted for load testing so stock
-- depletion doesn't confound throughput/latency measurements.
-- ============================================================

CREATE DATABASE IF NOT EXISTS techmart
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE techmart;

SET FOREIGN_KEY_CHECKS = 0;

-- Drop existing tables so this script can be re-run safely
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS warehouses;
DROP TABLE IF EXISTS products;

-- --------------------------------------------------------
-- Table: products
-- --------------------------------------------------------
CREATE TABLE products (
                          id INT AUTO_INCREMENT NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          price DOUBLE NOT NULL,
                          stock INT NOT NULL DEFAULT 0,
                          PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Table: warehouses
-- --------------------------------------------------------
CREATE TABLE warehouses (
                            id INT AUTO_INCREMENT NOT NULL,
                            name VARCHAR(255) NOT NULL,
                            location VARCHAR(255),
                            PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Table: customers
-- --------------------------------------------------------
CREATE TABLE customers (
                           id INT AUTO_INCREMENT NOT NULL,
                           name VARCHAR(255) NOT NULL,
                           email VARCHAR(255),
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Table: orders
-- --------------------------------------------------------
CREATE TABLE orders (
                        id INT AUTO_INCREMENT NOT NULL,
                        customer_id INT NULL,
                        totalamount DOUBLE,
                        status VARCHAR(20) DEFAULT 'PENDING',
                        order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (id),
                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Table: order_items
-- --------------------------------------------------------
CREATE TABLE order_items (
                             id INT AUTO_INCREMENT NOT NULL,
                             price DOUBLE,
                             quantity INT,
                             order_id INT,
                             product_id INT,
                             PRIMARY KEY (id),
                             CONSTRAINT fk_order_items_order_id
                                 FOREIGN KEY (order_id) REFERENCES orders(id),
                             CONSTRAINT fk_order_items_product_id
                                 FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Table: inventory
-- --------------------------------------------------------
CREATE TABLE inventory (
                           id INT AUTO_INCREMENT NOT NULL,
                           productid INT,
                           warehouseid INT,
                           quantity INT,
                           PRIMARY KEY (id),
                           CONSTRAINT fk_inventory_product
                               FOREIGN KEY (productid) REFERENCES products(id),
                           CONSTRAINT fk_inventory_warehouse
                               FOREIGN KEY (warehouseid) REFERENCES warehouses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Table: notifications
-- --------------------------------------------------------
CREATE TABLE notifications (
                               id INT AUTO_INCREMENT NOT NULL,
                               order_id INT,
                               message VARCHAR(500),
                               status VARCHAR(20),
                               sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (id),
                               CONSTRAINT fk_notifications_order
                                   FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Indexes (performance / NFR support)
-- --------------------------------------------------------
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_inventory_productid ON inventory(productid);
CREATE INDEX idx_inventory_warehouseid ON inventory(warehouseid);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_notifications_order_id ON notifications(order_id);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- SEED DATA
-- ============================================================

-- Warehouses (Sri Lanka based)
INSERT INTO warehouses (id, name, location) VALUES
                                                (1, 'Colombo Central Warehouse', 'Colombo, Western Province'),
                                                (2, 'Kandy Regional Warehouse', 'Kandy, Central Province'),
                                                (3, 'Galle Regional Warehouse', 'Galle, Southern Province');

-- Products (stock column bumped to match the boosted inventory totals below)
INSERT INTO products (id, name, price, stock) VALUES
                                                  (1, 'Dell XPS 13 Laptop', 899.99, 12500),
                                                  (2, 'Apple iPhone 15', 999.00, 20000),
                                                  (3, 'Samsung Galaxy S24', 849.50, 17500),
                                                  (4, 'Logitech MX Master 3S Mouse', 99.99, 60000),
                                                  (5, 'Sony WH-1000XM5 Headphones', 349.00, 30000),
                                                  (6, 'HP LaserJet Pro Printer', 249.99, 7500),
                                                  (7, 'Kingston 1TB NVMe SSD', 89.99, 100000),
                                                  (8, 'ASUS 27" 4K Monitor', 429.00, 15000),
                                                  (9, 'Apple MacBook Pro 14"', 1999.00, 6000),
                                                  (10, 'Anker PowerCore 20000mAh', 49.99, 75000),
                                                  (11, 'Mechanical Gaming Keyboard RGB', 74.99, 40000),
                                                  (12, 'Google Pixel 8', 699.00, 14000);

-- Customers
INSERT INTO customers (id, name, email, created_at) VALUES
                                                        (1, 'Nimal Perera', 'nimal.perera@example.com', '2026-05-01 09:15:00'),
                                                        (2, 'Kavindi Silva', 'kavindi.silva@example.com', '2026-05-03 14:22:00'),
                                                        (3, 'Ashan Fernando', 'ashan.fernando@example.com', '2026-05-10 11:05:00'),
                                                        (4, 'Dinusha Jayasuriya', 'dinusha.jayasuriya@example.com', '2026-05-15 16:40:00'),
                                                        (5, 'Tharindu Wickramasinghe', 'tharindu.w@example.com', '2026-06-02 08:50:00');

-- Inventory (stock spread across the three warehouses per product — boosted ~500x for load testing)
INSERT INTO inventory (productid, warehouseid, quantity) VALUES
                                                             (1, 1, 7500),  (1, 2, 3000), (1, 3, 2000),
                                                             (2, 1, 10000), (2, 2, 6000), (2, 3, 4000),
                                                             (3, 1, 9000),  (3, 2, 5000), (3, 3, 3500),
                                                             (4, 1, 30000), (4, 2, 17500),(4, 3, 12500),
                                                             (5, 1, 15000), (5, 2, 10000),(5, 3, 5000),
                                                             (6, 1, 4000),  (6, 2, 2000), (6, 3, 1500),
                                                             (7, 1, 50000), (7, 2, 30000),(7, 3, 20000),
                                                             (8, 1, 7500),  (8, 2, 4500), (8, 3, 3000),
                                                             (9, 1, 3000),  (9, 2, 2000), (9, 3, 1000),
                                                             (10, 1, 35000),(10, 2, 25000),(10, 3, 15000),
                                                             (11, 1, 20000),(11, 2, 12500),(11, 3, 7500),
                                                             (12, 1, 7000), (12, 2, 4000), (12, 3, 3000);

-- Orders (mixed statuses to demonstrate lifecycle)
INSERT INTO orders (id, customer_id, totalamount, status, order_date) VALUES
                                                                          (1, 1, 1799.98, 'CONFIRMED', '2026-06-10 10:05:00'),
                                                                          (2, 2, 349.00,  'CONFIRMED', '2026-06-12 13:47:00'),
                                                                          (3, 3, 174.98,  'PENDING',   '2026-06-18 09:22:00'),
                                                                          (4, 4, 2098.99, 'CONFIRMED', '2026-06-20 17:30:00'),
                                                                          (5, 5, 99.99,   'SHIPPED',   '2026-06-25 12:00:00');

-- Order Items
INSERT INTO order_items (price, quantity, order_id, product_id) VALUES
                                                                    (899.99, 2, 1, 1),   -- Order 1: 2x Dell XPS 13
                                                                    (349.00, 1, 2, 5),   -- Order 2: 1x Sony WH-1000XM5
                                                                    (74.99,  1, 3, 11),  -- Order 3: 1x Mechanical Keyboard
                                                                    (99.99,  1, 3, 4),   -- Order 3: 1x MX Master 3S
                                                                    (1999.00,1, 4, 9),   -- Order 4: 1x MacBook Pro 14"
                                                                    (99.99,  1, 4, 4),   -- Order 4: 1x MX Master 3S
                                                                    (99.99,  1, 5, 4);   -- Order 5: 1x MX Master 3S

-- Notifications (async confirmation log — no row for the still-PENDING order 3)
INSERT INTO notifications (order_id, message, status, sent_at) VALUES
                                                                   (1, 'SUCCESS: Confirmation notification sent to customer for Order ID: 1 (Amount: $1799.98)', 'SUCCESS', '2026-06-10 10:05:03'),
                                                                   (2, 'SUCCESS: Confirmation notification sent to customer for Order ID: 2 (Amount: $349.00)',  'SUCCESS', '2026-06-12 13:47:03'),
                                                                   (4, 'SUCCESS: Confirmation notification sent to customer for Order ID: 4 (Amount: $2098.99)', 'SUCCESS', '2026-06-20 17:30:03'),
                                                                   (5, 'SUCCESS: Confirmation notification sent to customer for Order ID: 5 (Amount: $99.99)',   'SUCCESS', '2026-06-25 12:00:03');

-- End of script