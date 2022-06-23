package com.zhans.ntsdserver.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class SocketRequest {
    private final String command;
    private final String filename;
}
