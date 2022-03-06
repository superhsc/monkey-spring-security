package space.imaya.basic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import space.imaya.basic.domain.HealthRecord;

import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Integer> {

	List<HealthRecord> getHealthRecordsByUsername(String username);
}
