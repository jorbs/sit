package com.github.jorbs.sit.domain;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue
	@Column
	private Integer id;

	@ManyToOne
	private Receipt receipt;

	@Column
	private String negotiation;

	@Column
	private String bs;

	@Column
	private String market;

	@Column
	private String description;

	@Column
	private String observation;

	@Column
	private Integer quantity;

	@Column
	private BigDecimal price;

	@Column
	private BigDecimal value;

	@Column
	private String dc;

	public Order() {
		this.quantity = 0;
		this.price = BigDecimal.ZERO;
		this.value = BigDecimal.ZERO;
	}

	public Order(Receipt receipt) {
		this.receipt = receipt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public String getNegotiation() {
		return negotiation;
	}

	public void setNegotiation(String negotiation) {
		this.negotiation = negotiation;
	}

	public String getBs() {
		return bs;
	}

	public void setBs(String bs) {
		this.bs = bs;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getDc() {
		return dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	public Boolean isBuy() {
		if (bs == null) {
			return null;
		}

		return "c".equals(bs.toLowerCase());
	}

	public BigDecimal getAveragePrice() {
		Integer ordersCount = this.receipt.getOrders().size();
		BigDecimal volume = this.price.multiply(new BigDecimal(this.quantity));
		BigDecimal relativeBrokerage = this.receipt.getBrokerage().divide(new BigDecimal(ordersCount), 2);
		BigDecimal relativeTaxes = this.receipt.getTotalTaxes().divide(new BigDecimal(ordersCount), 2);
		BigDecimal averagePrice = null;

		if (this.isBuy()) {
			averagePrice = volume.add(relativeBrokerage).add(relativeTaxes);
		} else {
			averagePrice = volume.subtract(relativeBrokerage).subtract(relativeTaxes);
		}

		return averagePrice.divide(new BigDecimal(this.quantity), 2);
	}

	public String getStockSymbol() {
		Pattern pattern = Pattern.compile("(\\w+)\\s+(PN|ON|UNT)");
		Matcher matcher = pattern.matcher(this.description);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return this.description;
	}

	public String toString() {
		String ret = "";

		ret += "Negotiation: " + this.negotiation + "\n";
		ret += "BS: " + this.bs + "\n";
		ret += "Market: " + this.market + "\n";
		ret += "Description: " + this.description + "\n";
		ret += "Observation: " + this.observation + "\n";
		ret += "Quantity: " + this.quantity + "\n";
		ret += "Price: " + this.price + "\n";
		ret += "Value: " + this.value + "\n";
		ret += "DC: " + this.dc + "\n";

		return ret;
	}

}
