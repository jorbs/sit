package com.github.jorbs.sit.repository;

import org.springframework.data.repository.CrudRepository;

import com.github.jorbs.sit.domain.Order;

public interface OrderRepository extends CrudRepository<Order, Integer> {
}
