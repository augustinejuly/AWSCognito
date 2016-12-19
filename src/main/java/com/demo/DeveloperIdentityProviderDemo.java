package com.demo;

import static com.demo.Util.getProperty;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityResult;
import com.amazonaws.services.securitytoken.model.Credentials;

public class DeveloperIdentityProviderDemo
{
	static final String IDENTITY_POOL_ID = getProperty("identity_pool_id1");

	static final String CUSTOM_PROVIDER_NAME = getProperty("id_provider_name1");

	static final String ROLE_ARN = "arn:aws:iam::247308865742:role/MyAssumableRole";
	
	static final String bucketName = "staging-bucket1";
	static final String key = "simple.txt";
	
	
	public static void main(String[] args) throws Exception 
	{
		AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJKHQH6WIF4725FZA","ZwIlQya8YkTE4XYfTxE/RmSCLgoRcPMjzegeeGax");
		AmazonCognitoIdentityClient identityClient = new AmazonCognitoIdentityClient(awsCredentials);
		identityClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
		

		try {
			
			GetOpenIdTokenForDeveloperIdentityRequest request = new GetOpenIdTokenForDeveloperIdentityRequest();
			request.setIdentityPoolId(IDENTITY_POOL_ID);
			request.setTokenDuration(900L);
			request.addLoginsEntry(CUSTOM_PROVIDER_NAME,"thiruID");
			GetOpenIdTokenForDeveloperIdentityResult result = identityClient.getOpenIdTokenForDeveloperIdentity(request);
			System.out.println("Identity is " + result.getIdentityId());
			System.out.println("OpenIdConnect Token is " + result.getToken());

			//Accessing STS
			AWSSecurityTokenService stsClient = new AWSSecurityTokenServiceClient(awsCredentials);
			
			AssumeRoleWithWebIdentityRequest stsReq = new AssumeRoleWithWebIdentityRequest();
			stsReq.setRoleArn(ROLE_ARN);
			stsReq.setWebIdentityToken(result.getToken());
			stsReq.setRoleSessionName("AppTestSession");

			AssumeRoleWithWebIdentityResult stsResp = stsClient.assumeRoleWithWebIdentity(stsReq);
			Credentials stsCredentials = stsResp.getCredentials(); 

			// Create the session credentials object
			AWSSessionCredentials sessionCredentials = new BasicSessionCredentials(
					stsCredentials.getAccessKeyId(),
					stsCredentials.getSecretAccessKey(),
					stsCredentials.getSessionToken());

			//Now Accessing S3
			AmazonS3 s3Client = new AmazonS3Client(sessionCredentials);
			S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, key));

			try(BufferedReader reader = new BufferedReader(new InputStreamReader(s3object.getObjectContent())))
			{
				while (true)
				{
					String line = reader.readLine();
					if (line == null)
					{
						break;
					}
					System.out.println(line);
				}
			}

			//s3Client.putObject(new PutObjectRequest(bucketName, "test", new File("C://output.txt")));

		}
		catch ( Exception exception )
		{
			throw exception;
		}
		
		System.out.println("---------------------Done---------------------");
		
	}
	
}
