package com.pie.binarytable.services;

import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.dto.GoogleOauthUserInfo;
import com.pie.binarytable.entities.Role;
import com.pie.binarytable.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Service
public class OAuth2Service extends OidcUserService
{
	@Autowired
	private UserDAO userDAO;

	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException
	{
		OidcUser oidcUser = super.loadUser(userRequest);
		Map<String, Object> attributes = oidcUser.getAttributes();
		GoogleOauthUserInfo googleOauthUserInfo = new GoogleOauthUserInfo();

		googleOauthUserInfo.setEmail((String) attributes.get("email"));
		googleOauthUserInfo.setName((String) attributes.get("name"));
		googleOauthUserInfo.setId((String) attributes.get("sub"));
		googleOauthUserInfo.setImageUrl((String) attributes.get("picture"));

		updateUser(googleOauthUserInfo);

		return oidcUser;
	}


	private void updateUser(GoogleOauthUserInfo googleOauthUserInfo)
	{
		User user = userDAO.findByGoogleUsername(googleOauthUserInfo.getEmail());

		if(user == null)
		{
			user = new User();
		}

		user.setName(googleOauthUserInfo.getName());
		user.setUsername(googleOauthUserInfo.getEmail());
		user.setPassword("googleUser");
		user.setRegistrationDate(LocalDateTime.now().toString());
		user.setRoles(Collections.singleton(Role.USER));
		user.setActive(true);

		userDAO.save(user);
	}
}
