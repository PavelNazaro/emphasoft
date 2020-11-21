package com.pavelnazaro.emphasoft.controllers;

import com.google.gson.Gson;
import com.pavelnazaro.emphasoft.entities.Request;
import com.pavelnazaro.emphasoft.entities.RequestRating;
import com.pavelnazaro.emphasoft.services.RequestService;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RestController
@RequestMapping
@ApiOperation("Main controller")
public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class);

    private RequestService requestService;

    @Autowired
    public Controller(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/exchange")
    @ResponseBody
    @ApiOperation("Запрос: id пользователя, сумма в исходной валюте, исходная валюта, целевая валюта." +
            " Ответ: id запроса, сумма в целевой валюте.")
    public Map<String, String> exchange(@RequestParam(value = "user_id") long userId,
                                        @RequestParam double money,
                                        @RequestParam(value = "currency_base") String currencyBase,
                                        @RequestParam(value = "currency_need") String currencyNeed)
            throws JSONException {
        double course = getCourse(currencyBase, currencyNeed);
        double sum = money*course;
        long requestId = requestService.save(new Request(userId, money, currencyBase, currencyNeed, course, sum));

        Map<String, String> map = new LinkedHashMap<>();
        map.put("userId", "1");
        map.put("money", String.valueOf(money));
        map.put("currencyBase", currencyBase);
        map.put("currencyNeed", currencyNeed);
        map.put("course", String.valueOf(course));
        map.put("requestId", String.valueOf(requestId));
        map.put("sum", String.format("%.2f", sum));

        Gson gson = new Gson();
        logger.info(gson.toJson(map, LinkedHashMap.class));

        return map;
    }

    @GetMapping("/stats")
    @ResponseBody
    @ApiOperation("Предоставление доступа к выборочной информации по запросам. " +
            "Запросы: " +
            "1) Пользователи, запросившие конвертацию больше 10 000 $ за один запрос. " +
            "2) Пользователи, суммарный запрошенный объём которых больше 100 000 $. " +
            "3) Рейтинг направлений конвертации валют по популярности.")
    public String stats(){
        StringBuilder str = new StringBuilder();

        List<Long> requests;
        requests = requestService.getRequestsByMoneyIsGreaterThan(10000.00);
        str.append("Пользователи, запросившие конвертацию больше 10 000 $ за один запрос:").append(System.lineSeparator());
        requests.forEach(obj -> str.append(obj).append(System.lineSeparator()));
        str.append(System.lineSeparator());

        requests = requestService.findUserIdWithTotalSumMore(100000.00);
        str.append("Пользователи, суммарный запрошенный объём которых больше 100 000 $:").append(System.lineSeparator());
        requests.forEach(obj -> str.append(obj).append(System.lineSeparator()));
        str.append(System.lineSeparator());

        List<RequestRating> requestsRating = requestService.getRating();
        str.append("Рейтинг направлений конвертации валют по популярности:").append(System.lineSeparator());
        requestsRating.forEach(requestRating -> str.append("Count: ").append(requestRating.getCount()).append("; Currency base: ").append(requestRating.getCurrencyBase()).append("; Currency need: ").append(requestRating.getCurrencyNeed()).append(System.lineSeparator()));

        return str.toString();
    }

    public double getCourse(String currencyBase, String currencyNeed) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        String s = restTemplate.getForObject("https://api.exchangeratesapi.io/latest?symbols=" + currencyNeed + "&base=" + currencyBase, String.class);

        return new JSONObject(s).getJSONObject("rates").getDouble(currencyNeed);
    }
}
