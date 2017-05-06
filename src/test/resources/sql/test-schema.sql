create table brokers (
	id integer primary key,
	type varchar(255) not null,
	name varchar(255) not null
);

create table receipts (
	id integer primary key,
	brokerage numeric(19,2),
	buy_amount numeric(19,2),
	created_at timestamp,
	emoluments numeric(19,2),
	irrf numeric(19,2),
	iss numeric(19,2),
	issued_at timestamp,
	liquidation_tax numeric(19,2),
	number varchar(255),
	others numeric(19,2),
	registry_tax numeric(19,2),
	sell_amount numeric(19,2),
	tax_total numeric(19,2),
	broker_id integer not null
);

create table orders (
	id integer primary key,
	bs varchar(255),
	dc varchar(255),
	description varchar(255),
	market varchar(255),
	negotiation varchar(255),
	observation varchar(255),
	price numeric(19,2),
	quantity integer,
	value numeric(19,2),
	receipt_id integer not null
);

create table receipts_orders (
	receipt_id integer not null,
	order_id integer not null
);