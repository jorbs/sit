package com.github.jorbs.sit.domain;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("XP")
public class XpBroker extends Broker {

	public XpBroker() {}
	
	public XpBroker(String[] receiptLines) {
		super(receiptLines);
	}

	@Override
	public Receipt readReceipt() throws Exception {
		Receipt receipt = new Receipt(this);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Pattern pattern = Pattern.compile("(\\d+)(\\d{2}/\\d{2}/\\d{4})$");
		Matcher matcher = pattern.matcher(receiptLines[11]);
		
		while (matcher.find()) {
			try {
				receipt.setNumber(matcher.group(1));
				receipt.setIssuedAt(sdf.parse(matcher.group(2)));
			} catch (ParseException e) {
				throw new Exception("Unable to parse receipt issue date.");
			}
		}
		
		this.readOrders(receipt);
		
		return receipt;
	}

	@Override
	protected void readOrders(Receipt receipt) throws Exception {
		String orderRegex = "(.*)\\s+.*(1-BOVESPA)\\s+([CV])\\s+(VIS)\\s+(D?)#?\\s*(\\d+)\\s+(\\d*,?\\d{2})\\s+(\\d*,?\\d{2})\\s+([DC])$";
		Pattern orderPattern = Pattern.compile(orderRegex);
		List<Order> orders = new ArrayList<Order>();
		
		for (int i = 12; i < receiptLines.length; i++) {
			if (receiptLines[i].contains("1-BOVESPA")) {
				String orderLine = receiptLines[i].replace(".", "");
				Order order = new Order(receipt);
				Matcher matcher = orderPattern.matcher(orderLine);
				
				while (matcher.find()) {
					order.setNegotiation(matcher.group(2));
					order.setBs(matcher.group(3));
					order.setMarket(matcher.group(4));
					order.setObservation(matcher.group(5));
					order.setDc(matcher.group(5)); // FIXME
					order.setDescription(matcher.group(1));
					order.setQuantity(Integer.parseInt(matcher.group(6)));
					order.setPrice(new BigDecimal(matcher.group(7).replace(",", ".")));
					order.setValue(new BigDecimal(matcher.group(8).replace(",", ".")));
					order.setDc(matcher.group(9));
				}
				
				orders.add(order);
				continue;
			}
			
			if (receiptLines[i].contains("Total Bovespa / Soma")) {
				i += 2;
				receipt.setSellAmount(new BigDecimal(receiptLines[i++].replace(".", "").replace(",", ".")));
				receipt.setBuyAmount(new BigDecimal(receiptLines[i].replace(".", "").replace(",", ".")));
				i += 7;
				
				Pattern pricePattern = Pattern.compile("(\\d*,\\d{2})\\s[CD]");
				Matcher priceMatcher = pricePattern.matcher(receiptLines[i++].replace(".", ""));
				
				while (priceMatcher.find()) {
					BigDecimal liquidationTax = new BigDecimal(priceMatcher.group(1).replace(",", "."));
					receipt.setLiquidationTax(liquidationTax);
				}
				
				priceMatcher = pricePattern.matcher(receiptLines[i].replace(".", ""));
				
				while (priceMatcher.find()) {
					BigDecimal registryTax = new BigDecimal(priceMatcher.group(1).replace(",", "."));
					receipt.setRegistryTax(registryTax);
				}
				
				i += 3;
				receipt.setEmoluments(new BigDecimal(receiptLines[i].replace(",", ".")));
				
				i += 19;
				priceMatcher = pricePattern.matcher(receiptLines[i++].replace(".", ""));
				
				while (priceMatcher.find()) {
					BigDecimal brokerage = new BigDecimal(priceMatcher.group(1).replace(",", "."));
					receipt.setBrokerage(brokerage);
				}
				
				priceMatcher = pricePattern.matcher(receiptLines[i++].replace(".", ""));
				
				while (priceMatcher.find()) {
					BigDecimal iss = new BigDecimal(priceMatcher.group(1).replace(",", "."));
					receipt.setIss(iss);
				}
				
				receipt.setIrrf(new BigDecimal(receiptLines[i].replace(".", "").replace(",", ".")));
				i += 3;
				
				priceMatcher = pricePattern.matcher(receiptLines[i].replace(".", ""));
				
				while (priceMatcher.find()) {
					BigDecimal others = new BigDecimal(priceMatcher.group(1).replace(",", "."));
					receipt.setOthers(others);
				}
			}
		}
		
		receipt.setOrders(orders);
	}

}
