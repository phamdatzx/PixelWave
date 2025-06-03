package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.model.User;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SocketConnectionManager {
    private Set<Long> connectedUserIds = ConcurrentHashMap.newKeySet();
}
