package space.imaya.basic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.imaya.basic.domain.HealthRecord;
import space.imaya.basic.repository.HealthRecordRepository;

import java.util.List;

@Service
public class HealthRecordService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    public List<HealthRecord> getHealthRecordsByUsername(String userName) {
    	
        return healthRecordRepository.getHealthRecordsByUsername(userName);
    }
}
