package com.raidiam.techtask.accountapi.security;

import com.raidiam.techtask.accountapi.model.Account;
import com.raidiam.techtask.accountapi.model.Customer;
import com.raidiam.techtask.accountapi.services.AccountService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
public class SecurityAdvice {

    private static final Pattern BEARER = Pattern.compile("^Bearer (?<bearer>.+)");

    @Autowired
    private SecurityFacade securityFacade;

    @Autowired
    private AccountService accountService;

    @Around("@annotation(secured)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, Secured secured) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String authorization = request.getHeader("Authorization");
        if(authorization == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No auth header");
        }
        Matcher matcher = BEARER.matcher(authorization);
        if(!matcher.matches()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No bearer token");
        }
        String bearerToken = Optional.ofNullable(matcher.group("bearer")).orElseThrow(() ->  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No bearer token"));
        verify(bearerToken, request, secured);
        return joinPoint.proceed();
    }

    private void verify(String bearerToken, HttpServletRequest request, Secured secured) {
        if(secured.id().equals("customerId")) {
            Authorisation authorisation = Optional.ofNullable(securityFacade.introspect(bearerToken)).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown customer"));
            Customer customer = authorisation.getCustomer();
            Map<String, Object> vars = (Map) request.getAttribute("org.springframework.web.servlet.View.pathVariables");
            UUID customerId = (UUID) vars.get("customerId");
            if(!customer.getId().equals(customerId)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not resource owner");
            }
            if(!authorisation.getScope().equals(secured.scope())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid scope");
            }
            return;
        }
        if(secured.id().equals("accountId")) {
            Authorisation authorisation = Optional.ofNullable(securityFacade.introspect(bearerToken)).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown customer"));
            Customer customer = authorisation.getCustomer();
            Map<String, Object> vars = (Map) request.getAttribute("org.springframework.web.servlet.View.pathVariables");
            String accountId = (String) vars.get("accountId");
            Account account = accountService.getById(accountId);
            Customer owner = account.getCustomer();
            if(!customer.getId().equals(owner.getId())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not resource owner");
            }
            if(!authorisation.getScope().equals(secured.scope())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid scope");
            }
            return;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised");
    }

}
