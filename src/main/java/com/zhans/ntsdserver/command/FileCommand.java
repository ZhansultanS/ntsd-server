package com.zhans.ntsdserver.command;

import com.zhans.ntsdserver.entity.File;

public interface FileCommand {
    File perform(String filename);
}
