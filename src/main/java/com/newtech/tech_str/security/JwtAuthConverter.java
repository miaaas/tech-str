package com.newtech.tech_str.security;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component //ova klasa je konverter sto uzima jwt i konverta u authentication token
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    //helper object koji na kraj konverta inf iz jwt u role
  private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

  public JwtAuthConverter() {
      this.jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
  }

    @SuppressWarnings("null")
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
            //pravimo varijablu koja ce imati all the users granted authority - authorities
            final Set<GrantedAuthority> authorities = Stream.concat( //strem.concat merges two streams tj. two sets of authorities in one
            jwtGrantedAuthoritiesConverter.convert(jwt).stream(), //predifined, direktno extract role iz jwt i konverta u grantedauthority object
            extractUserRoles(jwt).stream() //isto ko gore samo dodatne role koje smo mi zadali u nasem realmu, dole definisana
        ).collect(Collectors.toSet()); //sve sto je gore extracted i konvertovano, spasava se u jedan set i nema duplikata
        return new JwtAuthenticationToken(jwt, authorities); // ovo returna oboje i jwt(users identity) i set authorities(aut. assigned to user); this is checked in app's requests
    }

    @SuppressWarnings("unchecked")
    private Set<? extends GrantedAuthority> extractUserRoles(Jwt jwt) {
        final Map<String, Object> resourceAccess = jwt.getClaim("resource_access"); //extractamo resource_access iz naseg jwt jer su role zadane za klijenta, ovdje saznajemo imena klijenata i sve sto oni sadrze (role)
        final Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get("myclient");
        final List<String> roles = (List<String>) clientRoles.get("roles");

        if (CollectionUtils.isNotEmpty(roles)) {//konverta listu u set GrantedAuhority objects
            return roles.stream() //svaku rolu u listi procesira
                    .map(role -> new SimpleGrantedAuthority(role)) //za svaku rolu pravi novi objekat
                    .collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }

}
