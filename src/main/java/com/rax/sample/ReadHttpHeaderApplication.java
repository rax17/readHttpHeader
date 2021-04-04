package com.rax.sample;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RestController
@SpringBootApplication
@PropertySource(ignoreResourceNotFound = true, value = "classpath:env.properties")
public class ReadHttpHeaderApplication {

	@Value("${uri}")
	private String uri;

	String username = null;
	String password = null;

	public static void main(String[] args) {
		SpringApplication.run(ReadHttpHeaderApplication.class, args);
	}

	@GetMapping("/print-all-headers")
	public ResponseEntity<String> getAllheaders(@RequestHeader Map<String, String> headers) {
		headers.forEach((key, value) -> {
			System.out.println("Header Name: " + key + " Header Value: " + value);
			if (key.equalsIgnoreCase("authorization") && null != value) {

				byte[] decodedBytes = Base64.getDecoder().decode(value.replace("Basic", "").trim());
				String auth = new String(decodedBytes);
				String resultArr[] = auth.split(":");
				username = resultArr[0];
				password = resultArr[1];
			}

		});

		System.out.println("decoded username=" + username + " password=" + password);

		HttpStatus statusCd = senduserPwd(username, password);

		if (statusCd == HttpStatus.OK) {
			return new ResponseEntity<>("result successful", statusCd);
		} else {
			return new ResponseEntity<>("some error", statusCd);
		}

	}

	public HttpStatus senduserPwd(String uid, String pwd) {

		User newUser = new User(uid, pwd);
		HttpEntity<String> request = new HttpEntity<String>(newUser.toString());
		ResponseEntity<String> response= null;
		RestTemplate restTemplate = new RestTemplate();
		try {
			response = restTemplate.postForEntity(uri, request, String.class);
		} catch (HttpStatusCodeException ex) {
			response = new ResponseEntity<String>(ex.getResponseBodyAsString(), ex.getResponseHeaders(),
					ex.getStatusCode());
		}

		return response.getStatusCode();

		// final String uri = "https://otis.oktapreview.com/api/v1/authn";
		/*
		 * RestTemplate restTemplate = new RestTemplate(); User newUser = new User(uid,
		 * pwd);
		 * 
		 * User result = restTemplate.postForObject(uri, newUser, User.class);
		 * 
		 * result.
		 * 
		 * System.out.println(result);
		 */
	}

}
