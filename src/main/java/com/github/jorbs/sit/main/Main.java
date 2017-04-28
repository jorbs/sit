package com.github.jorbs.sit.main;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.github.jorbs.sit.domain.Broker;
import com.github.jorbs.sit.domain.Order;
import com.github.jorbs.sit.domain.Position;
import com.github.jorbs.sit.domain.Receipt;

public class Main {

	public static void main(String[] args) {
		try {

			String[] extension = { "pdf" };

			Collection<File> files = FileUtils.listFiles(new File("."), extension, false);

			Set<Position> positions = new HashSet<>();
			System.out.println("Arquivos pdf encontrados: " + files);
			for (File file : files) {

				PDDocument document = PDDocument.load(file);
				PDFTextStripper textStripper = new PDFTextStripper();

				textStripper.setStartPage(1);
				textStripper.setEndPage(document.getNumberOfPages());

				// System.out.println(textStripper.getText(document));

				String receiptLines[] = textStripper.getText(document).split("\n");
				Broker broker = Broker.getBroker(receiptLines);
				Receipt receipt = broker.readReceipt();
				for (Order order : receipt.getOrders()) {
					Optional<Position> position = positions.stream().filter(p -> p.getStock().equals(order.getStockSymbol())).findFirst();
					Position newPosition = new Position(order);

					if (position.isPresent()) {
						position.get().merge(newPosition);
					} else {
						positions.add(newPosition);
					}
				}
			}
			System.out.println("Posições:");

			positions.stream().forEach(position -> System.out.println(position));

			BigDecimal total = BigDecimal.ZERO;
			for (Position p : positions) {
				if (p.getQuantity().equals(0)) // posição encerrada
					total = total.add(p.getBalance());
			}

			System.out.println("Lucro/Prejuízo (Sem impostos): " + total);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
