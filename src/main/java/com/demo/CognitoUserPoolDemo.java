package com.demo;

import static com.demo.Util.getProperty;
import static com.demo.Util.getSecretHash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClient;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ChangePasswordRequest;
import com.amazonaws.services.cognitoidp.model.ChangePasswordResult;
import com.amazonaws.services.cognitoidp.model.CodeDeliveryDetailsType;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.DeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutRequest;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutResult;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.amazonaws.services.cognitoidp.model.UpdateUserAttributesRequest;

public class CognitoUserPoolDemo 
{

	static final AWSCredentials awsCredentials = new BasicAWSCredentials(getProperty("access_key"),getProperty("secret_key"));
	static final AWSCognitoIdentityProviderClient cognitoClient = new AWSCognitoIdentityProviderClient(awsCredentials);

	static final String USER_POOL_ID = getProperty("userpool_id");

	static final String APP_NAME = getProperty("app_name");

	static final String APP_CLIENT_ID = getProperty("app_client_id");
	static final String APP_CLIENT_SECRET =  getProperty("app_client_secret");

	static final String ID_TOKEN = "id_token";
	static final String ACCESS_TOKEN = "access_token";
	static final String REFRESH_TOKEN = "refresh_token";

	static 
	{
		cognitoClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
	}

	public static void main(String[] args)
	{
		String userId = getProperty("userId");
		
		//signUp(userId,getProperty("password"));
		//verifyEmail(userId);
		//login(userId, getProperty("password"));
		//signOut();

		//updateUserAttributes();
		//getUser();

		//forgotPassword(userId);
		//confirmForgotPassword(userId, getProperty("confirm_password"));
		
		//changePassword(getProperty("confirm_password"), getProperty("change_password"));
		
		renewAccessTokenUsingRefreshToken(userId);
		
		//deleteUser();

		System.out.println("--------------Done-------------");
	}

	static void signUp(String userId,String password)
	{
		SignUpRequest signUpRequest = new SignUpRequest();
		
		signUpRequest.setClientId(APP_CLIENT_ID);
		signUpRequest.setSecretHash(getSecretHash(userId, APP_CLIENT_ID, APP_CLIENT_SECRET));
		
		signUpRequest.setUsername(userId);
		signUpRequest.setPassword(password);
		
		List<AttributeType> attributeTypeList = new ArrayList<>();
		attributeTypeList.add(new AttributeType().withName("email").withValue(getProperty("email")));
		signUpRequest.setUserAttributes(attributeTypeList);

		SignUpResult signUpResult = cognitoClient.signUp(signUpRequest);
		System.out.println(signUpResult.toString());
	}

	static void verifyEmail(String userId)
	{

		ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest();
		confirmSignUpRequest.setConfirmationCode("538625");
		confirmSignUpRequest.setClientId(APP_CLIENT_ID);
		confirmSignUpRequest.setSecretHash(getSecretHash(userId, APP_CLIENT_ID, APP_CLIENT_SECRET));
		confirmSignUpRequest.setUsername(userId);
		ConfirmSignUpResult confirmSignUpResult = cognitoClient.confirmSignUp(confirmSignUpRequest);

		System.out.println(confirmSignUpResult);
	}

	static void login(String userId, String password)
	{
		AdminInitiateAuthRequest adminInitiateAuthRequest = new AdminInitiateAuthRequest();
		adminInitiateAuthRequest.setAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH);
		adminInitiateAuthRequest.setClientId(APP_CLIENT_ID);
		adminInitiateAuthRequest.setUserPoolId(USER_POOL_ID);

		Map<String,String> authParamMap = new HashMap<>();
		authParamMap.put("USERNAME", userId);
		authParamMap.put("PASSWORD", password);
		authParamMap.put("SECRET_HASH", getSecretHash(userId, APP_CLIENT_ID, APP_CLIENT_SECRET));
		
		adminInitiateAuthRequest.setAuthParameters(authParamMap);

