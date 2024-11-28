package ncnl.balayanexpensewise.beans;

import lombok.Data;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * A class for encrypting the data inputted to the database. It also creates a random nickname for each
 */
@Data
public class Encryptor {
    private String userID;
    private String hashPassword;

    public Encryptor(String password ) {
        this.userID = generateShortId();
        this.hashPassword = createHashPassword(password);
    }

    public Encryptor(){

    }


    private String generateShortId(){
        return UUID.randomUUID().toString().substring(1,7);
    }


    /**
     * Using the SHA256 encryption method for making hash keys/passwords for better security.
     * You can also use other methods but SHA256 is better for anti-collision
     * @param password
     * @return String
     */
    public String createHashPassword(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] encode = md.digest(password.getBytes());
            BigInteger bigInt = new BigInteger(1, encode);
            return bigInt.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}
