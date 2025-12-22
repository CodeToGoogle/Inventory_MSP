-- V6__replenishment_rules.sql

CREATE TABLE ReplenishmentRules (
    RuleID INT PRIMARY KEY AUTO_INCREMENT,
    ProductID INT NOT NULL,
    LocationID INT,
    LowThreshold DECIMAL(15,3),
    HighThreshold DECIMAL(15,3),
    OrderType ENUM('PO','MO') NOT NULL,
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
