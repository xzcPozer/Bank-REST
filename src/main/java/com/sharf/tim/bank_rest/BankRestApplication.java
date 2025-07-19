package com.sharf.tim.bank_rest;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.StringFixedSaltGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankRestApplication.class, args);

//		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//		encryptor.setPassword("mySecretKeys");
//		encryptor.setAlgorithm("PBEWithMD5AndDES");
//		encryptor.setSaltGenerator(new StringFixedSaltGenerator("fixedsalt"));
//
//		String[] cardNumbers = {
//				"1234 5678 9012 3456",
//				"2345 6789 0123 4567",
//				"3456 7890 1234 5678",
//				"4567 8901 2345 6789",
//				"5678 9012 3456 7890",
//				"6789 0123 4567 8901",
//				"7890 1234 5678 9012",
//				"8901 2345 6789 0123",
//				"9012 3456 7890 1234",
//				"0123 4567 8901 2345"
//		};
//
//		for (String cardNumber : cardNumbers) {
//			String encrypted = encryptor.encrypt(cardNumber);
//			System.out.println("Card Number: " + cardNumber + " -> Encrypted: " + encrypted);
//		}
	}

}
