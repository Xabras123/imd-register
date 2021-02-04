package co.com.aruma.dto;

	
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRegisterDTO {

	@NotBlank(message = "please provide a email")
	private String email;

	@NotBlank(message = "please provide a user password")
	private String password;
	
	//@NotBlank(message = "please provide a password confirmation")
	//private String passwordConfirmation;

	@NotBlank(message = "please provide a cellphone number")
	private String cellphone;
	
	@NotBlank(message = "please provide the users name")
	private String username;

}	


