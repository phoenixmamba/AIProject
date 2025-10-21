# MCP测试API文档

## 概述

本文档描述了MCP测试服务的API接口，用于前端调用以测试AI调用MCP服务的功能。该服务基于Spring Boot框架开发，提供了RESTful API接口，支持服务端事件流（Server-Sent Events）方式返回AI生成的内容。

## 服务基础信息

- 服务地址: `http://localhost:8001/ai-meeting`
- API路径前缀: `/api/mcp-test`
- 通信协议: HTTP/JSON

## API接口详情

### 1. 测试查询用户信息

#### 接口地址
```
POST /api/mcp-test/query-user
```

#### 请求头
```
Content-Type: application/x-www-form-urlencoded
```

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| userId | string | 是 | 用户ID |

#### 响应格式
服务端事件流（Server-Sent Events），每条消息包含AI生成的内容片段。

#### 请求示例
```bash
curl -X POST http://localhost:8001/ai-meeting/api/mcp-test/query-user \
  -d "userId=123"
```

### 2. 测试查询产品列表

#### 接口地址
```
POST /api/mcp-test/query-products
```

#### 请求头
```
Content-Type: application/x-www-form-urlencoded
```

#### 请求参数
无

#### 响应格式
服务端事件流（Server-Sent Events），每条消息包含AI生成的内容片段。

#### 请求示例
```bash
curl -X POST http://localhost:8001/ai-meeting/api/mcp-test/query-products
```

### 3. 自定义查询

#### 接口地址
```
POST /api/mcp-test/custom-query
```

#### 请求头
```
Content-Type: application/x-www-form-urlencoded
```

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| queryType | string | 是 | 查询类型 |
| queryParam | string | 是 | 查询参数 |

#### 响应格式
服务端事件流（Server-Sent Events），每条消息包含AI生成的内容片段。

#### 请求示例
```bash
curl -X POST http://localhost:8001/ai-meeting/api/mcp-test/custom-query \
  -d "queryType=getUserById&queryParam=456"
```

## 响应格式说明

所有接口均返回服务端事件流（Server-Sent Events），前端可以通过EventSource API接收流式数据。

### 前端使用示例

```javascript
// 查询用户信息示例
const eventSource = new EventSource('http://localhost:8080/api/mcp-test/query-user?userId=123');

eventSource.onmessage = function(event) {
    const data = event.data;
    console.log('收到数据:', data);
    // 处理接收到的数据片段
};

eventSource.onerror = function(event) {
    console.error('连接出错:', event);
    eventSource.close();
};
```

## 调用流程说明

1. 前端调用上述任一接口
2. 后端服务接收请求并构造AI提示词
3. AI服务调用MCP查询工具
4. MCP工具向本地MCP服务（http://localhost:8002/ai-mcp/mcp/query）发送HTTP请求
5. 本地MCP服务处理请求并返回结果
6. AI服务根据MCP服务返回的结果生成自然语言响应
7. 后端服务以流式方式将AI生成的内容返回给前端

## 注意事项

1. 所有接口均采用流式响应，前端需要使用EventSource或类似技术处理
2. 确保本地MCP服务（http://localhost:8002/ai-mcp）正在运行
3. 确保网络连接正常，能够访问本地MCP服务
4. 如果出现错误，会在响应流中返回错误信息