package com.github.jorbs.sit.domain;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Rico")
public class RicoBroker extends Broker {

	public RicoBroker() {}
	
	public RicoBroker(String[] receiptLines) {
		super(receiptLines);
	}

	@Override
	public Receipt readReceipt() throws Exception {
		Receipt receipt = new Receipt(this);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Pattern pattern = Pattern.compile("(\\d+)$");
		Matcher matcher = pattern.matcher(receiptLines[1]);
		
		if (matcher.find()) {
			receipt.setNumber(matcher.group(1));
		} else {
			throw new Exception("Unable to read receipt number");
		}
		
		pattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})$");
		matcher = pattern.matcher(receiptLines[2]);
		
		if (matcher.find()) {
			receipt.setIssuedAt(sdf.parse(matcher.group(1)));
		} else {
			throw new Exception("Unable to read receipt issue date");
		}
		
		this.readOrders(receipt);
		
		return receipt;
	}

	@Override
	protected void readOrders(Receipt receipt) throws Exception {
		String orderRegex = "^(1-BOVESPA)\\s+([CV])\\s+(VISTA|FRACIONARIO)\\s+([\\w\\s]+)\\s+(.{0,3})\\s+(\\d+)\\s+(\\d*,?\\d{2})\\s+(\\d*,?\\d{2})\\s+([DC])$";
		Pattern orderPattern = Pattern.compile(orderRegex);
		List<Order> orders = new ArrayList<Order>();
		
		for (int i = 22; i < receiptLines.length; i++) {
			if (receiptLines[i].contains("1-BOVESPA")) {
				String orderLine = receiptLines[i].replace(".", "");
				Order order = new Order(receipt);
				Matcher matcher = orderPattern.matcher(orderLine);
				
				while (matcher.find()) {
					order.setNegotiation(matcher.group(1));
					order.setBs(matcher.group(2));
					order.setMarket(matcher.group(3));
					order.setDescription(matcher.group(4));
					order.setObservation(matcher.group(5));
					order.setQuantity(Integer.parseInt(matcher.group(6)));
					order.setPrice(new BigDecimal(matcher.group(7).replace(",", ".")));
					order.setValue(new BigDecimal(matcher.group(8).replace(",", ".")));
					order.setDc(matcher.group(9));
				}
				
				orders.add(order);
			} else {
				String regex = null;
				String methodName = null;
				
				if (receiptLines[i].contains("Vendas à Vista")) {
					regex = "(\\d+,\\d{2})$";
					methodName = "setSellAmount";
				} else if (receiptLines[i].contains("Compras à Vista")) {
					regex = "(\\d+,\\d{2})$";
					methodName = "setBuyAmount";
				} else if (receiptLines[i].contains("Taxa de Liquidação")) {
					regex = "(\\d+,\\d{2})";
					methodName = "setLiquidationTax";
				} else if (receiptLines[i].contains("Taxa de Registro")) {
					regex = "(\\d+,\\d{2})";
					methodName = "setRegistryTax";
				} else if (receiptLines[i].contains("Emolumentos")) {
					regex = "(\\d+,\\d{2})";
					methodName = "setEmoluments";
				} else if (receiptLines[i].contains("Corretagem ")) {
					regex = "(\\d+,\\d{2})";
					methodName = "setBrokerage";
				} else if (receiptLines[i].contains("ISS")) {
					regex = "(\\d+,\\d{2})";
					methodName = "setIss";
				} else if (receiptLines[i].contains("I.R.R.F")) {
					regex = "(\\d+,\\d{2})$";
					methodName = "setIrrf";
				} else if (receiptLines[i].contains("Outras")) {
					regex = "(\\d+,\\d{2})";
					methodName = "setOthers";
				}
				
				if (regex != null) {
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(receiptLines[i].replace(".", ""));
					
					if (matcher.find()) {
						Method method = Receipt.class.getMethod(methodName, BigDecimal.class);
						method.invoke(receipt, new BigDecimal(matcher.group(1).replace(",", ".")));
					}
				}
			}
		}
		
		receipt.setOrders(orders);
	}

}