		AdminInitiateAuthResult adminInitiateAuthResult = cognitoClient.adminInitiateAuth(adminInitiateAuthRequest);
		AuthenticationResultType authenticationResultType = adminInitiateAuthResult.getAuthenticationResult();
		System.out.println("id_token=" + authenticationResultType.getIdToken());
		System.out.println("access_token=" + authenticationResultType.getAccessToken());
		System.out.println("refresh_token=" + authenticationResultType.getRefreshToken());
		//System.out.println("Expires in " + authenticationResultType.getExpiresIn());

	}

	static void signOut()
	{
		GlobalSignOutRequest globalSignOutRequest = new GlobalSignOutRequest();
		globalSignOutRequest.setAccessToken(getProperty(ACCESS_TOKEN));
		GlobalSignOutResult globalSignOutResult = cognitoClient.globalSignOut(globalSignOutRequest);
		System.out.println(globalSignOutResult.toString());
	}

	static void getUser()
	{

		GetUserRequest getUserRequest = new GetUserRequest();
		getUserRequest.setAccessToken(getProperty(ACCESS_TOKEN));

		GetUserResult userResult = cognitoClient.getUser(getUserRequest);

		System.out.println("UserName " + userResult.getUsername());

		userResult.getUserAttributes().stream()
		.forEach(a -> System.out.println("Name: " + a.getName() + " ,Value : " + a.getValue()));

	}

	static void updateUserAttributes()
	{
		UpdateUserAttributesRequest updateUserAttributesRequest = new UpdateUserAttributesRequest();
		updateUserAttributesRequest.setAccessToken(getProperty(ACCESS_TOKEN));

		List<AttributeType> userAttributes = new ArrayList<>();
		userAttributes.add(new AttributeType().withName("gender").withValue("Male"));
		userAttributes.add(new AttributeType().withName("custom:role").withValue("admin"));
		userAttributes.add(new AttributeType().withName("custom:region").withValue("North Region"));
		userAttributes.add(new AttributeType().withName("custom:customer").withValue("Bob Medical Center"));
		userAttributes.add(new AttributeType().withName("custom:facility").withValue("Cardiac Facility"));
		userAttributes.add(new AttributeType().withName("custom:ipaddress").withValue("127.0.0.1"));

		updateUserAttributesRequest.setUserAttributes(userAttributes);
		cognitoClient.updateUserAttributes(updateUserAttributesRequest);

		/*AdminUpdateUserAttributesRequest adminUpdateUserAttributesRequest = new AdminUpdateUserAttributesRequest();
		adminUpdateUserAttributesRequest.setUsername("demoUser");
		adminUpdateUserAttributesRequest.setUserPoolId(USER_POOL_ID);
		adminUpdateUserAttributesRequest.setUserAttributes(userAttributes);
		cognitoClient.adminUpdateUserAttributes(adminUpdateUserAttributesRequest);*/
	}

	static void forgotPassword(String userId)
	{
		ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
		forgotPasswordRequest.setClientId(APP_CLIENT_ID);
		forgotPasswordRequest.setSecretHash(getSecretHash(userId, APP_CLIENT_ID, APP_CLIENT_SECRET));
		forgotPasswordRequest.setUsername(userId);
		ForgotPasswordResult forgotPasswordResult = cognitoClient.forgotPassword(forgotPasswordRequest);

		CodeDeliveryDetailsType codeDeliveryDetails = forgotPasswordResult.getCodeDeliveryDetails();

		System.out.println(" " + codeDeliveryDetails.getAttributeName());
		System.out.println(" " + codeDeliveryDetails.getDeliveryMedium());
		System.out.println(" " + codeDeliveryDetails.getDestination());
	}

	static void confirmForgotPassword(String userId,String password)
	{
		ConfirmForgotPasswordRequest confirmForgotPasswordRequest = new ConfirmForgotPasswordRequest();
		confirmForgotPasswordRequest.setClientId(APP_CLIENT_ID);
		confirmForgotPasswordRequest.setSecretHash(getSecretHash(userId, APP_CLIENT_ID, APP_CLIENT_SECRET));
		confirmForgotPasswordRequest.setConfirmationCode("856797");
		confirmForgotPasswordRequest.setUsername(userId);
		confirmForgotPasswordRequest.setPassword(password);
		ConfirmForgotPasswordResult confirmForgotPasswordResult = cognitoClient.confirmForgotPassword(confirmForgotPasswordRequest);
		System.out.println(confirmForgotPasswordResult.toString());
	}

	static void changePassword(String oldPassword, String newPassword)
	{
		ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
		changePasswordRequest.setAccessToken(getProperty(ACCESS_TOKEN));
		changePasswordRequest.setPreviousPassword(oldPassword);
		changePasswordRequest.setProposedPassword(newPassword);
		ChangePasswordResult changePasswordResult = cognitoClient.changePassword(changePasswordRequest);
		System.out.println("" + changePasswordResult.toString());
	}

	static void deleteUser()
	{
		DeleteUserRequest deleteUserRequest = new DeleteUserRequest();
		deleteUserRequest.setAccessToken(getProperty(ACCESS_TOKEN));
		cognitoClient.deleteUser(deleteUserRequest);
	}

	static void renewAccessTokenUsingRefreshToken(String userId)
	{
		AdminInitiateAuthRequest adminInitiateAuthRequest = new AdminInitiateAuthRequest();
		adminInitiateAuthRequest.setAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH);
		adminInitiateAuthRequest.setClientId(APP_CLIENT_ID);
		adminInitiateAuthRequest.setUserPoolId(USER_POOL_ID);

		HashMap<String,String> authParamMap = new HashMap<>();
		authParamMap.put("USERNAME", userId);
		//authParamMap.put("PASSWORD", "password");
		authParamMap.put("REFRESH_TOKEN", getProperty(REFRESH_TOKEN));
		authParamMap.put("SECRET_HASH", getSecretHash(userId, APP_CLIENT_ID, APP_CLIENT_SECRET));

		adminInitiateAuthRequest.setAuthParameters(authParamMap);

		AdminInitiateAuthResult adminInitiateAuthResult = cognitoClient.adminInitiateAuth(adminInitiateAuthRequest);
		AuthenticationResultType authenticationResultType = adminInitiateAuthResult.getAuthenticationResult();
		System.out.println("id_token=" + authenticationResultType.getIdToken());
		System.out.println("access_token=" + authenticationResultType.getAccessToken());
		System.out.println("refresh_token=" + authenticationResultType.getRefreshToken());
		//System.out.println("Expires in " + authenticationResultType.getExpiresIn());

	}

}
