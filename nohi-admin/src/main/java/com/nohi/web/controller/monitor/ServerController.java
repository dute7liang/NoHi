package com.nohi.web.controller.monitor;

import com.nohi.common.core.domain.R;
import com.nohi.framework.web.domain.Server;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 *
 * @author nohi
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController {
    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping()
    public R<Server> getInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return R.ok(server);
    }
}
