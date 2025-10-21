# MCP API 文档

## 概述

本文档描述了MCP(Model Context Protocol)服务的API接口，用于提供上下文信息查询功能。该服务基于Spring Boot框架开发，提供了RESTful API接口。

## 服务基础信息

- 服务地址: `http://localhost:8002/ai-mcp`
- API路径前缀: `/mcp`
- 通信协议: HTTP/JSON

## API接口详情

### 1. 查询上下文信息

#### 接口地址
```
POST /mcp/query
```

#### 请求头
```
Content-Type: application/json
```

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| version | string | 否 | 版本号 |
| sources | array | 否 | 数据源列表 |
| query | object | 否 | 查询参数 |
| context | object | 否 | 上下文信息 |

#### ContextSource对象结构
```json
{
  "name": "数据源名称",
  "uri": "数据源地址",
  "type": "数据源类型(database, api, file等)",
  "description": "数据源描述"
}
```

#### 响应参数
| 参数名 | 类型 | 说明 |
|-------|------|------|
| version | string | 版本号 |
| sources | array | 数据源列表 |
| data | object | 返回的数据 |
| status | string | 状态(success, error) |
| message | string | 消息说明 |

### 2. 特殊查询支持

#### 2.1 根据用户ID查询用户信息
在query对象中添加`getUserById`参数：
```json
{
  "query": {
    "getUserById": "用户ID"
  }
}
```

返回示例：
```json
{
  "version": "1.0",
  "status": "success",
  "message": "Request processed successfully",
  "data": {
    "user": {
      "id": "用户ID",
      "name": "John Doe",
      "email": "john.doe@example.com"
    }
  }
}
```

#### 2.2 查询产品列表
在query对象中添加`getProductList`参数：
```json
{
  "query": {
    "getProductList": true
  }
}
```

返回示例：
```json
{
  "version": "1.0",
  "status": "success",
  "message": "Request processed successfully",
  "data": {
    "products": [
      {
        "id": 1,
        "name": "Product 1",
        "price": 99.99
      },
      {
        "id": 2,
        "name": "Product 2",
        "price": 149.99
      }
    ]
  }
}
```

## 请求示例

### 示例1: 查询用户信息
```bash
curl -X POST http://localhost:8002/ai-mcp/mcp/query \
  -H "Content-Type: application/json" \
  -d '{
    "version": "1.0",
    "query": {
      "getUserById": "123"
    },
    "sources": []
  }'
```

### 示例2: 查询产品列表
```bash
curl -X POST http://localhost:8002/ai-mcp/mcp/query \
  -H "Content-Type: application/json" \
  -d '{
    "version": "1.0",
    "query": {
      "getProductList": true
    },
    "sources": []
  }'
```

### 示例3: 无特定查询
```bash
curl -X POST http://localhost:8002/ai-mcp/mcp/query \
  -H "Content-Type: application/json" \
  -d '{
    "version": "1.0",
    "query": {},
    "sources": []
  }'
```

## 响应示例

### 成功响应
```json
{
  "version": "1.0",
  "sources": [],
  "data": {
    "message": "No specific query provided. This is sample data.",
    "timestamp": 1700000000000
  },
  "status": "success",
  "message": "Request processed successfully"
}
```

### 错误响应
```json
{
  "version": "1.0",
  "sources": null,
  "data": null,
  "status": "error",
  "message": "Error processing request: 错误信息"
}
```

## 错误码说明

| HTTP状态码 | 错误信息 | 说明 |
|-----------|---------|------|
| 200 | success | 请求成功 |
| 400 | Bad Request | 请求参数错误 |
| 500 | Internal Server Error | 服务器内部错误 |

## 注意事项

1. 所有请求和响应数据均使用JSON格式
2. 时间戳以毫秒为单位
3. 当前为演示版本，实际数据查询逻辑需要根据业务需求进行扩展
4. 支持跨域请求