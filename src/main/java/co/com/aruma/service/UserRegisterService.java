package co.com.aruma.service;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import co.com.aruma.dto.RegisteredUserDTO;
import co.com.aruma.dto.Response;
import co.com.aruma.dto.UserRegisterDTO;
import co.com.aruma.entity.User;
import co.com.aruma.repository.UserRepository;
import co.com.aruma.security.EncryptAndDecrypt;
import co.com.aruma.type.UserStatus;
import co.com.aruma.utils.LogTransaction;
import reactor.core.publisher.Mono;

@Service
public class UserRegisterService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EncryptAndDecrypt encryptAndDecrypt;
	
	@Value("${application.password-validation-regex}") String PaswordValidationRegex;
	static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	
	private static final String EMAIL_ALREADY_EXIST = "Email is already registerd in the platform";
	private static final String CELL_ALREADY_EXIST = "Cellphone is already registerd in the platform";
	private static final String USERNAME_ALREADY_EXIST = "Username is already registerd in the platform";
	private static final String USER_NOT_FOUND_OR_DISABLED = "User doesnt exist or is already disabled";
	private static final String USER_NOT_FOUND = "User doesnt exist";
	private static final String ERROR_DELETING_USER = "Error Deleting User";
	
	


	public Mono<Response> createUser(@Valid UserRegisterDTO userRegisterDTO) {
		
		LogTransaction log = new LogTransaction(null);
        log.startTransaction("Get Single User", userRegisterDTO);
		
		return  userRepository.findByEmailOrCellphoneOrUsername(userRegisterDTO.getEmail(),userRegisterDTO.getCellphone(), userRegisterDTO.getUsername())
		.collectList().flatMap(users -> {
			
			
			String userExistanceValidation = validateUserExistance(userRegisterDTO.getEmail(),userRegisterDTO.getCellphone(), userRegisterDTO.getUsername(), (ArrayList<User>)users);
			if(!userExistanceValidation.equals("")) {
				Response res = Response.builder().status(false).message(userExistanceValidation ).build();
				log.endTransaction(res);
				return Mono.just(res);
			}
			
			if(!validateEmail(userRegisterDTO.getEmail())) {
				
				Response res = Response.builder().status(false).message("Invalid Email").build();
				log.endTransaction(res);
				return Mono.just(res);
				
			}
			
			
			if(!validatePhone(userRegisterDTO.getCellphone())) {
				
				Response res = Response.builder().status(false).message("Invalid Phone number").build();
				log.endTransaction(res);
				return Mono.just(res);
				
			}
			
			
			if(!validatePassword(userRegisterDTO.getPassword())) {
				
				Response res = Response.builder().status(false).message("Invalid Password").build();
				log.endTransaction(res);
				return Mono.just(res);
				
			}
			
    		User user = mapEntityToDTO(userRegisterDTO);
		
			return this.userRepository.save(user)
				.flatMap(userSaved->{
					
					Response res = Response.builder()
	                		.status(true)
	                		.message("OK")
	                		.data(mapEntityToDTO(userSaved))
	                        .build();
					log.endTransaction(res);
					return Mono.just(res);
				});
			
        	
        }).onErrorResume(e -> {

			e.printStackTrace();

			Response res = Response.builder().status(false).message("Error ocurred: " + e.getMessage()).data(null).build();
			log.endTransaction(res);
			return Mono.just(res);

		});
	}
	
	
	
	
	
	private boolean validateEmail(String email) {
		
		if(email.length() > 40)
			return false;
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
	}

	private boolean validatePhone(String phone) {
		
		
		if(phone.length() != 10)
			return false;
		return true;
	}


	private boolean validatePassword(String password) {
		if(password.length() > 24)
			return false;
		return password.matches(PaswordValidationRegex);

	}

	private User mapEntityToDTO(@Valid UserRegisterDTO userRegisterDTO) {
		
		return User.builder()
				.username(userRegisterDTO.getUsername())
				.email(userRegisterDTO.getEmail())
				.cellphone(userRegisterDTO.getCellphone())
				.password(encryptAndDecrypt.encrypt(userRegisterDTO.getPassword()))
				.accountStatus(UserStatus.REGISTER_EMAIL_VERIFIED.getCode())
				.build();
	}

	private String validateUserExistance(String email, String cellphone, String username, ArrayList<User> users) {
		
		String output = "";
		boolean bandEmailAlreadyExist = false;
		boolean bandCellAlreadyExist = false;
		boolean bandUsernameAlreadyExist = false;

		for(User user : users) {
			if(user.getEmail().equals(email) && !bandEmailAlreadyExist) {
				output += EMAIL_ALREADY_EXIST + " - ";
				bandEmailAlreadyExist = true;
			}
			
			if(user.getCellphone().equals(cellphone) && !bandCellAlreadyExist) {
				output += CELL_ALREADY_EXIST + " - ";
				bandCellAlreadyExist = true;
			}
			
			if(user.getUsername().equals(username) && !bandUsernameAlreadyExist) {
				output += USERNAME_ALREADY_EXIST + " - ";
				bandUsernameAlreadyExist = true;
			}
			
			if(bandEmailAlreadyExist && bandCellAlreadyExist && bandUsernameAlreadyExist)
				break;
			
			
		}
		return output;
		
	}





	public Mono<Response> deleteUser(String userId) {
		LogTransaction log = new LogTransaction(null);
        log.startTransaction("Get Single User", userId);
		return userRepository.findByIdAndAccountStatus(userId, UserStatus.REGISTER_EMAIL_VERIFIED.getCode()).switchIfEmpty(Mono.error(new Exception(USER_NOT_FOUND_OR_DISABLED)))
		        .flatMap(user -> {
		        	
		        	user.setAccountStatus(UserStatus.ACCOUNT_DISABLED.getCode());
		        	return userRepository.save(user).switchIfEmpty(Mono.error(new Exception(ERROR_DELETING_USER)))
		    		        .flatMap(deletedUser -> {
		    		        		
		    					Response res =Response.builder().status(true).message("Ok").data(deletedUser.getId().toString()).build();
		    					log.endTransaction(res);
		    					return Mono.just(res);
		    		        	
		    		        }).onErrorResume(e -> {
		    					Response res =Response.builder().status(false).message(e.getMessage()).data(null).build();
		    					log.endTransaction(res);
		    					return Mono.just(res);
		    				});	
		        	
		        }).onErrorResume(e -> {
					Response res =Response.builder().status(false).message(e.getMessage()).data(null).build();
					log.endTransaction(res);
					return Mono.just(res);
				});	
	}





	public Mono<Response> getSingleUser(String userId) {
		
		LogTransaction log = new LogTransaction(null);
        log.startTransaction("Get Single User", userId);
		return userRepository.findByIdAndAccountStatus(userId, UserStatus.REGISTER_EMAIL_VERIFIED.getCode()).switchIfEmpty(Mono.error(new Exception(USER_NOT_FOUND)))
		        .flatMap(user -> {
		        	
		        	return Mono.just(Response
		        			.builder()
		        			.data(mapEntityToDTO(user))
		        			.status(true)
		        			.message("Ok")
		        			.build());

		        	
		        }).onErrorResume(e -> {
					Response res =Response.builder().status(false).message(e.getMessage()).data(null).build();
					log.endTransaction(res);
					return Mono.just(res);
				});	
	}





	public Mono<Response> getUsers() {
		
		LogTransaction log = new LogTransaction(null);
        log.startTransaction("Get All Users", null);
        
		return  userRepository.findAllByAccountStatus(UserStatus.REGISTER_EMAIL_VERIFIED.getCode())
		.collectList().flatMap(users -> {
			
					
				Response res = Response.builder()
                		.status(true)
                		.message("OK")
                		.data(mapListToListDTO((ArrayList<User>)users))
                        .build();
				log.endTransaction(res);
				return Mono.just(res);
				
			
        	
        }).onErrorResume(e -> {

			e.printStackTrace();

			Response res = Response.builder().status(false).message("Error ocurred: " + e.getMessage()).build();
			log.endTransaction(res);
			return Mono.just(res);

		});
        
        

	}
	
	private RegisteredUserDTO mapEntityToDTO(User user){
		
		
		return RegisteredUserDTO.builder()
				.id(user.getId().toString())
				.email(user.getEmail())
				.username(user.getUsername())
				.cellphone(user.getCellphone())
				.password(user.getPassword())
				.build();
		
	}
	
	private ArrayList<RegisteredUserDTO> mapListToListDTO(ArrayList<User> users){
		
		ArrayList<RegisteredUserDTO> usersDTO = new ArrayList<>();
		for(User user : users) {
			usersDTO.add(mapEntityToDTO(user));
		}
		
		return usersDTO;
		
	}
	

	 
	
	
	//El pingolin
	public Mono<Response> ping( HttpHeaders hh) {
		
		LogTransaction log = new LogTransaction(hh);
        log.startTransaction("ping user login", null);
    	log.endTransaction(null);
        return Mono.just(Response.builder().status(true).message("Service is working...").data(null).build());
    }

}
