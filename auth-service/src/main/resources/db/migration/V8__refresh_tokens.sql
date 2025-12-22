-- V8__refresh_tokens.sql
CREATE TABLE RefreshTokens (
    TokenID BIGINT PRIMARY KEY AUTO_INCREMENT,
    Token VARCHAR(512) NOT NULL UNIQUE,
    UserID INT NOT NULL,
    ExpiryDate DATETIME NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Revoked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

CREATE INDEX idx_refresh_user ON RefreshTokens(UserID);
CREATE INDEX idx_refresh_token ON RefreshTokens(Token);
