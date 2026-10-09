package com.Marketplace_Management.Auth.Services.OAuth;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Auth.Constants.Message;
import com.Marketplace_Management.Auth.Constants.OAuthProvider;
import com.Marketplace_Management.Shared.Errors.Exceptions.BadRequestException;

/** Picks the strategy of a provider. Every OAuthStrategy bean registers itself here. */
@Component
public class OAuthStrategyResolver {
    private final Map<OAuthProvider, OAuthStrategy> strategies = new EnumMap<>(OAuthProvider.class);

    public OAuthStrategyResolver(List<OAuthStrategy> strategies) {
        strategies.forEach(strategy -> this.strategies.put(strategy.provider(), strategy));
    }

    public OAuthStrategy resolve(OAuthProvider provider) {
        OAuthStrategy strategy = strategies.get(provider);
        if (strategy == null) {
            throw new BadRequestException(Message.OAUTH_PROVIDER_UNSUPPORTED);
        }
        return strategy;
    }
}
