package com.hoquangnam45.pharmacy.repo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ICustomRepository<T> {
    List<T> findAll(Specification<T> specification, int offset, int limit, Sort sort);
}
