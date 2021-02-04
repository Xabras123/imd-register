package co.com.aruma.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection =  "user")
public class User {
	
	
    @Id
    @Field(value = "_id")
    public ObjectId id;

	@Field("username") private String username;
	@Field("password") private String password;
    

    @Field("email") private String email;
    @Field("cellphone") private String cellphone;
    
    @Builder.Default
    @Field("accountStatus") private int accountStatus = 1;
    
    //@Field("last_login_date") private Date last_login_date;
    //@Field("login_tries") private int login_tries;
    //@Field("login_block_date")private Date login_block_date;
    //@Field("activation_date")private Date activation_date;

}