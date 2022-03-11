package com.ippon.gateway.repository;

import com.ippon.gateway.domain.ApiKey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the ApiKey entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApiKeyRepository extends R2dbcRepository<ApiKey, Long>, ApiKeyRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<ApiKey> findAll();

    @Override
    Mono<ApiKey> findById(Long id);

    @Override
    <S extends ApiKey> Mono<S> save(S entity);
}

interface ApiKeyRepositoryInternal {
    <S extends ApiKey> Mono<S> insert(S entity);
    <S extends ApiKey> Mono<S> save(S entity);
    Mono<Integer> update(ApiKey entity);

    Flux<ApiKey> findAll();
    Mono<ApiKey> findById(Long id);
    Flux<ApiKey> findAllBy(Pageable pageable);
    Flux<ApiKey> findAllBy(Pageable pageable, Criteria criteria);
}
