package com.demo;

import static com.demo.Util.getProperty;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityResult;
import com.amazonaws.services.securitytoken.model.Credentials;

public class CognitoUPIDProviderDemo 
{
	static final String IDENTITY_POOL_ID = getProperty("identity_pool_id");

	static final String IDENTITY_PROVIDER_NAME=getProperty("id_provider_name");

	static final String ROLE_ARN = getProperty("role_arn");

	static final String bucketName = getProperty("bucket_name");
	static final String key = getProperty("bucket_key");

	static final String ID_TOKEN = "id_token";

	public static void main(String[] args) throws Exception
	{
		AmazonCognitoIdentityClient cognitoIdentityClient = new AmazonCognitoIdentityClient(new AnonymousAWSCredentials());
		cognitoIdentityClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

		//1. GetID
		GetIdRequest idRequest = new GetIdRequest();
		idRequest.setIdentityPoolId(IDENTITY_POOL_ID);
		idRequest.addLoginsEntry(IDENTITY_PROVIDER_NAME, getProperty(ID_TOKEN));
		GetIdResult idResult = cognitoIdentityClient.getId(idRequest);
		String identityId = idResult.getIdentityId();
		System.out.println("Identity is " + identityId);

		//2. GetOpenIdToken
		GetOpenIdTokenRequest openIdTokenRequest = new GetOpenIdTokenRequest();
		openIdTokenRequest.setIdentityId(identityId);
		openIdTokenRequest.addLoginsEntry(IDENTITY_PROVIDER_NAME, getProperty(ID_TOKEN));
		GetOpenIdTokenResult openIdTokenResult = cognitoIdentityClient.getOpenIdToken(openIdTokenRequest);
		String openIdToken = openIdTokenResult.getToken();
		System.out.println("Open Id token is " + openIdToken);

		//3. AssumeRoleWithWebIdentity
		AWSSecurityTokenService stsClient = new AWSSecurityTokenServiceClient(new AnonymousAWSCredentials());

		AssumeRoleWithWebIdentityRequest stsReq = new AssumeRoleWithWebIdentityRequest();
		stsReq.setRoleArn(ROLE_ARN);
		stsReq.setWebIdentityToken(openIdToken);
		stsReq.setRoleSessionName("AppTestSession");
		//stsReq.setDurationSeconds(durationSeconds);

		AssumeRoleWithWebIdentityResult stsResp = stsClient.assumeRoleWithWebIdentity(stsReq);
		Credentials stsCredentials = stsResp.getCredentials();

		AWSSessionCredentials temporarySessionCredentials = new BasicSessionCredentials(
				stsCredentials.getAccessKeyId(),
				stsCredentials.getSecretAccessKey(),
				stsCredentials.getSessionToken());
		
		
		//Accessing AWS Resources (S3)
		AmazonS3 s3Client = new AmazonS3Client(temporarySessionCredentials);
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

		//try to put a file into S3 bucket and it should throw access denied exception
		//s3Client.putObject(new PutObjectRequest(bucketName, "test", new File("C://output.txt")));

		System.out.println("----------------Done---------------");
	}


}
