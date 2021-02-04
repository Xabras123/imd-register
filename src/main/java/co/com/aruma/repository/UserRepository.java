package co.com.aruma.repository;

import java.io.Serializable;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import co.com.aruma.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, Integer>, Serializable {
	
	Mono<User> findById(String id);
	
	Flux<User> findByEmailOrCellphoneOrUsername(String email, String cellphone, String username);
	
	Mono<User>  findByIdAndAccountStatus(String id, int accountStatus);

	Flux<User>  findAllByAccountStatus(int accountStatus);

	//Mono<User> findAllByState(boolean active);

}
