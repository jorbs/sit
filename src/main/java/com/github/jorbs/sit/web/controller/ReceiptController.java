package com.github.jorbs.sit.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.jorbs.sit.domain.Receipt;
import com.github.jorbs.sit.repository.ReceiptRepository;

@Controller
@RequestMapping("/receipt")
public class ReceiptController {

	@Autowired
	private ReceiptRepository receiptRepository;
	
	@RequestMapping("/list")
	public String list(ModelMap modelMap) {
		List<Receipt> receipts = (List<Receipt>) receiptRepository.findAll();
		
		modelMap.addAttribute("receipts", receipts);
		
		return "receipt/list";
	}
}
