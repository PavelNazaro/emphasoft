package com.pavelnazaro.emphasoft.repositories;

import com.pavelnazaro.emphasoft.entities.RequestRating;
import com.pavelnazaro.emphasoft.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("select distinct userId from Request where money > ?1")
    List<Long> findUserIdByMoneyIsGreaterThan(Double money);

    @Query("select distinct userId from Request group by userId having sum(money) > ?1")
    List<Long> findUserIdBySumMoneyIsGreaterThan(Double money);

    @Query(value = "select count(currency_base) as count, currency_base as currencyBase, currency_need as currencyNeed from requests group by currency_base, currency_need order by count DESC", nativeQuery = true)
    List<RequestRating> findBy();
}