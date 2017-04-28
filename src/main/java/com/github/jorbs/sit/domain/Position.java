package com.github.jorbs.sit.domain;

import java.math.BigDecimal;

public class Position {

	private String stock;

	private Integer quantity;

	private BigDecimal averagePrice;

	private BigDecimal balance;
	
	private Position invertedPosition;

	public Position(Order order) {
		this.stock = order.getStockSymbol();
		this.quantity = order.getQuantity();
		this.averagePrice = order.getAveragePrice();
		this.balance = BigDecimal.ZERO;
		this.invertedPosition = null;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(BigDecimal averagePrice) {
		this.averagePrice = averagePrice;
	}

	public BigDecimal getPositionValue() {
		return this.averagePrice.multiply(new BigDecimal(Math.abs(this.quantity)));
	}

	public BigDecimal getBalance() {
		BigDecimal balance = this.getPositionValue();
		BigDecimal invertedBalance = this.invertedPosition != null ? this.invertedPosition.getPositionValue() : BigDecimal.ZERO;
		
		return invertedBalance.subtract(balance);
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Position getInvertedPosition() {
		return invertedPosition;
	}

	public void setInvertedPosition(Position invertedPosition) {
		if (this.invertedPosition != invertedPosition) {
			this.invertedPosition = invertedPosition;
			invertedPosition.setInvertedPosition(this);
		}
	}
	
	public Integer openQuantity() {
		Integer invertedQuantity = this.invertedPosition != null ? this.invertedPosition.getQuantity() : 0;
		return this.quantity - invertedQuantity;
	}

	public void merge(Position position) {
		BigDecimal currentPositionValue = this.getPositionValue();

		this.quantity += position.getQuantity();

		if (position.getQuantity() < 0) { // sell position
			BigDecimal delta = position.getAveragePrice().subtract(this.averagePrice);
			this.balance = this.balance.add(delta.multiply(new BigDecimal(Math.abs(position.getQuantity()))));
		} else if (this.quantity != 0) { // buy position
			BigDecimal newPositionValue = currentPositionValue.add(position.getPositionValue());
			this.averagePrice = newPositionValue.divide(new BigDecimal(this.quantity), 2);
		}
	}

	@Override
	public String toString() {
		String ret = "";

		ret += this.stock + " qtd: " + this.quantity + ", Saldo: R$ " + this.balance;

		return ret;
	}
	
	@Override
	public boolean equals(Object source) {
		Position sourcePosition = (Position) source;
		return this.stock != null && this.stock.equals(sourcePosition.getStock());
	}
	
	@Override
	public int hashCode() {
		return this.stock.hashCode();
	}

}
