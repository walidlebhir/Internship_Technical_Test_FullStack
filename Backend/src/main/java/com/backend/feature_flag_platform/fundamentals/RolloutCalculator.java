package com.backend.feature_flag_platform.fundamentals;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RolloutCalculator {

    private  RolloutCalculator(){}

    public  static boolean isInRollout(
            String userId, String featureKey, int percentage
    ){
        if(percentage < 0 ){
            return  false ;
        }

        if(percentage >= 100){
            return  true  ;
        }

        if(userId == null || featureKey == null){
            return  false ;
        }

        // on cree hash  :
        try{
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            String key = userId + ":" +featureKey ;
            byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            int value =  ((hash[0] & 0xff) << 24)
                            | ((hash[1] & 0xff) << 16)
                            | ((hash[2] & 0xff) << 8)
                            | (hash[3] & 0xff);

            value = Math.abs(value);
            int bucket = value%100 ;
            return  bucket < percentage  ;

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e)  ;
        }
    }
}

