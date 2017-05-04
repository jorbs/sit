package com.github.jorbs.sit.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Broker {

	@Id
	@GeneratedValue
	protected Integer id;

	@Column
	protected String name;

	@Transient
	protected String[] receiptLines;

	public Broker(String[] receiptLines) {
		this.receiptLines = receiptLines;
		
		for (int i = 0; i < receiptLines.length; i++) {
			receiptLines[i] = receiptLines[i].trim();
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static Broker getBroker(String[] receiptLines) throws Exception {
		if (receiptLines.length == 0) {
			throw new Exception("Receipt is empty.");
		}

		if (receiptLines[1].contains("Conta XP")) {
			return new XpBroker(receiptLines);
		}
		
		if (receiptLines[3].contains("Rico")) {
			return new RicoBroker(receiptLines);
		}

		if (receiptLines[0].contains("NOTAS DE CORRETAGEM")) {
			return new CaixaBroker(receiptLines);
		}

		throw new Exception("No suitable broker has been found.");
	}

	public abstract Receipt readReceipt() throws Exception;

	protected abstract void readOrders(Receipt receipt) throws Exception;
	
	protected String normalizePrice(String price) {
		if (price != null) {
			return price.replace(".", "").replace(",", ".");
		}
		
		return null;
	}
}
