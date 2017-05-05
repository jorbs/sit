package com.github.jorbs.sit.test.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.jorbs.sit.repository.ReceiptRepository;
import com.github.jorbs.sit.service.ApplicationService;
import com.github.jorbs.sit.test.AbstractIntegrationTest;

public class ApplicationServiceTest extends AbstractIntegrationTest {

	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private ReceiptRepository receiptRepository;
	
	@Test
	public void testCalculateStockPosition() {
		String filepaths[] = {
			"/Users/joaoroberto/Dropbox/Ações/Caixa/2017/Abril/04-04-2017.pdf",
			"/Users/joaoroberto/Dropbox/Ações/Caixa/2017/Abril/05-04-2017.pdf",
			"/Users/joaoroberto/Dropbox/Ações/Caixa/2017/Abril/12-04-2017.pdf",
			"/Users/joaoroberto/Dropbox/Ações/Caixa/2017/Abril/19-04-2017.pdf",
			"/Users/joaoroberto/Dropbox/Ações/Caixa/2017/Abril/20-04-2017.pdf",
			"/Users/joaoroberto/Dropbox/Ações/Caixa/2017/Abril/27-04-2017.pdf"
		};
		
		for (String filepath : filepaths) {
			File file = new File(filepath);
			InputStream is = null;
			
			try {
				is = new FileInputStream(file);
				byte[] fileBytes = IOUtils.toByteArray(is);
				
				applicationService.saveReceipt(fileBytes);
				
				assertEquals(1, receiptRepository.count());
			} catch (Exception e) {
				IOUtils.closeQuietly(is);
				e.printStackTrace();
			}
			
		}
		
	}
}
