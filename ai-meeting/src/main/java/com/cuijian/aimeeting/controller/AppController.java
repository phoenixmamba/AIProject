package com.cuijian.aimeeting.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cuijian.aimeeting.common.BaseResponse;
import com.cuijian.aimeeting.constant.AppConstant;
import com.cuijian.aimeeting.entity.AppChatHistory;
import com.cuijian.aimeeting.entity.AppInfo;
import com.cuijian.aimeeting.entity.Meeting;
import com.cuijian.aimeeting.monitor.MonitorContext;
import com.cuijian.aimeeting.monitor.MonitorContextHolder;
import com.cuijian.aimeeting.service.AppService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 测试ai创建和编辑会议
 * @Date : 2025/9/26 10:52
 **/
@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @PostMapping("/createApp")
    public ResponseEntity<String> createApp(@RequestBody CreateAppRequest request, HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getHeader("userId") == null ? "admin" : httpServletRequest.getHeader("userId");
        return ResponseEntity.ok(appService.createApp(request.getAppName(), request.getInitPrompt(), userId));
    }
    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGenCode(@RequestParam String appId,
                                                       @RequestParam String message,
                                                       HttpServletRequest request) {
        // 参数校验
        if (appId == null || StrUtil.isBlank(message)) {
            throw new IllegalArgumentException("应用ID和消息内容不能为空");
        }
        // 获取当前登录用户
        String userId = request.getHeader("userId") == null ? "admin" : request.getHeader("userId");
        // 调用服务生成代码（SSE 流式返回）
        Flux<String> contentFlux = appService.chatToGenCode(appId, message, userId);
        return contentFlux
                .map(chunk -> {
                    Map<String, String> wrapper = Map.of("d", chunk);
                    String jsonData = JSONUtil.toJsonStr(wrapper);
                    return ServerSentEvent.<String>builder()
                            .data(jsonData)
                            .build();
                })
                .concatWith(Mono.just(
                        // 发送结束事件
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build()
                ));
    }

    /**
     * 根据 id 获取应用详情
     *
     * @param id 应用 id
     * @return 应用详情
     */
    @GetMapping("/get/vo")
    public BaseResponse<AppInfo> getAppById(String id) {
        // 参数校验
        if (StrUtil.isBlank(id)) {
            throw new IllegalArgumentException("应用ID不能为空");
        }
        
        // 查询应用信息
        AppInfo appInfo = appService.getAppInfoById(id);
        if (appInfo == null) {
            throw new IllegalArgumentException("应用不存在，ID: " + id);
        }
        
        return new BaseResponse<>(0, appInfo, "获取成功");
    }

    /**
     * 分页获取当前用户创建的应用列表
     *
     * @param request  请求
     * @return 应用列表
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AppInfo>> listMyAppVOByPage(@RequestBody(required = false) PageRequest pageRequest,
                                                         HttpServletRequest request) {
        // 设置默认分页参数
        if (pageRequest == null) {
            pageRequest = new PageRequest();
        }
        if (pageRequest.getPageNum() == null || pageRequest.getPageNum() <= 0) {
            pageRequest.setPageNum(1);
        }
        if (pageRequest.getPageSize() == null || pageRequest.getPageSize() <= 0) {
            pageRequest.setPageSize(10);
        }
        if (pageRequest.getPageSize() > 100) {
            pageRequest.setPageSize(100);
        }
        
        // 获取当前登录用户
        String userId = request.getHeader("userId") == null ? "admin" : request.getHeader("userId");
        
        // 调用服务分页查询应用列表
        Page<AppInfo> page = appService.listMyAppVOByPage(userId, pageRequest.getPageNum(), pageRequest.getPageSize());
        
        return new BaseResponse<>(0, page, "获取成功");
    }

    /**
     * 分页查询某个应用的对话历史（游标查询）
     *
     * @param appId          应用ID
     * @param pageSize       页面大小
     * @param lastCreateTime 最后一条记录的创建时间
     * @param request        请求
     * @return 对话历史分页
     */
    @GetMapping("/chatHistory/app/{appId}")
    public BaseResponse<Page<AppChatHistory>> listAppChatHistory(@PathVariable String appId,
                                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                                 @RequestParam(required = false) LocalDateTime lastCreateTime,
                                                                 HttpServletRequest request) {
        // 参数校验
        if (StrUtil.isBlank(appId)) {
            throw new IllegalArgumentException("应用ID不能为空");
        }
        if (pageSize <= 0 || pageSize > 100) {
            throw new IllegalArgumentException("页面大小必须在1-100之间");
        }
        
        // 获取当前登录用户
        String userId = request.getHeader("userId") == null ? "admin" : request.getHeader("userId");
        
        // 调用服务分页查询对话历史
        Page<AppChatHistory> page = appService.listAppChatHistory(appId, userId, pageSize, lastCreateTime);
        
        return new BaseResponse<>(0, page, "获取成功");
    }
    @Data
    public static class CreateAppRequest {
        private String appName;
        private String initPrompt;
    }

    @Data
    public static class PageRequest {
        private Integer pageNum;
        private Integer pageSize;
    }

    // 应用生成根目录（用于浏览）
    private static final String PREVIEW_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;

    /**
     * 提供静态资源访问，支持目录重定向
     * 访问格式：http://localhost:8001/ai-meeting/api/app/static/{deployKey}[/{fileName}]
     */
    @GetMapping("/static/{deployKey}/**")
    public ResponseEntity<Resource> serveStaticResource(
            @PathVariable String deployKey,
            HttpServletRequest request) {
        try {
            // 获取资源路径
            String resourcePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            resourcePath = resourcePath.substring(("/api/app/static/" + deployKey).length());
            // 如果是目录访问（不带斜杠），重定向到带斜杠的URL
            if (resourcePath.isEmpty()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Location", request.getRequestURI() + "/");
                return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
            }
            // 默认返回 index.html
            if (resourcePath.endsWith("/")) {
                resourcePath = "/index.html";
            }
            // 构建文件路径
            String filePath = PREVIEW_ROOT_DIR + "/" + deployKey + resourcePath;
            File file = new File(filePath);
            // 检查文件是否存在
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            // 返回文件资源
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header("Content-Type", getContentTypeWithCharset(filePath))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据文件扩展名返回带字符编码的 Content-Type
     */
    private String getContentTypeWithCharset(String filePath) {
        if (filePath.endsWith(".html")) return "text/html; charset=UTF-8";
        if (filePath.endsWith(".css")) return "text/css; charset=UTF-8";
        if (filePath.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".jpg")) return "image/jpeg";
        return "application/octet-stream";
    }

    public static void main(String[] args) {
        String str="http://localhost:8001/ai-meeting/api/app/static/1/style.css";
        System.out.println(str.substring(("/static/" + "1").length()));
    }
}