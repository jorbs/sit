package com.github.jorbs.sit.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.jorbs.sit.domain.CaixaBroker;
import com.github.jorbs.sit.domain.Order;
import com.github.jorbs.sit.domain.Receipt;
import com.github.jorbs.sit.repository.ReceiptRepository;
import com.github.jorbs.sit.service.ApplicationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationServiceTest {

	@Autowired
	private ReceiptRepository receiptRepository;
	
	@Autowired
	private ApplicationService applicationService;
	
	@Test
	public void testReceiptImport() {
		String receiptFilepath = "src/test/data/receipts/04-04-2017.pdf";
		InputStream is = null;
		
		try {
			File receiptFile = new File(receiptFilepath);
			is = new FileInputStream(receiptFile);
			byte[] receiptBytes = IOUtils.toByteArray(is);
			Receipt receipt = applicationService.importReceipt(receiptBytes, false);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			assertTrue(receipt.getBroker() instanceof CaixaBroker);
			assertEquals("447249", receipt.getNumber());
			assertEquals("04/04/2017", sdf.format(receipt.getIssuedAt()));
			assertEquals(3, receipt.getOrders().size());
			assertEquals(new BigDecimal("4785.00"), receipt.getBuyAmount());
			assertEquals(new BigDecimal("3500.00"), receipt.getSellAmount());
			assertEquals(new BigDecimal("1.75"), receipt.getLiquidationTax());
			assertEquals(new BigDecimal("0.00"), receipt.getRegistryTax());
			assertEquals(new BigDecimal("0.40"), receipt.getEmoluments());
			assertEquals(new BigDecimal("16.57"), receipt.getBrokerage());
			assertEquals(new BigDecimal("0.00"), receipt.getIss());
			assertEquals(new BigDecimal("0.00"), receipt.getIrrf());
			assertEquals(new BigDecimal("0.00"), receipt.getOthers());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
	
	@Test
	public void testOrdersImport() {
		String receiptFilepath = "src/test/data/receipts/04-04-2017.pdf";
		InputStream is = null;
		
		try {
			File receiptFile = new File(receiptFilepath);
			is = new FileInputStream(receiptFile);
			byte[] receiptBytes = IOUtils.toByteArray(is);
			Receipt receipt = applicationService.importReceipt(receiptBytes, false);
			Order firstOrder = receipt.getOrders().get(0);
			
			assertEquals("1-BOVESPA", firstOrder.getNegotiation());
			assertEquals("C", firstOrder.getBs());
			assertEquals("VISTA", firstOrder.getMarket());
			assertEquals("RNEW3  ON N2", firstOrder.getDescription());
			assertEquals("RNEW3", firstOrder.getStockSymbol());
			assertEquals("HD", firstOrder.getObservation());
			assertEquals(new Integer(1000), firstOrder.getQuantity());
			assertEquals(new BigDecimal("3.41"), firstOrder.getPrice());
			assertEquals(new BigDecimal("3410.00"), firstOrder.getValue());
			assertEquals("D", firstOrder.getDc());
			
			Order secondOrder = receipt.getOrders().get(1);
			
			assertEquals("1-BOVESPA", secondOrder.getNegotiation());
			assertEquals("V", secondOrder.getBs());
			assertEquals("VISTA", secondOrder.getMarket());
			assertEquals("RNEW3  ON N2", secondOrder.getDescription());
			assertEquals("RNEW3", secondOrder.getStockSymbol());
			assertEquals("HD", secondOrder.getObservation());
			assertEquals(new Integer(1000), secondOrder.getQuantity());
			assertEquals(new BigDecimal("3.50"), secondOrder.getPrice());
			assertEquals(new BigDecimal("3500.00"), secondOrder.getValue());
			assertEquals("C", secondOrder.getDc());
			
			Order thirdOrder = receipt.getOrders().get(2);
			
			assertEquals("1-BOVESPA", thirdOrder.getNegotiation());
			assertEquals("C", thirdOrder.getBs());
			assertEquals("VISTA", thirdOrder.getMarket());
			assertEquals("TIET11  UNT N2", thirdOrder.getDescription());
			assertEquals("TIET11", thirdOrder.getStockSymbol());
			assertEquals("H", thirdOrder.getObservation());
			assertEquals(new Integer(100), thirdOrder.getQuantity());
			assertEquals(new BigDecimal("13.75"), thirdOrder.getPrice());
			assertEquals(new BigDecimal("1375.00"), thirdOrder.getValue());
			assertEquals("D", thirdOrder.getDc());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
	
	@Test
	public void testCalculateStockPosition() {
		String receiptDir = "src/test/data/receipts/";
		Collection<File> receiptFiles = FileUtils.listFiles(new File(receiptDir), new String[]{ "pdf" }, false);
		
		for (File receiptFile : receiptFiles) {
			InputStream is = null;
			
			try {
				is = new FileInputStream(receiptFile);
				byte[] fileBytes = IOUtils.toByteArray(is);
				
				applicationService.importReceipt(fileBytes, true);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
		
		assertEquals(6, receiptRepository.count());
	}
}
