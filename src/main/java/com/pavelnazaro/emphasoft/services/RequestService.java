package com.pavelnazaro.emphasoft.services;

import com.pavelnazaro.emphasoft.entities.Request;
import com.pavelnazaro.emphasoft.entities.RequestRating;
import com.pavelnazaro.emphasoft.repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestService {
    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public long save(Request request){
        requestRepository.save(request);
        return request.getId();
    }

    public List<Long> getRequestsByMoneyIsGreaterThan(Double money){
        return requestRepository.findUserIdByMoneyIsGreaterThan(money);
    }

    public List<Long> findUserIdWithTotalSumMore(Double money){
        return requestRepository.findUserIdBySumMoneyIsGreaterThan(money);
    }

    public List<RequestRating> getRating(){
        return requestRepository.findBy();
    }
}