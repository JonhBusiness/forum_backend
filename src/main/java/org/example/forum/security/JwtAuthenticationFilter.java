package org.example.forum.security;

import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.forum.exception.TokenExpiredException;
import org.example.forum.exception.TokenMalformedException;
import org.example.forum.exception.TokenMissingException;
import org.example.forum.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

//    private String authPath="/auth/**";
private String[] publicPaths = {
        "/auth/**",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/cursos"
};
    private AntPathMatcher pathMatcher = new AntPathMatcher();
//    private String[] swaggerPaths = {
//            "/swagger-ui.html",
//            "/swagger-ui/**",
//            "/v3/api-docs/**",
//            "/swagger-resources/**"
//    };
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (isPublicPath(request)) {
                filterChain.doFilter(request, response);
                return;
            }
//            if (isSwaggerPath(request) || request.getServletPath().contains("/cursos")) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            if (isAuthPath(request)) {
//                filterChain.doFilter(request, response);
//                return;
//            }
            String jwt = getJwtFromRequest(request);
            if (jwt == null) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("JWT token is missing");
                return;
            }
//            if (jwt == null) {
//                throw new TokenMissingException("JWT token is missing");
//            }

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }
        catch (TokenExpiredException ex) {
            // Token inválido o expirado
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ex.getMessage());
            return;
        } catch (TokenMalformedException ex) {
            // Token inválido
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(ex.getMessage());
            return;
        } catch (TokenMissingException ex) {
            //Token Null
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(ex.getMessage());
                return;
        } catch (BadCredentialsException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ex.getMessage());
            return;
        }
        catch (SecurityException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(ex.getMessage());
            return;
        }
        catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    private boolean isPublicPath(HttpServletRequest request) {
        for (String path : publicPaths) {
            if (pathMatcher.match(path, request.getServletPath())) {
                return true;
            }
        }
        return false;
    }
//    private boolean isAuthPath(HttpServletRequest request) {
//        return pathMatcher.match(authPath, request.getServletPath());
//    }
//    private boolean isSwaggerPath(HttpServletRequest request) {
//        for (String path : swaggerPaths) {
//            if (pathMatcher.match(path, request.getServletPath())) {
//                return true;
//            }
//        }
//        return false;
//    }
}
