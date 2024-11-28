package ncnl.balayanexpensewise.beans;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class EncryptorTest {

    String password = "DavidAndGoliath";

    @Test
    void createHash(){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] encode = md.digest(password.getBytes());
            BigInteger bigInt = new BigInteger(1, encode);
            System.out.println(bigInt);


        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }




}