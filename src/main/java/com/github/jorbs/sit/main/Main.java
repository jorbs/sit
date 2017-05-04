package com.github.jorbs.sit.main;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.github.jorbs.sit.domain.Broker;
import com.github.jorbs.sit.domain.Receipt;

public class Main {

	public static void main(String[] args) {
		try {
			Collection<File> files = FileUtils.listFiles(new File(args[0]), new String[]{ "pdf" }, false);
			
			for (File file : files) {
				PDDocument document = PDDocument.load(file);
				PDFTextStripper textStripper = new PDFTextStripper();
	
				textStripper.setStartPage(1);
				textStripper.setEndPage(document.getNumberOfPages());
				
				String receiptLines[] = textStripper.getText(document).split("\n");
				Broker broker = Broker.getBroker(receiptLines);
				Receipt receipt = broker.readReceipt();
				
				System.out.println(receipt.toString());
	//			System.out.println(textStripper.getText(document));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
