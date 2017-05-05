package com.github.jorbs.sit.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.jorbs.sit.domain.Broker;
import com.github.jorbs.sit.domain.Order;
import com.github.jorbs.sit.domain.Position;
import com.github.jorbs.sit.domain.Receipt;
import com.github.jorbs.sit.repository.ReceiptRepository;

@Service
public class ApplicationService {

	@Autowired
	private ReceiptRepository receiptRepository;

	public void saveReceipt(byte[] receiptFile) throws Exception {
		PDDocument document = null;

		try {
			document = PDDocument.load(receiptFile);
			PDFTextStripper textStripper = new PDFTextStripper();

			textStripper.setStartPage(1);
			textStripper.setEndPage(document.getNumberOfPages());

			String receiptLines[] = textStripper.getText(document).split("\n");
			
			this.importReceipt(receiptLines);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			IOUtils.closeQuietly(document);
		}
	}
	
	public void importReceipt(String receiptLines[]) throws Exception  {
		Broker broker = Broker.getBroker(receiptLines);
		Receipt receipt = broker.readReceipt();

		if (receiptRepository.findByNumber(receipt.getNumber()) != null) {
			throw new Exception("Nota de corretagem já importada.");
		}
	}

	public Set<Position> calculateStockPosition(String stockSymbol) {
		List<Receipt> receipts = (List<Receipt>) receiptRepository.findAll();
		Set<Position> buyPositions = new HashSet<>();
		Set<Position> sellPositions = new HashSet<>();

		for (Receipt receipt : receipts) {
			List<Order> orders = receipt.getOrders();

			if (stockSymbol != null) {
				orders.removeIf(order -> !order.getStockSymbol().equals(stockSymbol));
			}

			for (Order order : orders) {
				Position newPosition = new Position(order);
				Set<Position> positions;
				Set<Position> invertedPositions;
				
				if (order.isBuy()) {
					positions = buyPositions;
					invertedPositions = sellPositions;
				} else {
					positions = sellPositions;
					invertedPositions = buyPositions;
				}
				
				Optional<Position> position = positions.stream().filter(p ->
					p.getStock().equals(order.getStockSymbol())
				).findFirst();
				
				if (position.isPresent()) {
					if (position.get().getInvertedPosition() == null) {
						Optional<Position> invertedPosition = invertedPositions.stream().filter(p ->
							p.getStock().equals(order.getStockSymbol())
						).findFirst();
						
						if (invertedPosition.isPresent()) {
							position.get().setInvertedPosition(invertedPosition.get());
						}
					}
					
					position.get().merge(newPosition);
				} else {
					positions.add(newPosition);
				}
			}
		}
		
		buyPositions.addAll(sellPositions);
		
		return buyPositions;
	}
}
