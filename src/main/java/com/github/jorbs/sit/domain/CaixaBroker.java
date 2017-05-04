package com.github.jorbs.sit.domain;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaixaBroker extends Broker {

	public CaixaBroker(String[] receiptLines) {
		super(receiptLines);
		this.name = "Caixa Econômica Federal";
	}

	@Override
	public Receipt readReceipt() throws Exception {
		Receipt receipt = new Receipt();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Pattern pattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})$");
		Matcher matcher = pattern.matcher(receiptLines[2]);
		
		if (matcher.find()) {
			receipt.setIssuedAt(sdf.parse(matcher.group(1)));
		} else {
			throw new Exception("Unable to read receipt issue date");
		}
		
		pattern = Pattern.compile("(\\d+)");
		matcher = pattern.matcher(receiptLines[4]);
		
		if (matcher.find()) {
			receipt.setNumber(matcher.group(1));
		} else {
			throw new Exception("Unable to read receipt number");
		}
		
		pattern = Pattern.compile("(\\d+\\.\\d{2})");
		matcher = pattern.matcher(normalizePrice(receiptLines[6]));
		
		if (matcher.find()) {
			receipt.setSellAmount(new BigDecimal(matcher.group(1)));
		} else {
			throw new Exception("Unable to read total sell amount");
		}
		
		matcher = pattern.matcher(normalizePrice(receiptLines[7]));
		
		if (matcher.find()) {
			receipt.setBuyAmount(new BigDecimal(matcher.group(1)));
		} else {
			throw new Exception("Unable to read total buy amount");
		}
		
		matcher = pattern.matcher(normalizePrice(receiptLines[7]));
		
		if (matcher.find()) {
			receipt.setBuyAmount(new BigDecimal(matcher.group(1)));
		} else {
			throw new Exception("Unable to read total buy amount");
		}

		matcher = pattern.matcher(normalizePrice(receiptLines[34]));
		
		if (matcher.find()) {
			receipt.setLiquidationTax(new BigDecimal(matcher.group(1)));
		} else {
			throw new Exception("Unable to read liquidation tax value");
		}
		
		matcher = pattern.matcher(normalizePrice(receiptLines[35]));
		
		if (matcher.find()) {
			receipt.setRegistryTax(new BigDecimal(matcher.group(1)));
		} else {
			throw new Exception("Unable to read regitry tax value");
		}
		
		matcher = pattern.matcher(normalizePrice(receiptLines[41]));
		
		if (matcher.find()) {
			receipt.setEmoluments(new BigDecimal(matcher.group(1)));
		} else {
			throw new Exception("Unable to read emoluments value");
		}
		
		matcher = pattern.matcher(normalizePrice(receiptLines[44]));
		
		if (matcher.find()) {
			receipt.setBrokerage(new BigDecimal(matcher.group(1)));
		} else {
			throw new Exception("Unable to read brokerage value");
		}
		
		matcher = pattern.matcher(normalizePrice(receiptLines[46]));
		
		if (matcher.find()) {
			receipt.setOthers(new BigDecimal(matcher.group(1)));
		} else {
			throw new Exception("Unable to read Others value");
		}
		
		matcher = pattern.matcher(normalizePrice(receiptLines[47]));
		
		if (matcher.find()) {
			receipt.setTaxTotal(new BigDecimal(matcher.group(1)));
		} else {
			throw new Exception("Unable to read Tax Total value");
		}
		
		pattern = Pattern.compile("(\\d+\\.\\d{2})$");
		matcher = pattern.matcher(normalizePrice(receiptLines[45]));
		
		if (matcher.find()) {
			receipt.setIrrf(new BigDecimal(matcher.group(1)));
		} else {
			throw new Exception("Unable to read IRRF value");
		}
		
		this.readOrders(receipt);
		
		return receipt;
	}

	@Override
	protected void readOrders(Receipt receipt) throws Exception {
		String orderRegex = "^(1-BOVESPA)\\s+([CV])\\s+(VISTA|FRACIONARIO)\\s+([\\w\\s]+)\\s+(HD?)\\s+(\\d+)\\s+(\\d+\\.\\d{2})\\s+(\\d+\\.\\d{2})\\s+([DC])$";
		Pattern orderPattern = Pattern.compile(orderRegex);
		List<Order> orders = new ArrayList<Order>();
		
		for (int i = 60; i < receiptLines.length; i++) {
			if (receiptLines[i].contains("1-BOVESPA")) {
				Order order = new Order(receipt);
				String orderLine = normalizePrice(receiptLines[i]);
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
			}
		}
		
		receipt.setOrders(orders);
	}

}
