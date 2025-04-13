package com.pixelwave.spring_boot.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelSubscription {
    private String userId;
    private String channelId;
}

