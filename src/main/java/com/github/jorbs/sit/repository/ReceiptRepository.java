package com.github.jorbs.sit.repository;

import org.springframework.data.repository.CrudRepository;

import com.github.jorbs.sit.domain.Receipt;

public interface ReceiptRepository extends CrudRepository<Receipt, Integer> {

	public Receipt findByNumber(final String number);

}
