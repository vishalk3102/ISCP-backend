package com.iscp.backend.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;


@Component
public class TokenBlacklist {

    private final ConcurrentHashMap<String,Boolean> blacklistTokenList=new ConcurrentHashMap<>();

    public void addToBlacklistTokenList(String token)
    {
        blacklistTokenList.put(token,true);
    }

    public  boolean isTokenBlacklisted(String token)
    {
        return blacklistTokenList.containsKey(token);
    }
}
