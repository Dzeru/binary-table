package com.pie.binarytable.services;

import com.pie.binarytable.entities.Role;
import com.pie.binarytable.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil
{
	private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 360;

	public String getUsernameFromToken(String token)
	{
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getExpirationDateFromToken(String token)
	{
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver)
	{
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token)
	{
		return Jwts.parser().parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token)
	{
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(User user)
	{
		return doGenerateToken(user.getUsername());
	}

	private String doGenerateToken(String subject)
	{
		Claims claims = Jwts.claims().setSubject(subject);
		claims.put("scopes", Collections.singleton(Role.USER));

		return Jwts.builder()
				.setClaims(claims)
				.setIssuer("https://me.org:4443")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS))
				.signWith(SignatureAlgorithm.HS256, "b18i1t0able")
				.compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails)
	{
		final String username = getUsernameFromToken(token);

		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
