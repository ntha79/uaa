package com.hdmon.uaa.service;

import com.hdmon.uaa.domain.RegisterStatistics;
import com.hdmon.uaa.repository.RegisterStatisticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by UserName on 7/24/2018.
 */
@Service
@Transactional
public class RegisterStatisticsService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final RegisterStatisticsRepository registerStatisticsRepository;

    public RegisterStatisticsService(RegisterStatisticsRepository registerStatisticsRepository)
    {
        this.registerStatisticsRepository = registerStatisticsRepository;
    }

    //=========================================HDMON-START=========================================

    /**
     * Tăng số lượt bộ đếm thống kê.
     * Last update date: 24-07-2018
     * @return entity của bản ghi mới tạo.
     */
    public RegisterStatistics increaseStatistics_hd(String clientType) {
        Date dtDay = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dtDay);

        Integer currentDay = cal.get(Calendar.DAY_OF_MONTH);
        Integer currentMonth = cal.get(Calendar.MONTH);
        Integer currentYear = cal.get(Calendar.YEAR);

        log.debug("Request to save ChatGroupStatistics : {}-{}-{}", currentDay, currentMonth, currentYear);

        RegisterStatistics dbInfo = registerStatisticsRepository.findByCurrentDayAndCurrentMonthAndCurrentYear(currentDay, currentMonth, currentYear);
        if(dbInfo != null && dbInfo.getId() != null)
        {
            dbInfo.setCount(dbInfo.getCount() + 1);
            if(clientType.equals("ANDROID")) {
                Integer countAndroid = dbInfo.getCountAndroid() != null ? dbInfo.getCountAndroid() : 0;
                dbInfo.setCountAndroid(countAndroid + 1);
            }
            else if(clientType.equals("IOS")) {
                Integer countIos = dbInfo.getCountIos() != null ? dbInfo.getCountIos() : 0;
                dbInfo.setCountIos(countIos + 1);
            }
            else if(clientType.equals("WEB")) {
                Integer countWeb = dbInfo.getCountWeb() != null ? dbInfo.getCountWeb() : 0;
                dbInfo.setCountWeb(countWeb + 1);
            }

            return registerStatisticsRepository.save(dbInfo);
        }
        else
        {
            RegisterStatistics newInfo = new RegisterStatistics();
            Integer countAndroid = 0;
            Integer countIos = 0;
            Integer countWeb = 0;

            newInfo.setCount(1);
            newInfo.setCountAndroid(countAndroid);
            newInfo.setCountIos(countIos);
            newInfo.setCountWeb(countWeb);
            newInfo.setCurrentDay(currentDay);
            newInfo.setCurrentMonth(currentMonth);
            newInfo.setCurrentYear(currentYear);

            return registerStatisticsRepository.save(newInfo);
        }
    }

    //===========================================HDMON-END===========================================
}
