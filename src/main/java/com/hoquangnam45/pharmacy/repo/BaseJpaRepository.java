package com.hoquangnam45.pharmacy.repo;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public class BaseJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> implements ICustomRepository<T>, JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    public BaseJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public BaseJpaRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }

    @Override
    public List<T> findAll(Specification<T> specification, int offset, int limit, Sort sort) {
        return getQuery(specification, sort).setFirstResult(offset).setMaxResults(limit).getResultList();
    }
}
