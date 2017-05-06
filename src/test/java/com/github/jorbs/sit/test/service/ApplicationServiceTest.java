package com.github.jorbs.sit.test.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.jorbs.sit.domain.CaixaBroker;
import com.github.jorbs.sit.domain.Receipt;
import com.github.jorbs.sit.repository.ReceiptRepository;
import com.github.jorbs.sit.service.ApplicationService;
import com.github.jorbs.sit.test.AbstractIntegrationTest;

public class ApplicationServiceTest extends AbstractIntegrationTest {

	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private ReceiptRepository receiptRepository;
	
	@Test
	public void testReceiptImport() {
		String receiptFilepath = "src/test/resources/receipts/04-04-2017.pdf";
		InputStream is = null;
		
		try {
			File receiptFile = new File(receiptFilepath);
			
			is = new FileInputStream(receiptFile);
			byte[] receiptBytes = IOUtils.toByteArray(is);
			Receipt receipt = applicationService.importReceipt(receiptBytes, false);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			assertEquals(receipt.getBroker().getClass(), CaixaBroker.class);
			assertEquals("447249", receipt.getNumber());
			assertEquals("04/04/2017", sdf.format(receipt.getIssuedAt()));
			assertEquals(3, receipt.getOrders().size());
			assertEquals(new BigDecimal(4785.00).setScale(2, BigDecimal.ROUND_DOWN), receipt.getBuyAmount());
			assertEquals(new BigDecimal(3500.00).setScale(2, BigDecimal.ROUND_DOWN), receipt.getSellAmount());
			assertEquals(new BigDecimal(1.75), receipt.getLiquidationTax());
			assertEquals(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_DOWN), receipt.getRegistryTax());
			assertEquals(new BigDecimal(0.40).setScale(2, BigDecimal.ROUND_DOWN), receipt.getEmoluments());
			assertEquals(new BigDecimal(16.57).setScale(2, BigDecimal.ROUND_DOWN), receipt.getBrokerage());
			assertEquals(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_DOWN), receipt.getIss());
			assertEquals(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_DOWN), receipt.getIrrf());
			assertEquals(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_DOWN), receipt.getOthers());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
	
//	@Test
	public void testCalculateStockPosition() {
		String receiptDir = "classpath:receipts/";
		Collection<File> receiptFiles = FileUtils.listFiles(new File(receiptDir), new String[]{ "pdf" }, false);
		
		for (File receiptFile : receiptFiles) {
			InputStream is = null;
			
			try {
				is = new FileInputStream(receiptFile);
				byte[] fileBytes = IOUtils.toByteArray(is);
				
				applicationService.importReceipt(fileBytes, true);
				
				Receipt receipt = receiptRepository.findOne(1);
				
				assertEquals("1", receipt.getNumber());
				assertEquals(1, receiptRepository.count());
			} catch (Exception e) {
				IOUtils.closeQuietly(is);
				e.printStackTrace();
			}
		}
		
	}
}
