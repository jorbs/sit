package com.github.jorbs.sit.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.github.jorbs.sit.domain.Position;
import com.github.jorbs.sit.service.ApplicationService;

@Controller
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@RequestMapping({"/", "/index"})
 	public String index(
 			@RequestParam(required = false) String stock,
			Model model
	) {
		Set<Position> positions = applicationService.calculateStockPosition(stock);
		
		model.addAttribute("positions", positions);

		return "index";
	}

	@RequestMapping(value = "/uploadReceiptFiles", method = RequestMethod.POST)
	public String uploadReceiptFiles(@RequestParam("receiptFile") MultipartFile receiptFiles[], Model model) {
		List<String> errors = new ArrayList<>();
		
		for (int i = 0; i < receiptFiles.length; i++) {
			try {
				byte receiptFile[] = receiptFiles[i].getBytes();
				applicationService.saveReceipt(receiptFile);
			} catch (Exception e) {
				errors.add(receiptFiles[i].getName());
			}
		}
		
		model.addAttribute("errors", errors);

		return "redirect:/index";
	}

}
