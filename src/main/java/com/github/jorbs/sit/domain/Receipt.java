package com.github.jorbs.sit.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table
public class Receipt {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	private Broker broker;

	@Column
	private String number;

	@Column(name = "issued_at")
	private Date issuedAt;

	@OneToMany(fetch = FetchType.EAGER)
	@Cascade(CascadeType.ALL)
	private List<Order> orders;

	@Column(name = "buy_amount")
	private BigDecimal buyAmount;

	@Column(name = "sell_amount")
	private BigDecimal sellAmount;

	@Column(name = "liquidation_tax")
	private BigDecimal liquidationTax;

	@Column(name = "registry_tax")
	private BigDecimal registryTax;

	@Column
	private BigDecimal emoluments;

	@Column
	private BigDecimal brokerage;

	@Column
	private BigDecimal iss;

	@Column
	private BigDecimal irrf;

	@Column
	private BigDecimal others;

	@Column(name = "tax_total")
	private BigDecimal taxTotal;

	@Column(name = "created_at")
	@CreationTimestamp
	private Timestamp createdAt;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Date getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(Date issuedAt) {
		this.issuedAt = issuedAt;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public BigDecimal getBuyAmount() {
		return buyAmount;
	}

	public void setBuyAmount(BigDecimal buyAmount) {
		this.buyAmount = buyAmount;
	}

	public BigDecimal getSellAmount() {
		return sellAmount;
	}

	public void setSellAmount(BigDecimal sellAmount) {
		this.sellAmount = sellAmount;
	}

	public BigDecimal getLiquidationTax() {
		return liquidationTax;
	}

	public void setLiquidationTax(BigDecimal liquidationTax) {
		this.liquidationTax = liquidationTax;
	}

	public BigDecimal getRegistryTax() {
		return registryTax;
	}

	public void setRegistryTax(BigDecimal registryTax) {
		this.registryTax = registryTax;
	}

	public BigDecimal getEmoluments() {
		return emoluments;
	}

	public void setEmoluments(BigDecimal emoluments) {
		this.emoluments = emoluments;
	}

	public BigDecimal getBrokerage() {
		return brokerage;
	}

	public void setBrokerage(BigDecimal brokerage) {
		this.brokerage = brokerage;
	}

	public BigDecimal getIss() {
		return iss;
	}

	public void setIss(BigDecimal iss) {
		this.iss = iss;
	}

	public BigDecimal getIrrf() {
		return irrf;
	}

	public void setIrrf(BigDecimal irrf) {
		this.irrf = irrf;
	}

	public BigDecimal getOthers() {
		return others;
	}

	public void setOthers(BigDecimal others) {
		this.others = others;
	}

	public BigDecimal getTaxTotal() {
		return taxTotal;
	}

	public void setTaxTotal(BigDecimal taxTotal) {
		this.taxTotal = taxTotal;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getTotalQuantity() {
		return this.orders.stream().mapToInt(order -> order.getQuantity()).sum();
	}

	public BigDecimal getTotalTaxes() {
		return liquidationTax.add(this.emoluments).add(this.iss).add(this.others);
	}

	public String toString() {
		String ret = "";

		ret += "Number: " + number + "\n";
		ret += "Issued at: " + issuedAt.toString() + "\n";
		ret += "\n[Orders]\n";

		for (Order order : orders) {
			ret += order.toString() + "\n";
		}

		ret += "Sell amount: " + sellAmount + "\n";
		ret += "Buy amount: " + buyAmount + "\n";
		ret += "Liquidation tax: " + liquidationTax + "\n";
		ret += "Registry tax: " + registryTax + "\n";
		ret += "Emoluments: " + emoluments + "\n";
		ret += "Brokerage: " + brokerage + "\n";
		ret += "ISS: " + iss + "\n";
		ret += "IRRF: " + irrf + "\n";
		ret += "Others: " + others + "\n";

		return ret;
	};
}
