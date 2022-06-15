package ua.bala.project_reactor.service;

import ua.bala.project_reactor.domain.Revenue;
import ua.bala.project_reactor.util.CommonUtil;

public class RevenueService {

    public Revenue getRevenue(Long movieId){
        CommonUtil.delay(1000); // simulating a network call ( DB or Rest call)
        return Revenue.builder()
                .movieId(movieId)
                .budget(1000000)
                .boxOffice(5000000)
                .build();

    }
}
