CREATE SCHEMA IF NOT EXISTS Banking;
USE Banking;

CREATE TABLE IF NOT EXISTS User (
	id BIGINT IDENTITY NOT NULL,
	name VARCHAR NOT NULL UNIQUE,
	password VARCHAR NOT NULL,
	address VARCHAR NOT NULL,
	phone_number VARCHAR NOT NULL UNIQUE,
    primary_account_id UUID
);

CREATE TABLE IF NOT EXISTS Account (
	id UUID NOT NULL DEFAULT RANDOM_UUID() PRIMARY KEY,
	user_id BIGINT NOT NULL,
	balance DECIMAL(30, 3) NOT NULL DEFAULT 0,
	currency VARCHAR NOT NULL,
	FOREIGN KEY (user_id) REFERENCES User
);

CREATE TABLE IF NOT EXISTS Operation (
	id BIGINT IDENTITY NOT NULL,
	date TIMESTAMP NOT NULL,
	currency VARCHAR NOT NULL,
	sender_account_id UUID NOT NULL,
	receiver_account_id UUID NOT NULL,
	amount DECIMAL NOT NULL,
	sender_initial_balance DECIMAL NOT NULL,
	sender_resulting_balance DECIMAL NOT NULL,
	receiver_initial_balance DECIMAL NOT NULL,
	receiver_resulting_balance DECIMAL NOT NULL,
	FOREIGN KEY (sender_account_id) REFERENCES Account,
	FOREIGN KEY (receiver_account_id) REFERENCES Account
);