-- V1__customers_and_sales_orders.sql
-- Customers, SalesOrders, SalesOrderLines

CREATE TABLE IF NOT EXISTS Customers (
    CustomerID INT PRIMARY KEY AUTO_INCREMENT,
    CustomerName VARCHAR(200) NOT NULL,
    CustomerCode VARCHAR(50) UNIQUE,
    CustomerType ENUM('Retail','Wholesale','Corporate','Government') DEFAULT 'Retail',
    ContactPerson VARCHAR(100),
    Email VARCHAR(100),
    Phone VARCHAR(20),
    Address TEXT,
    CreditLimit DECIMAL(15,2),
    PaymentTerms INT,
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS SalesOrders (
    OrderID INT PRIMARY KEY AUTO_INCREMENT,
    OrderNumber VARCHAR(50) UNIQUE NOT NULL,
    OrderDate DATE NOT NULL,
    CustomerID INT NOT NULL,
    TotalAmount DECIMAL(15,2) DEFAULT 0,
    TotalQty DECIMAL(15,3) DEFAULT 0,
    OrderStatus ENUM('Draft','Confirmed','In Process','Partially Shipped','Shipped','Invoiced','Completed','Cancelled') DEFAULT 'Draft',
    DeliveryInfo TEXT,
    DeliveryStatus ENUM('Pending','Scheduled','In Transit','Delivered','Returned') DEFAULT 'Pending',
    DeliveryDate DATE,
    CreatedBy INT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID)
);

CREATE TABLE IF NOT EXISTS SalesOrderLines (
    LineID INT PRIMARY KEY AUTO_INCREMENT,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    SaleUnitPrice DECIMAL(15,2) NOT NULL,
    OrderedQty DECIMAL(15,3) NOT NULL,
    UnitID INT NOT NULL,
    DeliveredQty DECIMAL(15,3) DEFAULT 0,
    LineTotal DECIMAL(15,2) GENERATED ALWAYS AS (SaleUnitPrice * OrderedQty) STORED,
    FOREIGN KEY (OrderID) REFERENCES SalesOrders(OrderID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES ProductMaster(ProductID),
    FOREIGN KEY (UnitID) REFERENCES ProductUnits(UnitID)
);
