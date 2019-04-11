package com.pie.binarytable.config;

import com.pie.binarytable.entities.Role;
import com.pie.binarytable.services.JwtTokenUtil;
import com.pie.binarytable.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter
{
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException
	{
		String token = req.getParameter("auth_token");
		String username = null;

		if(token != null)
		{
			try
			{
				username = jwtTokenUtil.getUsernameFromToken(token);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		if(username != null && SecurityContextHolder.getContext().getAuthentication() != null)
		{
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if(jwtTokenUtil.validateToken(token, userDetails))
			{
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Collections.singleton(Role.USER));
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		chain.doFilter(req, res);
	}
}
