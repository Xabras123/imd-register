package co.com.aruma.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import co.com.aruma.dto.Response;
//import co.com.aruma.dto.Response;
import co.com.aruma.dto.UserRegisterDTO;
import co.com.aruma.service.UserRegisterService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${application.services.user-crud}")

public class UserRegisterController {
	
	@Autowired
	UserRegisterService userRegisterService;
	
	//Metodo para determinar si un conjunto de credenciales son validas para el logueo de un usuario particular
	@PostMapping("crear-usuario")
	public Mono<Response> createUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO, 
			ServerWebExchange exchange){
		
		return this.userRegisterService.createUser(userRegisterDTO);
	}
	
	//Metodo para determinar si un conjunto de credenciales son validas para el logueo de un usuario particular
	@GetMapping("usuarios")
	public Mono<Response> getUsers(){
		
		return this.userRegisterService.getUsers();
	}
	
	
	//Metodo para determinar si un conjunto de credenciales son validas para el logueo de un usuario particular
	@GetMapping("usuario/{user-id}")
	public Mono<Response> getSingleUser(@PathVariable("user-id") String userId, 
			ServerWebExchange exchange){
		
		return this.userRegisterService.getSingleUser(userId);
	}
	
	
	//Metodo para determinar si un conjunto de credenciales son validas para el logueo de un usuario particular
	@DeleteMapping("usuario/{user-id}")
	public Mono<Response> deleteUser(@PathVariable("user-id") String userId, 
			ServerWebExchange exchange){
		
		return this.userRegisterService.deleteUser(userId);
	}

	
    @GetMapping("/ping")
    public Mono<Response> ping( ServerWebExchange exchange) {
        return this.userRegisterService.ping( exchange.getRequest().getHeaders());
    }
}
